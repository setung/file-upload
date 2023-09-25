package com.example.uploadserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CardBinNumber {

    @Id
    @GeneratedValue
    private Long id;

    private String issuingCompanyName;

    private String code;

    private String cardName;

}
