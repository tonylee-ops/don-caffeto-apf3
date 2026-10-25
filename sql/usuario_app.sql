-- =====================================================================
--  Usuario de la aplicación con permisos mínimos.
--  Ejecutar como root DESPUÉS de don_caffeto.sql.
--  Cambie 'ESCRIBA_SU_CLAVE' por una clave propia y escriba la misma
--  en el archivo config.properties (ese archivo NO se sube a GitHub).
--  EXECUTE permite usar las funciones fn_... desde la aplicación.
-- =====================================================================
CREATE USER IF NOT EXISTS 'app_doncaffeto'@'localhost' IDENTIFIED BY 'ESCRIBA_SU_CLAVE';
GRANT SELECT, INSERT, UPDATE, EXECUTE ON don_caffeto.* TO 'app_doncaffeto'@'localhost';
FLUSH PRIVILEGES;
