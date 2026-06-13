package com.vehiclemanagement.service;

import com.vehiclemanagement.dto.DocumentDTO;
import com.vehiclemanagement.entity.Document;

import java.util.List;

public interface IDocumentService {

    Document createDocument(DocumentDTO documentDTO);

    Document getDocumentById(long id);

    List<Document> getAllDocuments();

    Document updateDocument(long id, DocumentDTO documentDTO);

    void deleteDocument(long id);

    Document getDocumentByCode(String documentCode);
}
