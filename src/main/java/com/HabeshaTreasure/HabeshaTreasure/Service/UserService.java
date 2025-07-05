package com.HabeshaTreasure.HabeshaTreasure.Service;

import com.HabeshaTreasure.HabeshaTreasure.DTO.UserProfileUpdateDTO;
import com.HabeshaTreasure.HabeshaTreasure.DTO.UserRequestDTO;
import com.HabeshaTreasure.HabeshaTreasure.DTO.UserResponseDTO;
import com.HabeshaTreasure.HabeshaTreasure.Entity.NotificationType;
import com.HabeshaTreasure.HabeshaTreasure.Entity.Role;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Entity.UsersInfo;
import com.HabeshaTreasure.HabeshaTreasure.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private UsersInfoRepo usersInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private ReviewRepo reviewRepository;
    @Autowired
    private CartItemRepo cartItemRepository;
    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FavoriteProductRepo favoriteProductRepository;


    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }

    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UsersInfo info = user.getUsersInfo();

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                info.getFirstName(),
                info.getLastName(),
                info.getPhoneNumber(),
                info.getCity(),
                info.getCountry(),
                info.getRegion(),
                info.isEnabled(),
                info.getJoined(),
                info.getLastLogin()
        );
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> result = new ArrayList<>();

        for (User user : users) {
            UsersInfo info = usersInfoRepository.findByUserId(user.getId());
            result.add(new UserResponseDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    info.getFirstName(),
                    info.getLastName(),
                    info.getPhoneNumber(),
                    info.getCity(),
                    info.getCountry(),
                    info.getRegion(),
                    info.isEnabled(),
                    info.getJoined(),
                    info.getLastLogin()
            ));
        }

        return result;
    }

    public void updateUser(Long userId, Map<String, Object> updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UsersInfo info = usersInfoRepository.findByUserId(userId);

        if (updates.containsKey("firstName")) info.setFirstName((String) updates.get("firstName"));
        if (updates.containsKey("lastName")) info.setLastName((String) updates.get("lastName"));
        if (updates.containsKey("phoneNumber")) info.setPhoneNumber((String) updates.get("phoneNumber"));
        if (updates.containsKey("city")) info.setCity((String) updates.get("city"));
        if (updates.containsKey("country")) info.setCountry((String) updates.get("country"));
        if (updates.containsKey("region")) info.setRegion((String) updates.get("region"));
        if (updates.containsKey("enabled")) info.setEnabled((Boolean) updates.get("enabled"));

        if (updates.containsKey("role")) {
            try {
                String roleStr = (String) updates.get("role");
                Role newRole = Role.valueOf(roleStr.toUpperCase());
                user.setRole(newRole);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role value");
            }
        }

        userRepository.save(user);
        usersInfoRepository.save(info);

        notificationService.createNotification("User updated: " + user.getEmail(), NotificationType.USER, null);

    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 🧹 1. Delete verification token
        verificationTokenRepository.deleteByUser(user);

        // 🧹 2. Delete reviews
        reviewRepository.deleteByUser(user);
        favoriteProductRepository.deleteByUser(user);

        // 🧹 3. Delete cart items
        cartItemRepository.deleteByUser(user);

        // 🧹 4. Delete orders (and order items because of Cascade)
        orderRepository.deleteByUser(user);

        // 🧹 5. Delete notifications related to user
        notificationRepository.deleteByUser(user); // Only if user-linked notifications exist

        // 🧹 6. Delete users_info
        UsersInfo info = usersInfoRepository.findByUserId(userId);
        if (info != null) {
            usersInfoRepository.delete(info);
        }

        // 🧹 7. Delete user
        userRepository.delete(user);

        // 🔔 Notify
        notificationService.createNotification(
                "User deleted: " + user.getEmail(),
                NotificationType.USER,
                null
        );
    }


    public void createUser(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (usersInfoRepository.findByPhoneNumber(dto.getPhoneNumber()) != null) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        UsersInfo info = new UsersInfo();
        info.setUser(user);
        info.setFirstName(dto.getFirstName());
        info.setLastName(dto.getLastName());
        info.setPhoneNumber(dto.getPhoneNumber());
        info.setCity(dto.getCity());
        info.setCountry(dto.getCountry());
        info.setRegion(dto.getRegion());
        info.setJoined(LocalDateTime.now());
        info.setLastLogin(LocalDateTime.now()); // or null

        // Initialize account flags
        info.setEnabled(true);
        info.setAccountNonExpired(true);
        info.setAccountNonLocked(true);
        info.setCredentialsNonExpired(true);

        user.setUsersInfo(info); // link back

        userRepository.save(user); // saves both due to cascade

        notificationService.createNotification("New user created: " + user.getEmail(), NotificationType.USER, null);

    }

    //===================================================================

    public void updateProfile(User user, UserProfileUpdateDTO dto) {
        UsersInfo info = user.getUsersInfo();

        if (dto.getFirstName() != null) info.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) info.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) info.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getCity() != null) info.setCity(dto.getCity());
        if (dto.getCountry() != null) info.setCountry(dto.getCountry());
        if (dto.getRegion() != null) info.setRegion(dto.getRegion());

        usersInfoRepository.save(info); // repo already injected

        notificationService.createNotification("Profile updated: " + user.getEmail(), NotificationType.USER, user);

    }

}

