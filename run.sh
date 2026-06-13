#!/bin/bash
# Script de inicio para Vehicle Management API (Linux/Mac)

echo ""
echo "========================================"
echo "  Vehicle Management API - Inicializador"
echo "========================================"
echo ""

# Verificar si Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven no está instalado"
    exit 1
fi

# Verificar si Java está instalado
if ! command -v java &> /dev/null; then
    echo "ERROR: Java no está instalado"
    exit 1
fi

echo "[+] Verificando versión de Java..."
java -version

echo ""
echo "[+] Limpiando compilaciones previas..."
mvn clean

echo ""
echo "[+] Compilando proyecto..."
mvn compile

if [ $? -ne 0 ]; then
    echo "ERROR: La compilación falló"
    exit 1
fi

echo ""
echo "========================================"
echo "  Iniciando Vehicle Management API..."
echo "========================================"
echo ""
echo "Accede a: http://localhost:8080/api"
echo ""

mvn spring-boot:run
