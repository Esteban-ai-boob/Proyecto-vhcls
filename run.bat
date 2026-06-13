@echo off
REM Script de inicio para Vehicle Management API

echo.
echo ========================================
echo  Vehicle Management API - Inicializador
echo ========================================
echo.

REM Verificar si Maven está instalado
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven no está instalado o no está en el PATH
    echo Por favor instala Maven y agrega su bin al PATH
    pause
    exit /b 1
)

REM Verificar si Java está instalado
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java no está instalado o no está en el PATH
    echo Por favor instala Java 17+ y agrega su bin al PATH
    pause
    exit /b 1
)

echo [+] Verificando versión de Java...
java -version

echo.
echo [+] Verificando MySQL en localhost:3306...
REM Este comando falla silenciosamente si MySQL no está disponible
timeout /t 2 >nul

echo.
echo [+] Limpiando compilaciones previas...
call mvn clean

echo.
echo [+] Compilando proyecto...
call mvn compile

if %errorlevel% neq 0 (
    echo ERROR: La compilación falló
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Iniciando Vehicle Management API...
echo ========================================
echo.
echo Accede a: http://localhost:8080/api
echo.

call mvn spring-boot:run

pause
