package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.Entity.*;
import com.HabeshaTreasure.HabeshaTreasure.Repository.PasswordResetTokenRepository;
import com.HabeshaTreasure.HabeshaTreasure.Repository.UserRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.UsersInfoRepo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.VerificationTokenRepository;
import com.HabeshaTreasure.HabeshaTreasure.SecurityService.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private UsersInfoRepo usersInfoRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepo;
    @Autowired
    private NotificationService notificationService;



    public Map<String, String> registerUser(User givenUser) {
        // Encode password
        givenUser.setPassword(passwordEncoder.encode(givenUser.getPassword()));

        // Set up UsersInfo
        if (givenUser.getUsersInfo() != null) {
            givenUser.getUsersInfo().setUser(givenUser);
            LocalDateTime now = LocalDateTime.now();
            givenUser.getUsersInfo().setJoined(now);
            givenUser.getUsersInfo().setLastLogin(now);
            givenUser.getUsersInfo().setEnabled(false); // Disable until email is verified
        }

        userRepository.save(givenUser);

        notificationService.createNotification("New user registered: " + givenUser.getEmail(), NotificationType.USER, null);


        // Generate email verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, givenUser, LocalDateTime.now().plusMinutes(30));
        verificationTokenRepo.save(verificationToken);


        // Send verification email
        String link = "http://localhost:3000/verify-email?token=" + token;
        emailService.send(givenUser.getEmail(), "Verify Your Email", "Click to verify: " + link);



        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully. Please check your email to verify your account.");

        return response;
    }

    public Map<String, String> loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Update last login time
        UsersInfo info = user.getUsersInfo();
        info.setLastLogin(LocalDateTime.now());

        // Save update
        usersInfoRepository.save(info);

        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        ));

        // Return the token in a map
        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged in successfully");
        response.put("Role", user.getRole().toString());
        response.put("token", token); // Include the token in the response
        return response;
    }

    public void sendForgotPasswordEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Do not reveal that the user doesn't exist
            return;
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepo.save(resetToken);

        String link = "http://localhost:3000/reset-password?token=" + token;
        String subject = "Reset your password";
        String body = "Hi " + user.getUsersInfo().getFirstName() + ",\n\n" +
                "Click the link to reset your password:\n" + link + "\n\n" +
                "This link will expire in 15 minutes.";

        emailService.send(user.getEmail(), subject, body);
    }

    public String resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optional = passwordResetTokenRepo.findByToken(token);
        if (optional.isEmpty()) {
            return "Invalid or expired reset token.";
        }

        PasswordResetToken resetToken = optional.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Reset token has expired.";
        }

        User user = resetToken.getUser();
        if (user == null) {
            return "User linked to token not found.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepo.delete(resetToken);

        return "Password has been reset successfully.";
    }

    public String handleOAuth2User(String email, String firstName, String lastName) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("oauth2-" + email));
            user.setRole(Role.USER);

            UsersInfo usersInfo = new UsersInfo();
            usersInfo.setFirstName(firstName != null ? firstName : "Unknown");
            usersInfo.setLastName(lastName != null ? lastName : "Unknown");
            usersInfo.setPhoneNumber("0000000000");
            usersInfo.setUser(user);
            user.setUsersInfo(usersInfo);

            userRepository.save(user);
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
        return jwtUtil.generateToken(userDetails);
    }

    public boolean validateToken(String jwtToken) {
        try {
            // Parse the JWT token to extract claims
            jwtUtil.extractAllClaims(jwtToken);
            return true; // If parsing is successful, the token is valid
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false; // If an exception occurs, the token is invalid
        }
    }

    public Map<String, Object> extractClaims(String jwtToken) {
        try {
            // Extract claims from the JWT token
            return jwtUtil.extractAllClaims(jwtToken);
        } catch (Exception e) {
            System.err.println("Error extracting claims: " + e.getMessage());
            return new HashMap<>(); // Return an empty map if extraction fails
        }
    }


}
