package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private String country;
    private String region;
}
