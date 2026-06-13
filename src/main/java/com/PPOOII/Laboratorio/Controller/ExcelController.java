package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Entities.TrayectoTmp;
import com.PPOOII.Laboratorio.Services.Interfaces.IExcelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final IExcelService excelService;

    public ExcelController(IExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(excelService.cargarExcel(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error interno", "trace", e.toString()));
        }
    }

    @GetMapping("/cargue/{idCargue}")
    public ResponseEntity<List<TrayectoTmp>> consultarCargue(@PathVariable Double idCargue) {
        return ResponseEntity.ok(excelService.consultarCargue(idCargue));
    }
}
