package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteRequestDTO {
    private List<Integer> ids;
}
