INSERT IGNORE INTO persona (
    id,
    pnombre,
    ubicacion,
    tipo_identificacion,
    numero_identificacion,
    tipo_persona,
    fecha_licencia
) VALUES
(1, 'Andres', 'Ibague, Tolima, Colombia', 'CC', '100000001', 'ADMINISTRATIVO', DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR)),
(2, 'Camila', 'Universidad de Ibague, Ibague, Tolima, Colombia', 'CC', '100000002', 'CONDUCTOR', DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR)),
(3, 'Valentina', 'Parque Murillo Toro, Ibague, Tolima, Colombia', 'CC', '100000003', 'CONDUCTOR', DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR)),
(4, 'Santiago', 'Terminal de Transportes de Ibague, Ibague, Tolima, Colombia', 'CC', '100000004', 'CONDUCTOR', DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY)),
(5, 'Mariana', 'Centro Comercial La Estacion, Ibague, Tolima, Colombia', 'CC', '100000005', 'CLIENTE', DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR));

UPDATE persona SET tipo_identificacion = 'CC', numero_identificacion = '100000001', tipo_persona = 'ADMINISTRATIVO', fecha_licencia = DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), ubicacion = 'Ibague, Tolima, Colombia' WHERE id = 1;
UPDATE persona SET tipo_identificacion = 'CC', numero_identificacion = '100000002', tipo_persona = 'CONDUCTOR', fecha_licencia = DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), ubicacion = 'Universidad de Ibague, Ibague, Tolima, Colombia' WHERE id = 2;
UPDATE persona SET tipo_identificacion = 'CC', numero_identificacion = '100000003', tipo_persona = 'CONDUCTOR', fecha_licencia = DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), ubicacion = 'Parque Murillo Toro, Ibague, Tolima, Colombia' WHERE id = 3;
UPDATE persona SET tipo_identificacion = 'CC', numero_identificacion = '100000004', tipo_persona = 'CONDUCTOR', fecha_licencia = DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), ubicacion = 'Terminal de Transportes de Ibague, Ibague, Tolima, Colombia' WHERE id = 4;
UPDATE persona SET tipo_identificacion = 'CC', numero_identificacion = '100000005', tipo_persona = 'CLIENTE', fecha_licencia = DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), ubicacion = 'Centro Comercial La Estacion, Ibague, Tolima, Colombia' WHERE id = 5;

INSERT INTO usuario (login, password, apikey, persona, persona_id)
SELECT 'demo', 'demo123', 'APIKEY-DEMO-123', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'demo');

UPDATE usuario
SET password = 'demo123', apikey = 'APIKEY-DEMO-123', persona = 1, persona_id = 1
WHERE login = 'demo';

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud)
SELECT 1, 'Andres', -75.2322, 4.4389
WHERE NOT EXISTS (SELECT 1 FROM coordenadas WHERE persona = 1);

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud)
SELECT 2, 'Camila', -75.2255, 4.4419
WHERE NOT EXISTS (SELECT 1 FROM coordenadas WHERE persona = 2);

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud)
SELECT 3, 'Valentina', -75.2376, 4.4358
WHERE NOT EXISTS (SELECT 1 FROM coordenadas WHERE persona = 3);

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud)
SELECT 4, 'Santiago', -75.2294, 4.4462
WHERE NOT EXISTS (SELECT 1 FROM coordenadas WHERE persona = 4);

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud)
SELECT 5, 'Mariana', -75.2408, 4.4321
WHERE NOT EXISTS (SELECT 1 FROM coordenadas WHERE persona = 5);

INSERT IGNORE INTO vehicles (
    id,
    vehicle_type,
    license_plate,
    service_type,
    fuel_type,
    passenger_capacity,
    color,
    model_year,
    brand,
    line
) VALUES
(1001, 'AUTOMOVIL', 'ABC123', 'PRIVADO', 'GASOLINA', 5, '#FF0000', 2023, 'Toyota', 'Corolla'),
(1002, 'MOTOCICLETA', 'XYZ12A', 'PRIVADO', 'GASOLINA', 2, '#0000FF', 2024, 'Yamaha', 'MT03'),
(1003, 'AUTOMOVIL', 'DEF456', 'PUBLICO', 'DIESEL', 4, '#00AA00', 2022, 'Kia', 'Rio');

