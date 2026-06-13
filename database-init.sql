USE ppooii;

INSERT IGNORE INTO persona (ID, PNOMBRE, UBICACION) VALUES
(1, 'Andres', 'Ibague, Tolima, Colombia'),
(2, 'Camila', 'Ibague, Tolima, Colombia'),
(3, 'Valentina', 'Ibague, Tolima, Colombia'),
(4, 'Santiago', 'Ibague, Tolima, Colombia'),
(5, 'Mariana', 'Ibague, Tolima, Colombia');

INSERT IGNORE INTO coordenadas (persona, marca, longitud, latitud) VALUES
(1, 'Andres', -75.2322, 4.4389),
(2, 'Camila', -75.2255, 4.4419),
(3, 'Valentina', -75.2376, 4.4358),
(4, 'Santiago', -75.2294, 4.4462),
(5, 'Mariana', -75.2408, 4.4321);
