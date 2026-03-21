package com.OswaldoBenitez.usersApi.Controller;

import com.OswaldoBenitez.usersApi.Model.User;
import com.OswaldoBenitez.usersApi.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String filter) {

        if (filter != null) {
            List<User> filteredUsers = userService.getFilteredUsers(filter);
            return ResponseEntity.ok(filteredUsers);
        }

        List<User> userList = userService.getUsers(sortedBy);
        return ResponseEntity.ok(userList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User createdUser = userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId,
                                           @RequestBody User updatedFields) {
        User updatedUser = userService.updateUser(userId, updatedFields);
        return ResponseEntity.ok(updatedUser);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }
}
