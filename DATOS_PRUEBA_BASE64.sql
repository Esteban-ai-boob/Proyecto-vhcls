-- ============================================
-- SCRIPT DE PRUEBA: Carga de Documentos y Base64
-- ============================================
-- Este script inserta datos de prueba para demostrar
-- la funcionalidad de carga de documentos con Base64
-- y envío de correos de notificación

USE ppooii;

-- ============================================
-- 1. INSERTAR DOCUMENTOS PARAMETRIZADOS
-- ============================================

INSERT INTO documents (document_code, document_name, vehicle_type_applicability, mandatory_flag, description)
VALUES 
  ('LIC-001', 'Licencia de Conducción', 'A', 'RA', 'Licencia de conducción tipo A'),
  ('SOAT-001', 'Certificado SOAT', 'A', 'RM', 'Seguro obligatorio de accidentes de tránsito'),
  ('TEC-001', 'Certificado Técnico', 'A', 'RA', 'Certificado técnico-mecánico'),
  ('CONT-001', 'Contrato de Responsabilidad', 'A', 'RR', 'Contrato de responsabilidad civil');

-- ============================================
-- 2. INSERTAR VEHÍCULOS DE PRUEBA
-- ============================================

INSERT INTO vehicles (vehicle_type, license_plate, service_type, fuel_type, passenger_capacity, color, model_year, brand, line)
VALUES 
  ('A', 'ABC-1234', 'PUBLICO', 'GASOLINA', 5, 'ROJO', 2023, 'Toyota', 'Corolla'),
  ('A', 'XYZ-5678', 'PRIVADO', 'DIESEL', 7, 'BLANCO', 2022, 'Nissan', 'Qashqai');

-- ============================================
-- 3. ASOCIAR DOCUMENTOS A VEHÍCULOS
-- ============================================

INSERT INTO vehicle_documents (vehicle_id, document_id, issuance_date, expiration_date, document_status)
VALUES 
  (1, 1, '2024-01-15', '2026-01-15', 'EN_VERIFICACION'),
  (1, 2, '2024-01-20', '2025-01-20', 'EN_VERIFICACION'),
  (2, 1, '2024-02-10', '2026-02-10', 'HABILITADO'),
  (2, 3, '2024-02-15', '2025-02-15', 'HABILITADO');

-- ============================================
-- 4. INSERTAR DOCUMENTOS CON BASE64 (EJEMPLO)
-- ============================================
-- Este es un ejemplo de PDF mínimo en Base64
-- Para pruebas reales, usa archivos reales convertidos a Base64

UPDATE vehicle_documents 
SET document_content = 'JVBERi0xLjQKJeLjz9MNCjEgMCBvYmoKPDwKL1R5cGUgL0NhdGFsb2cKL1BhZ2VzIDIgMCBSCj4+CmVuZG9iajogMCBvYmoKPDwKL1R5cGUgL1BhZ2VzCi9LaWRzIFszIDAgUl0KL0NvdW50IDEKPj4KZW5kb2JqCjMgMCBvYmoKPDwKL1R5cGUgL1BhZ2UKL1BhcmVudCAyIDAgUgovUmVzb3VyY2VzIDwKL0ZvbnQgPDwKL0YxIDQgMCBSCj4+Cj4+Ci9NZWRpYUJveCBbMCAwIDYxMiA3OTJdCi9Db250ZW50cyA1IDAgUgo+PgplbmRvYmoK'
WHERE id = 1;

-- ============================================
-- 5. CONSULTAS DE VERIFICACIÓN
-- ============================================

-- Ver vehículos con documentos
SELECT 
  v.id,
  v.license_plate,
  v.vehicle_type,
  d.document_code,
  d.document_name,
  vd.document_status,
  CASE 
    WHEN vd.document_content IS NOT NULL THEN 'SÍ'
    ELSE 'NO'
  END as 'Tiene Base64'
FROM vehicles v
JOIN vehicle_documents vd ON v.id = vd.vehicle_id
JOIN documents d ON vd.document_id = d.id
ORDER BY v.license_plate, d.document_code;

-- Ver tamaño de contenido Base64 guardado
SELECT 
  vd.id,
  v.license_plate,
  d.document_name,
  LENGTH(vd.document_content) as 'Tamaño Base64 (caracteres)',
  LEFT(vd.document_content, 50) as 'Primeros 50 caracteres'
FROM vehicle_documents vd
JOIN vehicles v ON vd.vehicle_id = v.id
JOIN documents d ON vd.document_id = d.id
WHERE vd.document_content IS NOT NULL;

-- ============================================
-- 6. COMMANDS PARA PRUEBAS MANUALES
-- ============================================

-- Ver todos los documentos parametrizados
-- SELECT * FROM documents;

-- Ver todos los vehículos
-- SELECT * FROM vehicles;

-- Ver todas las asociaciones de documentos
-- SELECT * FROM vehicle_documents;

-- Borrar datos de prueba (si es necesario)
-- DELETE FROM vehicle_documents WHERE id > 0;
-- DELETE FROM vehicles WHERE id > 0;
-- DELETE FROM documents WHERE id > 0;

-- ============================================
-- 7. NOTAS IMPORTANTES
-- ============================================
/*
NOTAS:
------

1. El campo 'document_content' almacena archivos en Base64
   - Cuando se carga un documento, se guarda codificado aquí
   - Para recuperar el archivo original, decodificar desde Base64

2. Para pruebas con correo:
   - Asegúrate de tener credenciales SMTP configuradas en application.properties
   - Los correos se envían si se especifica 'notificationEmail' en la carga

3. Códigos de estado de documentos:
   - HABILITADO: Documento válido y vigente
   - VENCIDO: Documento expirado
   - EN_VERIFICACION: Documento en proceso de validación

4. Campos de vehículos:
   - vehicle_type: A (Automóvil), M (Motocicleta), AM (Motocicleta A)
   - service_type: PUBLICO, PRIVADO
   - fuel_type: GASOLINA, DIESEL, ELECTRICO, etc.

5. Para generar Base64:
   - Linux: base64 archivo.pdf
   - Windows: [Convert]::ToBase64String([IO.File]::ReadAllBytes("archivo.pdf"))
   - Online: https://www.base64encode.org/
*/
