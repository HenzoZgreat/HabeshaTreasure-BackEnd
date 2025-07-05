package com.HabeshaTreasure.HabeshaTreasure.DTO;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String email;
    private String password;
    private String role;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private String country;
    private String region;
}
