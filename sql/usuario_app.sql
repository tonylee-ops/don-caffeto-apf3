CREATE USER IF NOT EXISTS 'app_doncaffeto'@'localhost' IDENTIFIED BY 'ESCRIBA_SU_CLAVE';
GRANT SELECT, INSERT, UPDATE, EXECUTE ON don_caffeto.* TO 'app_doncaffeto'@'localhost';
FLUSH PRIVILEGES;
