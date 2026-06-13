@echo off
title Iniciar Servidor y Tunel Publico
echo ========================================================
echo  Iniciando Backend y creando Tunel de Internet...
echo ========================================================
echo.

REM 1. Iniciar el backend de Spring Boot en una nueva ventana
echo [+] Iniciando el servidor Spring Boot en segundo plano...
start "Backend Spring Boot" cmd /c "mvn spring-boot:run"

echo [+] Esperando 12 segundos a que el servidor se inicialice...
timeout /t 12 /nobreak >nul

echo.
echo ========================================================
echo  ¡TÚNEL ACTIVO! Copia la URL de abajo (terminada en .lhr.life)
echo  y pegala en el boton "Configurar API" de tu web en Vercel.
echo  Para cerrar el servidor y el tunel, cierra esta ventana.
echo ========================================================
echo.

REM 2. Ejecutar el tunel SSH
ssh -o StrictHostKeyChecking=no -R 80:localhost:9001 nokey@localhost.run

pause
