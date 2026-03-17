package com.OswaldoBenitez.usersApi.Service;

import com.OswaldoBenitez.usersApi.Component.JwtUtil;
import com.OswaldoBenitez.usersApi.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public String login(String taxId, String password) {

        User foundUser = userService.findByTaxId(taxId);

        if (foundUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        boolean passwordMatches = userService.checkPassword(password, foundUser.getPassword());

        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        
        return jwtUtil.generateToken(taxId);
    }
}
