package com.OswaldoBenitez.usersApi.Service;

import com.OswaldoBenitez.usersApi.Model.User;
import com.OswaldoBenitez.usersApi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String SECRET_KEY = "MiClaveSecreta12345678901234567A";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers(String sortedBy) {

        List<User> users = userRepository.findAll();

        if (sortedBy == null || sortedBy.isBlank()) {
            return users;
        }

        String sortField = sortedBy.toLowerCase();

        switch (sortField) {
            case "name":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getName()).compareTo(nullToEmpty(userTwo.getName())))
                        .collect(Collectors.toList());
            case "email":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getEmail()).compareTo(nullToEmpty(userTwo.getEmail())))
                        .collect(Collectors.toList());
            case "id":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getId()).compareTo(nullToEmpty(userTwo.getId())))
                        .collect(Collectors.toList());
            case "phone":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getPhone()).compareTo(nullToEmpty(userTwo.getPhone())))
                        .collect(Collectors.toList());
            case "tax_id":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getTaxId()).compareTo(nullToEmpty(userTwo.getTaxId())))
                        .collect(Collectors.toList());
            case "created_at":
                return users.stream()
                        .sorted((userOne, userTwo) ->
                                nullToEmpty(userOne.getCreatedAt()).compareTo(nullToEmpty(userTwo.getCreatedAt())))
                        .collect(Collectors.toList());
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid sortedBy: " + sortedBy
                );
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public List<User> getFilteredUsers(String filter) {

        if (filter == null || filter.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filter is required. Format: field operator value"
            );
        }

        String[] parts = filter.split(" ", 3);
        if (parts.length != 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filter format must be: field operator value"
            );
        }

        String fieldName = parts[0];
        String operator = parts[1];
        String searchValue = parts[2].toLowerCase();

        return userRepository.findAll()
                .stream()
                .filter(user -> {
                    String fieldValue = getFieldValue(user, fieldName);
                    if (fieldValue == null) {
                        return false;
                    }
                    String fieldValueLower = fieldValue.toLowerCase();
                    switch (operator) {
                        case "co":
                            return fieldValueLower.contains(searchValue);
                        case "eq":
                            return fieldValueLower.equals(searchValue);
                        case "sw":
                            return fieldValueLower.startsWith(searchValue);
                        case "ew":
                            return fieldValueLower.endsWith(searchValue);
                        default:
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Invalid operator: " + operator + ". Use: co, eq, sw, ew"
                            );
                    }
                })
                .collect(Collectors.toList());
    }

    private String getFieldValue(User user, String fieldName) {
        switch (fieldName) {
            case "email":
                return user.getEmail();
            case "id":
                return user.getId();
            case "name":
                return user.getName();
            case "phone":
                return user.getPhone();
            case "tax_id":
                return user.getTaxId();
            case "created_at":
                return user.getCreatedAt();
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid field: " + fieldName
                );
        }
    }

    public User createUser(User newUser) {

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        if (newUser.getPhone() == null || newUser.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phone is required");
        }
        if (newUser.getTaxId() == null || newUser.getTaxId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tax_id is required");
        }
        if (newUser.getPassword() == null || newUser.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password is required");
        }

        if (!isValidRfc(newUser.getTaxId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid RFC format. Example: AARR990101XXX"
            );
        }

        if (!isValidPhone(newUser.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid phone format. Example: +521234567890 or 1234567890"
            );
        }

        if (userRepository.findByTaxId(newUser.getTaxId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "tax_id already exists");
        }

        newUser.setPassword(encryptPassword(newUser.getPassword()));
        newUser.setId(UUID.randomUUID().toString());
        newUser.setCreatedAt(getCurrentDateTime());

        return userRepository.save(newUser);
    }

    public User updateUser(String userId, User updatedUser) {

        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (updatedUser.getPhone() != null && !isValidPhone(updatedUser.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid phone format. Example: +521234567890 or 1234567890"
            );
        }

        if (updatedUser.getTaxId() != null && !isValidRfc(updatedUser.getTaxId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid RFC format. Example: AARR990101XXX"
            );
        }

        if (updatedUser.getTaxId() != null) {
            User userWithSameTaxId = userRepository.findByTaxId(updatedUser.getTaxId());
            if (userWithSameTaxId != null && !userWithSameTaxId.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "tax_id already exists");
            }
        }

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getTaxId() != null) {
            existingUser.setTaxId(updatedUser.getTaxId());
        }
        if (updatedUser.getAddresses() != null) {
            existingUser.setAddresses(updatedUser.getAddresses());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(encryptPassword(updatedUser.getPassword()));
        }

        return existingUser;
    }

    public void deleteUser(String userId) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.delete(existingUser);
    }

    public User findByTaxId(String taxId) {
        return userRepository.findByTaxId(taxId);
    }

    public boolean checkPassword(String plainPassword, String encryptedPassword) {
        String decryptedPassword = decryptPassword(encryptedPassword);
        return decryptedPassword.equals(plainPassword);
    }

    private String encryptPassword(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception exception) {
            throw new RuntimeException("Error encrypting password", exception);
        }
    }

    private String decryptPassword(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decryptedBytes));
        } catch (Exception exception) {
            throw new RuntimeException("Error decrypting password", exception);
        }
    }

    private boolean isValidRfc(String taxId) {
        String rfcRegex = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$";
        return taxId != null && taxId.toUpperCase().matches(rfcRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(\\+\\d{1,3}[\\s-]?)?\\d{10}$";
        return phone != null && phone.matches(phoneRegex);
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Indian/Antananarivo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return now.format(formatter);
    }
}
