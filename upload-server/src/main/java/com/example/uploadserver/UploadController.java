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

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final CardBinNumberRepository repository;

    private final CardBinFileReader reader;

    @PostMapping("/upload")
    public String upload(HttpServletRequest request) throws FileNotFoundException {
        List<CardBinNumber> cardBinNumbers = reader.readXlsx(new FileInputStream("card_bin_6.xlsx"));
        repository.saveAll(cardBinNumbers);

        return "ok";
    }
}
