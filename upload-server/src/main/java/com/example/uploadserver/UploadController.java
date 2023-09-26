package com.example.uploadserver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final CardBinNumberRepository cardBinNumberRepository;

    private final FileUploadHistoryRepository fileUploadHistoryRepository;

    private final CardBinFileReader reader;

    @PostMapping("/upload")
    public String upload(HttpServletRequest request) throws FileNotFoundException {
        //List<CardBinNumber> cardBinNumbers = reader.readXlsx(new FileInputStream("card_bin_12.xlsx"));
        //cardBinNumberRepository.saveAll(cardBinNumbers);

        return "ok";
    }


    @PostMapping("/upload2")
    public FileUploadHistory upload2() throws FileNotFoundException {
        File file = new File("card_bin_12.xlsx");
        FileUploadHistory fileUploadHistory = fileUploadHistoryRepository.save(FileUploadHistory.builder()
                .status("대기")
                .name(file.getName())
                .build());

        System.out.println(fileUploadHistory.getId() + "controller upload start");

        CompletableFuture<List<CardBinNumber>> completableFuture = reader.readXlsx(new FileInputStream("card_bin_12.xlsx"), fileUploadHistory.getId() );
        completableFuture
                .thenAccept((list) -> {
                    System.out.println(fileUploadHistory.getId() + " callback read compete");
                    cardBinNumberRepository.saveAll(list);
                    fileUploadHistoryRepository.save(fileUploadHistory.success());
                })
                .exceptionally(error -> {
                    System.out.println(fileUploadHistory.getId() + "callback read error");
                    error.printStackTrace();
                    fileUploadHistoryRepository.save(fileUploadHistory.fail());
                    return null;
                });



        System.out.println(fileUploadHistory.getId() + "controller upload end");
        return fileUploadHistory;
    }


}