INSERT INTO documents (
    id,
    document_code,
    document_name,
    vehicle_type_applicability,
    mandatory_flag,
    description
) VALUES
(9501, 'DEMO-SOAT', 'Seguro obligatorio SOAT demo', 'AM', 'RM', 'Documento obligatorio para operar'),
(9502, 'DEMO-TECNO', 'Revision tecnico mecanica demo', 'AM', 'RM', 'Revision tecnico mecanica vigente'),
(9503, 'DEMO-ANEXO', 'Anexo Base64 demo', 'AM', 'RR', 'Documento usado para demostrar Base64 y correo')
ON DUPLICATE KEY UPDATE
    document_name = VALUES(document_name),
    vehicle_type_applicability = VALUES(vehicle_type_applicability),
    mandatory_flag = VALUES(mandatory_flag),
    description = VALUES(description);

INSERT INTO vehiculo_documento (
    vehiculo_id,
    documento_id,
    fecha_expedicion,
    fecha_vencimiento,
    estado,
    contenido_documento,
    archivo_documento
)
SELECT 1001, 9501, DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH), DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), 'Habilitado',
       'UERGIGRlIGRlbW8gU09BVCBSVVRBLUlCRy0wMQ==',
       FROM_BASE64('UERGIGRlIGRlbW8gU09BVCBSVVRBLUlCRy0wMQ==')
WHERE NOT EXISTS (SELECT 1 FROM vehiculo_documento WHERE vehiculo_id = 1001 AND documento_id = 9501);

INSERT INTO vehiculo_documento (
    vehiculo_id,
    documento_id,
    fecha_expedicion,
    fecha_vencimiento,
    estado
)
SELECT 1001, 9502, DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH), DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR), 'Habilitado'
WHERE NOT EXISTS (SELECT 1 FROM vehiculo_documento WHERE vehiculo_id = 1001 AND documento_id = 9502);

INSERT INTO vehiculo_documento (
    vehiculo_id,
    documento_id,
    fecha_expedicion,
    fecha_vencimiento,
    estado
)
SELECT 1003, 9501, DATE_SUB(CURRENT_DATE(), INTERVAL 2 YEAR), DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), 'Vendido/Vencido'
WHERE NOT EXISTS (SELECT 1 FROM vehiculo_documento WHERE vehiculo_id = 1003 AND documento_id = 9501);

INSERT INTO conductor_vehiculo (persona_id, vehicle_id, estado)
SELECT 2, 1001, 'PO'
WHERE NOT EXISTS (
    SELECT 1 FROM conductor_vehiculo WHERE persona_id = 2 AND vehicle_id = 1001
);

INSERT INTO conductor_vehiculo (persona_id, vehicle_id, estado)
SELECT 3, 1002, 'PO'
WHERE NOT EXISTS (
    SELECT 1 FROM conductor_vehiculo WHERE persona_id = 3 AND vehicle_id = 1002
);

INSERT INTO conductor_vehiculo (persona_id, vehicle_id, estado)
SELECT 4, 1003, 'RO'
WHERE NOT EXISTS (
    SELECT 1 FROM conductor_vehiculo WHERE persona_id = 4 AND vehicle_id = 1003
);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-01', 1, 'Universidad de Ibague', 'Universidad de Ibague, Ibague, Tolima, Colombia', 4.4284, -75.2138, 1001, 1001, 2
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-01' AND orden = 1);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-01', 2, 'Parque Murillo Toro', 'Parque Murillo Toro, Ibague, Tolima, Colombia', 4.4389, -75.2322, 1001, 1001, 2
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-01' AND orden = 2);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-01', 3, 'Terminal de Transportes', 'Terminal de Transportes de Ibague, Ibague, Tolima, Colombia', 4.4471, -75.2409, 1001, 1001, 2
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-01' AND orden = 3);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-01', 4, 'La Estacion', 'Centro Comercial La Estacion, Ibague, Tolima, Colombia', 4.4486, -75.2157, 1001, 1001, 2
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-01' AND orden = 4);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-01', 5, 'Plaza de Bolivar', 'Plaza de Bolivar, Ibague, Tolima, Colombia', 4.4424, -75.2429, 1001, 1001, 2
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-01' AND orden = 5);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-BLOCK', 1, 'Terminal bloqueada', 'Terminal de Transportes de Ibague, Ibague, Tolima, Colombia', 4.4471, -75.2409, 1003, 1003, 4
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-BLOCK' AND orden = 1);

INSERT INTO trayecto (
    codigo_ruta,
    orden,
    nombre_parada,
    ubicacion,
    latitud,
    longitud,
    vehicle_id,
    vehiculo_id,
    conductor_id
)
SELECT 'RUTA-IBG-BLOCK', 2, 'Centro bloqueado', 'Parque Murillo Toro, Ibague, Tolima, Colombia', 4.4389, -75.2322, 1003, 1003, 4
WHERE NOT EXISTS (SELECT 1 FROM trayecto WHERE codigo_ruta = 'RUTA-IBG-BLOCK' AND orden = 2);
