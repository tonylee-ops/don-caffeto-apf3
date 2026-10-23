-- =====================================================================
--  Usuario de la aplicación con permisos mínimos.
--  Ejecutar como root DESPUÉS de don_caffeto.sql.
--  Cambie 'ESCRIBA_SU_CLAVE' por una clave propia y escriba la misma
--  en el archivo config.properties (ese archivo NO se sube a GitHub).
-- =====================================================================
CREATE USER IF NOT EXISTS 'app_doncaffeto'@'localhost' IDENTIFIED BY 'ESCRIBA_SU_CLAVE';
GRANT SELECT, INSERT, UPDATE ON don_caffeto.* TO 'app_doncaffeto'@'localhost';
FLUSH PRIVILEGES;
