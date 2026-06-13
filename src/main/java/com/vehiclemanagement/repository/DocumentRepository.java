package com.vehiclemanagement.repository;

import com.vehiclemanagement.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad Document.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Busca un documento por su código.
     */
    Optional<Document> findByDocumentCode(String documentCode);

    /**
     * Verifica si existe un documento con un código específico.
     */
    boolean existsByDocumentCode(String documentCode);
}
