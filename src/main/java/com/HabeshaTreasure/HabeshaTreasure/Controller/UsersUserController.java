package com.HabeshaTreasure.HabeshaTreasure.Controller;

import com.HabeshaTreasure.HabeshaTreasure.DTO.UserProfileDTO;
import com.HabeshaTreasure.HabeshaTreasure.DTO.UserProfileUpdateDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Entity.UsersInfo;
import com.HabeshaTreasure.HabeshaTreasure.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UsersUserController {

    @Autowired
    UserService service;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal User user) {
        UsersInfo info = user.getUsersInfo();
        return ResponseEntity.ok(new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                info.getFirstName(),
                info.getLastName(),
                info.getPhoneNumber(),
                info.getCity(),
                info.getCountry(),
                info.getRegion(),
                info.getJoined()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal User user,
                                             @RequestBody UserProfileUpdateDTO dto) {
        service.updateProfile(user, dto);
        return ResponseEntity.ok("Profile updated");
    }

}
