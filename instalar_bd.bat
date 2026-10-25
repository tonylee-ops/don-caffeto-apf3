@echo off
chcp 65001 >nul
cd /d "%~dp0"
set "MYSQL="
for /d %%D in ("C:\Program Files\MySQL\MySQL Server *") do if exist "%%D\bin\mysql.exe" set "MYSQL=%%D\bin\mysql.exe"
if not defined MYSQL (
  echo No se encontro mysql.exe en C:\Program Files\MySQL
  pause
  exit /b 1
)
echo Usando: %MYSQL%
echo Se creara la base don_caffeto_apf3 y el usuario app_apf3.
echo Escribe la contrasena de root de MySQL:
"%MYSQL%" -u root -p --default-character-set=utf8mb4 < "sql\don_caffeto.sql"
if errorlevel 1 (
  echo.
  echo ERROR: revisa la contrasena de root y vuelve a intentarlo.
) else (
  echo.
  echo LISTO. Ahora abre el proyecto en NetBeans y ejecutalo.
)
pause
