package com.vehiclemanagement.service;

import com.vehiclemanagement.dto.DocumentDTO;
import com.vehiclemanagement.entity.Document;
import com.vehiclemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement.exception.ValidationException;
import com.vehiclemanagement.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Servicio para la gestion de documentos parametrizados.
 */
@Service
@Transactional
public class DocumentService implements IDocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document createDocument(DocumentDTO documentDTO) {
        String documentCode = normalizeDocumentCode(documentDTO);
        if (documentCode != null && documentRepository.existsByDocumentCode(documentCode)) {
            throw new ValidationException("Ya existe un documento con el codigo: " + documentCode);
        }

        Document document = new Document();
        document.setDocumentCode(documentCode);
        document.setDocumentName(documentDTO.getDocumentName());
        document.setVehicleTypeApplicability(Document.normalizeApplicability(documentDTO.getVehicleTypeApplicability()));
        document.setMandatory(documentDTO.getMandatory());
        document.setDescription(documentDTO.getDescription());

        document.validateParametrizedFields();
        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public Document getDocumentById(long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document updateDocument(long id, DocumentDTO documentDTO) {
        Document document = getDocumentById(id);

        String documentCode = normalizeDocumentCode(documentDTO);
        if (documentCode != null
            && !documentCode.equals(document.getDocumentCode())
            && documentRepository.existsByDocumentCode(documentCode)) {
            throw new ValidationException("Ya existe un documento con el codigo: " + documentCode);
        }

        document.setDocumentCode(documentCode);
        document.setDocumentName(documentDTO.getDocumentName());
        document.setVehicleTypeApplicability(Document.normalizeApplicability(documentDTO.getVehicleTypeApplicability()));
        document.setMandatory(documentDTO.getMandatory());
        document.setDescription(documentDTO.getDescription());

        document.validateParametrizedFields();
        return documentRepository.save(document);
    }

    public void deleteDocument(long id) {
        Document document = getDocumentById(id);

        if (!document.getVehicleDocuments().isEmpty()) {
            throw new ValidationException(
                "No se puede eliminar el documento. Existen vehiculos asociados a este documento."
            );
        }

        documentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Document getDocumentByCode(String documentCode) {
        String normalizedDocumentCode = Objects.requireNonNull(documentCode, "documentCode no puede ser null");
        return documentRepository.findByDocumentCode(normalizedDocumentCode)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Documento no encontrado con codigo: " + normalizedDocumentCode
            ));
    }

    private String normalizeDocumentCode(DocumentDTO documentDTO) {
        if (documentDTO.getDocumentCode() == null || documentDTO.getDocumentCode().isBlank()) {
            return documentDTO.getDocumentName().trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        }
        return documentDTO.getDocumentCode().trim().toUpperCase();
    }
}
