-- Backward-compatible fix: older databases were created before ListStatus.DISLIKED existed.
-- Hibernate doesn't reliably update enum CHECK constraints, so we patch it here.
--
-- NOTE: Spring's SQL initializer splits on semicolons, so avoid Postgres DO $$ blocks.

-- Normalize legacy/invalid values before adding the CHECK constraint.
-- Without this, ADD CONSTRAINT would fail if any rows have unexpected status values.
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
   OR upper(btrim(status)) NOT IN ('WATCH_LATER', 'SEEN', 'DISLIKED');

ALTER TABLE IF EXISTS public.user_list_entry
  DROP CONSTRAINT IF EXISTS user_list_entry_status_check;

ALTER TABLE IF EXISTS public.user_list_entry
  ADD CONSTRAINT user_list_entry_status_check
  CHECK (status IN ('WATCH_LATER', 'SEEN', 'DISLIKED'));
