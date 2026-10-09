-- Run ONCE, after the first successful start (Flyway has created the tables). Safe to re-run.
-- run-env.ps1 fixdb executes this against both schemas.
-- 1) entity CityPreferences maps municipalityRuralLogo but no Flyway migration adds it (ISSUE-015)
ALTER TABLE generic.eg_citypreferences ADD COLUMN IF NOT EXISTS municipalityrurallogo bigint;
ALTER TABLE state.eg_citypreferences   ADD COLUMN IF NOT EXISTS municipalityrurallogo bigint;
-- 2) demo passwords otherwise expire and force a password change
UPDATE state.eg_user   SET pwdexpirydate = now() + interval '10 years';
UPDATE generic.eg_user SET pwdexpirydate = now() + interval '10 years';
