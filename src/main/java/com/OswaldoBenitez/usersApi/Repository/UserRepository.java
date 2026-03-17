package com.OswaldoBenitez.usersApi.repository;

import com.OswaldoBenitez.usersApi.Model.Address;
import com.OswaldoBenitez.usersApi.Model.User;
import org.springframework.stereotype.Repository;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private List<User> users = new ArrayList<>();

    private static final String SECRET_KEY = "MiClaveSecreta12345678901234567A";

    public UserRepository() {
        String encryptedPassword = encryptPassword("qwerty123#");

        users.add(new User(
            UUID.randomUUID().toString(),
            "user1@mail.com",
            "user1",
            "+15555555555",
            encryptedPassword,
            "AARR990101XXX",
            "01-01-2026 00:00",
            List.of(new Address(1, "workaddress", "street No. 1", "UK"),
                    new Address(2, "homeaddress", "street No. 2", "AU"))
        ));

        users.add(new User(
            UUID.randomUUID().toString(),
            "user2@mail.com",
            "user2",
            "+16666666666",
            encryptedPassword,
            "BBRR990202YYY",
            "01-01-2026 00:00",
            List.of(new Address(1, "workaddress", "street No. 3", "US"),
                    new Address(2, "homeaddress", "street No. 4", "MX"))
        ));

        users.add(new User(
            UUID.randomUUID().toString(),
            "user3@mail.com",
            "user3",
            "+17777777777",
            encryptedPassword,
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
}
