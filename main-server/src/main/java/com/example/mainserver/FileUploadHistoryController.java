package com.example.mainserver;

import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequiredArgsConstructor
public class FileUploadHistoryController {

    private final FileUploadHistoryRepository repository;

    @PostMapping("/upload")
    public FileUploadHistory upload(MultipartFile file) {
        long startTime = System.currentTimeMillis();

        FileUploadHistory history = repository.save(FileUploadHistory.builder()
                .status("대기")
                .name(file.getName())
                .build());

        WebClient webClient = WebClient.create("http://localhost:7070");
        Mono<String> result = webClient
                .post()
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(file)))
                .retrieve()
                .bodyToMono(String.class);

        // WebFlux 서버의 response를 받으면 subscribe의 callback 함수가 실행된다.
        result
                .subscribe(
                success -> {
                    long transactionEndTime = System.currentTimeMillis();
                    System.out.println("transaction 반환시간 : " + (transactionEndTime - startTime) / 1000 + "초");
                    repository.save(history.success());
                },
                error -> {
                    repository.save(history.fail());
                    error.printStackTrace();
                }
        );

        return history;
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(MultipartFile file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        // Excel 파일을 저장할 로컬 Path
        String uploadedPath = file.getOriginalFilename();

        File convFile = new File(uploadedPath);
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.part("file", uploadedPath);
        return builder.build();
    }



}
