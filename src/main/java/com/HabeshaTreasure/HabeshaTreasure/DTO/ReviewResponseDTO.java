package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long userId;
    private String reviewer;
    private int rating;
    private String comment;
    private LocalDateTime reviewedAt;
}
