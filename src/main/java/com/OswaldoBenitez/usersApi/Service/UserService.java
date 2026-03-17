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

        if (sortedBy == null || sortedBy.isBlank()) {
            return userRepository.findAll();
        }

        switch (sortedBy) {
            case "name":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getName().compareTo(userTwo.getName()))
                        .collect(Collectors.toList());
            case "email":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getEmail().compareTo(userTwo.getEmail()))
                        .collect(Collectors.toList());
            case "id":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getId().compareTo(userTwo.getId()))
                        .collect(Collectors.toList());
            case "phone":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getPhone().compareTo(userTwo.getPhone()))
                        .collect(Collectors.toList());
            case "tax_id":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getTaxId().compareTo(userTwo.getTaxId()))
                        .collect(Collectors.toList());
            case "created_at":
                return userRepository.findAll()
                        .stream()
                        .sorted((userOne, userTwo) -> userOne.getCreatedAt().compareTo(userTwo.getCreatedAt()))
                        .collect(Collectors.toList());
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortedBy: " + sortedBy);
        }
    }

    public List<User> getFilteredUsers(String filter) {

        String[] filterParts = filter.split(" ", 3);

        if (filterParts.length != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Filter format must be: field+operator+value");
        }

        String fieldName   = filterParts[0];
        String operator    = filterParts[1];
        String searchValue = filterParts[2].toLowerCase();

        return userRepository.findAll()
                .stream()
                .filter(currentUser -> matchesFilter(currentUser, fieldName, operator, searchValue))
                .collect(Collectors.toList());
    }

    public User createUser(User newUser) {

        if (!isValidRfc(newUser.getTaxId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid RFC format. Example: AARR990101XXX");
        }
        if (!isValidPhone(newUser.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid phone format. Example: +521234567890 or 1234567890");
        }

        User existingUser = userRepository.findByTaxId(newUser.getTaxId());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "tax_id already exists");
        }

        newUser.setPassword(encryptPassword(newUser.getPassword()));
        newUser.setId(UUID.randomUUID().toString());
        newUser.setCreatedAt(getCurrentDateTime());

        return userRepository.save(newUser);
    }

    public User updateUser(String userId, User updatedFields) {

        User existingUser = userRepository.findById(userId);

        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (updatedFields.getPhone() != null && !isValidPhone(updatedFields.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid phone format. Example: +521234567890 or 1234567890");
        }
        if (updatedFields.getTaxId() != null && !isValidRfc(updatedFields.getTaxId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid RFC format. Example: AARR990101XXX");
        }
        if (updatedFields.getTaxId() != null) {
            User userWithSameTaxId = userRepository.findByTaxId(updatedFields.getTaxId());
            if (userWithSameTaxId != null && !userWithSameTaxId.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "tax_id already exists");
            }
        }

        if (updatedFields.getEmail() != null)     existingUser.setEmail(updatedFields.getEmail());
        if (updatedFields.getName() != null)      existingUser.setName(updatedFields.getName());
        if (updatedFields.getPhone() != null)     existingUser.setPhone(updatedFields.getPhone());
        if (updatedFields.getTaxId() != null)     existingUser.setTaxId(updatedFields.getTaxId());
        if (updatedFields.getAddresses() != null) existingUser.setAddresses(updatedFields.getAddresses());

        if (updatedFields.getPassword() != null) {
            existingUser.setPassword(encryptPassword(updatedFields.getPassword()));
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
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    private String decryptPassword(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decryptedBytes));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting password", e);
        }
    }

    private boolean isValidRfc(String taxId) {
        if (taxId == null) return false;
        String rfcRegex = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$";
        return taxId.toUpperCase().matches(rfcRegex);
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String phoneRegex = "^(\\+\\d{1,3}[\\s-]?)?\\d{10}$";
        return phone.matches(phoneRegex);
    }

    private boolean matchesFilter(User currentUser, String fieldName, String operator, String searchValue) {
        String fieldValue = getFieldValue(currentUser, fieldName);

        if (fieldValue == null) return false;

        String fieldValueLower = fieldValue.toLowerCase();

        switch (operator) {
            case "co": return fieldValueLower.contains(searchValue);
            case "eq": return fieldValueLower.equals(searchValue);
            case "sw": return fieldValueLower.startsWith(searchValue);
            case "ew": return fieldValueLower.endsWith(searchValue);
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid operator: " + operator + ". Use: co, eq, sw, ew");
        }
    }

    private String getFieldValue(User currentUser, String fieldName) {
        switch (fieldName) {
            case "email":      return currentUser.getEmail();
            case "id":         return currentUser.getId();
            case "name":       return currentUser.getName();
            case "phone":      return currentUser.getPhone();
            case "tax_id":     return currentUser.getTaxId();
            case "created_at": return currentUser.getCreatedAt();
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid field: " + fieldName);
        }
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Indian/Antananarivo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return now.format(formatter);
    }
}
