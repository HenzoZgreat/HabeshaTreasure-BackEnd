package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelebirrTransactionDetails {
    private String transactionId;
    private String payerName;
    private String creditedParty;
    private String status;
    private String bankReference;
    private String date;
    private double amount;
}

