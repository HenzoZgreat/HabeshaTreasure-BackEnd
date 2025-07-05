package com.HabeshaTreasure.HabeshaTreasure.DTO;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private String country;
    private String region;
    private boolean enabled;
    private LocalDateTime joined;
    private LocalDateTime lastLogin;
}
