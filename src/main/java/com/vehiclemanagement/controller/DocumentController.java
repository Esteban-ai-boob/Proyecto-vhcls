package com.vehiclemanagement.controller;

import com.vehiclemanagement.dto.DocumentDTO;
import com.vehiclemanagement.entity.Document;
import com.vehiclemanagement.service.IDocumentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "4. Gestión de Documentos", description = "Operaciones CRUD para Documentos")
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final IDocumentService documentService;

    public DocumentController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@Valid @RequestBody DocumentDTO documentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.createDocument(documentDTO));
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(
        @PathVariable long id,
        @Valid @RequestBody DocumentDTO documentDTO
    ) {
        return ResponseEntity.ok(documentService.updateDocument(id, documentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
