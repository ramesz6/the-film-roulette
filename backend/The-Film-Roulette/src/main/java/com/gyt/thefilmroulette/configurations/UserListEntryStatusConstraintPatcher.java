package com.gyt.thefilmroulette.configurations;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Backward-compatible DB patch for older Postgres databases created before ListStatus.DISLIKED existed.
 *
 * <p>We apply this patch at application startup (after JPA has initialized) instead of using schema.sql,
 * because Spring's SQL initializer can't safely express a "run only if table exists" patch without
 * PL/pgSQL blocks.
 */
@Component
public class UserListEntryStatusConstraintPatcher implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(UserListEntryStatusConstraintPatcher.class);

  private final DataSource dataSource;
  private final JdbcTemplate jdbcTemplate;
  private final TransactionTemplate transactionTemplate;

  public UserListEntryStatusConstraintPatcher(
      DataSource dataSource,
      JdbcTemplate jdbcTemplate,
      PlatformTransactionManager transactionManager) {
    this.dataSource = dataSource;
    this.jdbcTemplate = jdbcTemplate;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (!isPostgres(dataSource)) {
      return;
    }

    Boolean tableExists =
        jdbcTemplate.queryForObject(
            "select to_regclass('public.user_list_entry') is not null", Boolean.class);

    if (!Boolean.TRUE.equals(tableExists)) {
      log.info("DB patch skipped: public.user_list_entry does not exist");
      return;
    }

    transactionTemplate.executeWithoutResult(
        status -> {
          int updated =
              jdbcTemplate.update(
                  """
                  UPDATE public.user_list_entry
                  SET status = CASE
                    WHEN status IS NULL THEN 'WATCH_LATER'
                    WHEN upper(btrim(status)) IN ('WATCH_LATER', 'SEEN', 'DISLIKED')
                      THEN upper(btrim(status))
                    ELSE 'WATCH_LATER'
                  END
                  WHERE status IS NULL
                     OR status <> upper(btrim(status))
                     OR btrim(status) <> status
                     OR upper(btrim(status)) NOT IN ('WATCH_LATER', 'SEEN', 'DISLIKED')
                  """);

          jdbcTemplate.execute(
              "ALTER TABLE public.user_list_entry DROP CONSTRAINT IF EXISTS user_list_entry_status_check");
          jdbcTemplate.execute(
              """
              ALTER TABLE public.user_list_entry
                ADD CONSTRAINT user_list_entry_status_check
                CHECK (status IN ('WATCH_LATER', 'SEEN', 'DISLIKED'))
              """);

          if (updated > 0) {
            log.info("DB patch applied: normalized {} user_list_entry rows", updated);
          } else {
            log.info("DB patch applied: no rows needed normalization");
          }
        });
  }

  private static boolean isPostgres(DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      DatabaseMetaData meta = conn.getMetaData();
      String product = meta.getDatabaseProductName();
      return product != null && product.toLowerCase().contains("postgres");
    } catch (Exception e) {
      // Fail safe: if we can't identify the DB, don't run a Postgres-specific patch.
      return false;
    }
  }
}
