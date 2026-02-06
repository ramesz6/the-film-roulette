-- Deprecated: this patch is now applied by the application at startup
-- (see com.gyt.thefilmroulette.configurations.UserListEntryStatusConstraintPatcher).
--
-- We keep this file in the repo as documentation, but Spring SQL init is disabled for Postgres.

-- Normalize legacy/invalid values before adding the CHECK constraint.
-- Without this, ADD CONSTRAINT would fail if any rows have unexpected status values.

