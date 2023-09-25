package com.example.mainserver;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadHistoryRepository extends JpaRepository<FileUploadHistory, Long> {
}
