package com.PPOOII.Laboratorio.Services.Interfaces;

import com.PPOOII.Laboratorio.Entities.TrayectoTmp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IExcelService {

    Map<String, Object> cargarExcel(MultipartFile file);

    List<TrayectoTmp> consultarCargue(Double idCargue);
}
