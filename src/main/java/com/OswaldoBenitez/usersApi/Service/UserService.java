package com.OswaldoBenitez.usersApi.Service;

import com.OswaldoBenitez.usersApi.Model.User;
import com.OswaldoBenitez.usersApi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

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
                    "Filter must be: field+operator+value");
        }

        String fieldName = filterParts[0];
        String operator = filterParts[1];
        String searchValue = filterParts[2].toLowerCase();

        return userRepository.findAll()
                .stream()
                .filter(currentUser -> matchesFilter(currentUser, fieldName, operator, searchValue))
                .collect(Collectors.toList());
    }


    public User findByTaxId(String taxId) {
        return userRepository.findByTaxId(taxId);
    }

    private boolean matchesFilter(User currentUser, String fieldName, String operator, String searchValue) {
        String fieldValue = getFieldValue(currentUser, fieldName);

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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid operator: " + operator + ". Use: co, eq, sw, ew");
        }
    }

    private String getFieldValue(User currentUser, String fieldName) {
        switch (fieldName) {
            case "email":
                return currentUser.getEmail();
            case "id":
                return currentUser.getId();
            case "name":
                return currentUser.getName();
            case "phone":
                return currentUser.getPhone();
            case "tax_id":
                return currentUser.getTaxId();
            case "created_at":
                return currentUser.getCreatedAt();
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid field: " + fieldName);
        }
    }
}
