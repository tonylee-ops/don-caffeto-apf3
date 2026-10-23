-- Usuario de la aplicación con permisos mínimos (ejecutar como root después de don_caffeto.sql)
CREATE USER IF NOT EXISTS 'app_doncaffeto'@'localhost' IDENTIFIED BY 'CambieEstaClave_2026';
GRANT SELECT, INSERT, UPDATE, EXECUTE ON don_caffeto.* TO 'app_doncaffeto'@'localhost';
FLUSH PRIVILEGES;
