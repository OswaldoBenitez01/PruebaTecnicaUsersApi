
package com.OswaldoBenitez.usersApi.Service;

import com.OswaldoBenitez.usersApi.Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService implements UserDetailsService {

    private final UserService userService;

    public AuthUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String taxId) throws UsernameNotFoundException {

        User result = userService.findByTaxId(taxId);

        if (result == null) {
            throw new UsernameNotFoundException("User not found with taxId: " + taxId);
        }

        User user = result;

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getTaxId())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
