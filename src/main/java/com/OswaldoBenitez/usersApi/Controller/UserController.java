package com.OswaldoBenitez.usersApi.Controller;

import com.OswaldoBenitez.usersApi.Model.User;
import com.OswaldoBenitez.usersApi.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    
}
