package com.PPOOII.Laboratorio.Services;

import com.PPOOII.Laboratorio.Entities.TrayectoTmp;
import com.PPOOII.Laboratorio.Repository.IPersonaRepository;
import com.PPOOII.Laboratorio.Repository.TrayectoRepository;
import com.PPOOII.Laboratorio.Repository.TrayectoTmpRepository;
import com.PPOOII.Laboratorio.Services.Interfaces.IExcelService;
import com.PPOOII.Laboratorio.Entities.Trayecto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExcelService implements IExcelService {

    private final IPersonaRepository personaRepository;
    private final TrayectoTmpRepository trayectoTmpRepository;
    private final TrayectoRepository trayectoRepository;
    private final LaboratorioService laboratorioService;
    private final DataFormatter dataFormatter = new DataFormatter();

    public ExcelService(
        IPersonaRepository personaRepository,
        TrayectoTmpRepository trayectoTmpRepository,
        TrayectoRepository trayectoRepository,
        LaboratorioService laboratorioService
    ) {
        this.personaRepository = personaRepository;
        this.trayectoTmpRepository = trayectoTmpRepository;
        this.trayectoRepository = trayectoRepository;
        this.laboratorioService = laboratorioService;
    }

    @Override
    @Transactional
    public Map<String, Object> cargarExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar un archivo Excel con datos");
        }

        Double idCargue = Double.valueOf(System.currentTimeMillis());
        int filasLeidas = cargarFilasTemporales(file, idCargue);

        personaRepository.VALIDAR_DATOS_CARGUE(idCargue);
        personaRepository.PROCESAR_DATOS_EXCEL(idCargue);

        List<TrayectoTmp> resultado = trayectoTmpRepository.findByIdCargueOrderByIdAsc(idCargue);
        String codigoRuta = crearRutaDesdeCargue(idCargue);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCargue", idCargue.longValue());
        response.put("codigoRuta", codigoRuta);
        response.put("filasLeidas", filasLeidas);
        response.put("cargados", contarPorEstado(resultado, "CARGADO"));
        response.put("validados", contarPorEstado(resultado, "VALIDADO"));
        response.put("procesados", contarPorEstado(resultado, "PROCESADO"));
        response.put("errores", contarPorEstado(resultado, "ERROR"));
        response.put("resultado", resultado);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrayectoTmp> consultarCargue(Double idCargue) {
        return trayectoTmpRepository.findByIdCargueOrderByIdAsc(idCargue);
    }

    private int cargarFilasTemporales(MultipartFile file, Double idCargue) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int filasLeidas = 0;
            for (Row row : sheet) {
                if (row == null || isBlankRow(row)) {
                    continue;
                }
                if (row.getRowNum() == 0 && looksLikeHeader(row)) {
                    continue;
                }

                String pnombre = value(row.getCell(0));
                String edad = value(row.getCell(1));
                String ubicacion = value(row.getCell(2));
                personaRepository.CARGAR_DATOS_EXCEL(pnombre, edad, ubicacion, idCargue);
                filasLeidas++;
            }
            if (filasLeidas == 0) {
                throw new IllegalArgumentException("El archivo Excel no contiene filas de datos");
            }
            return filasLeidas;
        } catch (IOException ex) {
            throw new IllegalArgumentException("No fue posible leer el archivo Excel", ex);
        }
    }

    private boolean looksLikeHeader(Row row) {
        String firstCell = value(row.getCell(0)).toUpperCase(Locale.ROOT);
        return firstCell.contains("PNOMBRE") || firstCell.contains("NOMBRE");
    }

    private boolean isBlankRow(Row row) {
        for (int i = 0; i < 3; i++) {
            if (!value(row.getCell(i)).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String value(Cell cell) {
        return dataFormatter.formatCellValue(cell).trim();
    }

    private long contarPorEstado(List<TrayectoTmp> resultado, String estado) {
        return resultado.stream()
            .filter(item -> estado.equalsIgnoreCase(item.getEstado()))
            .count();
    }

    private String crearRutaDesdeCargue(Double idCargue) {
        String codigoRuta = "RUTA-CARGUE-" + idCargue.longValue();
        // El SP PROCESAR_DATOS_EXCEL ya inserta en la tabla trayecto.
        // Solo completamos coordenadas de los registros que el SP haya creado.
        completarCoordenadasRuta(codigoRuta);
        return codigoRuta;
    }

    private void completarCoordenadasRuta(String codigoRuta) {
        for (Trayecto trayecto : trayectoRepository.findByCodigoRutaOrder(codigoRuta)) {
            if (trayecto.getLatitud() == null || trayecto.getLongitud() == null) {
                laboratorioService.completarCoordenadas(trayecto);
                trayectoRepository.save(trayecto);
            }
        }
    }
}
