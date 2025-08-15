DO
$$
BEGIN
   IF NOT EXISTS (
       SELECT FROM pg_catalog.pg_roles WHERE rolname = 'neoflex_admin'
   ) THEN
      CREATE USER neoflex_admin WITH ENCRYPTED PASSWORD 'qadminwerty';
END IF;
END
$$;

DO
$$
BEGIN
   IF NOT EXISTS (
       SELECT FROM pg_database WHERE datname = 'credit_services'
   ) THEN
      CREATE DATABASE credit_services OWNER neoflex_admin;
END IF;
END
$$;
