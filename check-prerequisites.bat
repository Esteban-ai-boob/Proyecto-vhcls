@echo off
REM Verificación de requisitos previos

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║         VERIFICADOR DE PREREQUISITOS               ║
echo ║      Vehicle Management API - MySQL Setup          ║
echo ╚════════════════════════════════════════════════════╝
echo.

set "errors=0"

REM Check Java
echo [*] Verificando Java 17+...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('java -version 2^>^&1') do set "java_version=%%i" & goto :java_done
    :java_done
    echo ✓ JAVA ENCONTRADO: %java_version%
) else (
    echo ✗ ERROR: Java no encontrado o no en PATH
    set "errors=1"
)
echo.

REM Check Maven
echo [*] Verificando Maven...
mvn -v >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MAVEN ENCONTRADO
) else (
    echo ✗ ERROR: Maven no encontrado o no en PATH
    set "errors=1"
)
echo.

REM Check MySQL
echo [*] Verificando MySQL...
mysql -u root -p mysqlroot -e "SELECT 1" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MYSQL ACCESIBLE (root:mysqlroot)
) else (
    echo ✗ ADVERTENCIA: No se puede conectar a MySQL
    echo   - Asegúrate de que MySQL esté corriendo: net start MySQL80
    echo   - Verifica la contraseña en application.properties
    set "errors=1"
)
echo.

REM Check database
echo [*] Verificando base de datos...
mysql -u root -p mysqlroot -e "USE vehicle_management; SELECT 1;" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ BASE DE DATOS CREADA
) else (
    echo ✗ ADVERTENCIA: BD 'vehicle_management' no existe
    echo   - Crea la BD con: CREATE DATABASE vehicle_management;
)
echo.

REM Results
echo ╔════════════════════════════════════════════════════╗
if %errors% equ 0 (
    echo ║              ✓ TODO LISTO PARA INICIAR            ║
    echo ╚════════════════════════════════════════════════════╝
    echo.
    echo Ejecuta: mvn spring-boot:run
    echo O: doble clic en run.bat
) else (
    echo ║        ⚠ REVISAR ERRORES ARRIBA ⬆               ║
    echo ╚════════════════════════════════════════════════════╝
)
echo.
pause
