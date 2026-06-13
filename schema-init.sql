CREATE DATABASE IF NOT EXISTS ppooii DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ppooii;

CREATE TABLE IF NOT EXISTS persona (
  ID bigint NOT NULL,
  PNOMBRE varchar(100) NOT NULL,
  UBICACION varchar(255) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS coordenadas (
  id_coordenada bigint NOT NULL AUTO_INCREMENT,
  persona bigint NOT NULL,
  marca varchar(100) NOT NULL,
  longitud double NOT NULL,
  latitud double NOT NULL,
  PRIMARY KEY (id_coordenada),
  KEY coordenadas_persona_FK (persona),
  CONSTRAINT coordenadas_persona_FK FOREIGN KEY (persona) REFERENCES persona (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO persona (ID, PNOMBRE, UBICACION) VALUES
(1, 'Andres', 'Ibague, Tolima, Colombia'),
(2, 'Camila', 'Ibague, Tolima, Colombia'),
(3, 'Valentina', 'Ibague, Tolima, Colombia'),
(4, 'Santiago', 'Ibague, Tolima, Colombia'),
(5, 'Mariana', 'Ibague, Tolima, Colombia');
