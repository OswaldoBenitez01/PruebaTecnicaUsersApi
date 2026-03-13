package com.OswaldoBenitez.usersApi.repository;

import com.OswaldoBenitez.usersApi.Model.Address;
import com.OswaldoBenitez.usersApi.Model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private List<User> users = new ArrayList<>();

    public UserRepository() {

        users.add(new User(
            UUID.randomUUID().toString(),
            "user1@mail.com",
            "user1",
            "+1 5555555555",
            "qwerty123#",
            "AARR990101XXX",
            "01-01-2026 00:00",
            List.of(new Address(1, "workaddress", "street No. 1", "UK"),
                    new Address(2, "homeaddress", "street No. 2", "AU"))
        ));

        users.add(new User(
            UUID.randomUUID().toString(),
            "user2@mail.com",
            "user2",
            "+1 6666666666",
            "qwerty123#",
            "BBRR990202YYY",
            "01-01-2026 00:00",
            List.of(new Address(1, "workaddress", "street No. 3", "US"),
                    new Address(2, "homeaddress", "street No. 4", "MX"))
        ));

        users.add(new User(
            UUID.randomUUID().toString(),
            "user3@mail.com",
            "user3",
            "+1 7777777777",
            "qwerty123&",
            "CCRR990303ZZZ",
            "01-01-2026 00:00",
            List.of(new Address(1, "workaddress", "street No. 5", "UK"),
                    new Address(2, "homeaddress", "street No. 6", "AU"))
        ));
    }

    public List<User> findAll() {
        return users;
    }

    public User findById(String userId) {
        for (User currentUser : users) {
            if (currentUser.getId().equals(userId)) {
                return currentUser;
            }
        }
        return null;
    }

    public User findByTaxId(String taxId) {
        for (User currentUser : users) {
            if (currentUser.getTaxId().equalsIgnoreCase(taxId)) {
                return currentUser;
            }
        }
        return null;
    }

    public User save(User newUser) {
        users.add(newUser);
        return newUser;
    }

    public void delete(User userToDelete) {
        users.remove(userToDelete);
    }
}
