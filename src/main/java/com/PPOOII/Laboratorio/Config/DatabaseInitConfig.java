package com.PPOOII.Laboratorio.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseInitConfig {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initStoredProcedures() {
        try {
            jdbcTemplate.execute("ALTER TABLE ppooii.trayecto_tmp DROP CONSTRAINT chk_trayecto_tmp_estado");
        } catch (org.springframework.dao.DataAccessException ignored) {}
        try {
            jdbcTemplate.execute("ALTER TABLE ppooii.trayecto_tmp ADD CONSTRAINT chk_trayecto_tmp_estado CHECK (estado IN ('CARGADO', 'VALIDADO', 'PROCESADO', 'ERROR'))");
        } catch (org.springframework.dao.DataAccessException ignored) {}

        try {
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS ppooii.CARGAR_DATOS_EXCEL");
            jdbcTemplate.execute("""
                CREATE PROCEDURE ppooii.CARGAR_DATOS_EXCEL(
                    IN P_NOMBRE VARCHAR(100),
                    IN P_EDAD VARCHAR(3),
                    IN P_UBICACION VARCHAR(100),
                    IN P_ID_CARGUE DOUBLE
                )
                BEGIN
                    INSERT INTO ppooii.trayecto_tmp (nombre, edad, ubicacion, estado, observacion, id_cargue)
                    VALUES (P_NOMBRE, P_EDAD, P_UBICACION, 'CARGADO', 'Fila cargada inicialmente', P_ID_CARGUE);
                END
            """);

            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS ppooii.VALIDAR_DATOS_CARGUE");
            jdbcTemplate.execute("""
                CREATE PROCEDURE ppooii.VALIDAR_DATOS_CARGUE(
                    IN P_ID_CARGUE DOUBLE
                )
                BEGIN
                    DECLARE v_id BIGINT;
                    DECLARE v_edad VARCHAR(3);
                    DECLARE v_nombre VARCHAR(100);
                    DECLARE v_ubicacion VARCHAR(100);
                    DECLARE v_estado VARCHAR(10);
                    DECLARE v_observacion VARCHAR(500);
                    DECLARE done INT DEFAULT FALSE;
                    
                    DECLARE cur CURSOR FOR 
                        SELECT id, edad, nombre, ubicacion, estado 
                        FROM ppooii.trayecto_tmp 
                        WHERE id_cargue = P_ID_CARGUE AND estado = 'CARGADO';
                        
                    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

                    OPEN cur;

                    read_loop: LOOP
                        FETCH cur INTO v_id, v_edad, v_nombre, v_ubicacion, v_estado;
                        IF done THEN
                            LEAVE read_loop;
                        END IF;

                        SET v_estado = 'VALIDADO';
                        SET v_observacion = 'Validacion exitosa';

                        IF NOT v_edad REGEXP '^[0-9]+$' THEN
                            SET v_estado = 'ERROR';
                            SET v_observacion = 'La edad debe ser un numero valido';
                        ELSEIF CAST(v_edad AS UNSIGNED) < 0 THEN
                            SET v_estado = 'ERROR';
                            SET v_observacion = 'La edad no puede ser negativa';
                        END IF;

                        IF v_nombre IS NULL OR TRIM(v_nombre) = '' THEN
                            SET v_estado = 'ERROR';
                            SET v_observacion = CONCAT(v_observacion, ' | El nombre es obligatorio');
                        END IF;

                        IF v_ubicacion IS NULL OR TRIM(v_ubicacion) = '' THEN
                            SET v_estado = 'ERROR';
                            SET v_observacion = CONCAT(v_observacion, ' | La ubicacion es obligatoria');
                        END IF;

                        UPDATE ppooii.trayecto_tmp 
                        SET estado = v_estado, observacion = v_observacion 
                        WHERE id = v_id;
                    END LOOP;

                    CLOSE cur;
                END
            """);

            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS ppooii.PROCESAR_DATOS_EXCEL");
            jdbcTemplate.execute("""
                CREATE PROCEDURE ppooii.PROCESAR_DATOS_EXCEL(
                    IN P_ID_CARGUE DOUBLE
                )
                BEGIN
                    DECLARE v_id BIGINT;
                    DECLARE v_nombre VARCHAR(100);
                    DECLARE v_ubicacion VARCHAR(100);
                    DECLARE v_codigo_ruta VARCHAR(50);
                    DECLARE v_orden INT DEFAULT 1;
                    DECLARE done INT DEFAULT FALSE;
                    
                    DECLARE cur CURSOR FOR 
                        SELECT id, nombre, ubicacion 
                        FROM ppooii.trayecto_tmp 
                        WHERE id_cargue = P_ID_CARGUE AND estado = 'VALIDADO';
                        
                    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

                    SET v_codigo_ruta = CONCAT('RUTA-CARGUE-', CAST(P_ID_CARGUE AS UNSIGNED));

                    OPEN cur;

                    read_loop: LOOP
                        FETCH cur INTO v_id, v_nombre, v_ubicacion;
                        IF done THEN
                            LEAVE read_loop;
                        END IF;

                        INSERT INTO ppooii.trayecto (codigo_ruta, orden, nombre_parada, ubicacion, conductor_id, vehicle_id, vehiculo_id)
                        VALUES (v_codigo_ruta, v_orden, v_nombre, v_ubicacion, 2, 1001, 1001);

                        UPDATE ppooii.trayecto_tmp 
                        SET estado = 'PROCESADO', observacion = 'Procesado exitosamente' 
                        WHERE id = v_id;

                        SET v_orden = v_orden + 1;
                    END LOOP;

                    CLOSE cur;
                END
            """);
        } catch (org.springframework.dao.DataAccessException | IllegalStateException e) {
            System.err.println("Error initializing stored procedures: " + e.getMessage());
        }
    }
}
