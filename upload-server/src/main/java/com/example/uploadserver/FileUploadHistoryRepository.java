package com.example.uploadserver;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadHistoryRepository extends JpaRepository<FileUploadHistory, Long> {
}
