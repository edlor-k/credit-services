CREATE USER neoflex_admin WITH ENCRYPTED PASSWORD 'qadminwerty';
CREATE DATABASE credit_services OWNER neoflex_admin;
GRANT ALL PRIVILEGES ON DATABASE credit_services TO neoflex_admin;