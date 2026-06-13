#!/usr/bin/env bash
# Script alternativo para Linux/Mac para limpiar y ejecutar

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$PROJECT_DIR"

echo "╔════════════════════════════════════════════════════╗"
echo "║   Vehicle Management API - Inicializador (Bash)   ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

# Check Java
echo "[*] Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java no instalado. Por favor instala Java 17+"
    exit 1
fi
java_version=$(java -version 2>&1 | head -1)
echo "✅ $java_version"
echo ""

# Check Maven
echo "[*] Verificando Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no instalado. Por favor instala Apache Maven"
    exit 1
fi
mvn_version=$(mvn -version 2>&1 | head -1)
echo "✅ $mvn_version"
echo ""

# Clean
echo "[*] Limpiando compilaciones previas..."
mvn clean -q
echo "✅ Limpieza completada"
echo ""

# Compile
echo "[*] Compilando proyecto..."
if mvn compile -q; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación"
    exit 1
fi
echo ""

echo "╔════════════════════════════════════════════════════╗"
echo "║         INICIANDO VEHICLE MANAGEMENT API           ║"
echo "╠════════════════════════════════════════════════════╣"
echo "║                                                    ║"
echo "║  API disponible en: http://localhost:8080/api     ║"
echo "║                                                    ║"
echo "║  Documentos: http://localhost:8080/api/documents  ║"
echo "║  Vehículos:  http://localhost:8080/api/vehicles   ║"
echo "║                                                    ║"
echo "╚════════════════════════════════════════════════════╝"
echo ""

mvn spring-boot:run
