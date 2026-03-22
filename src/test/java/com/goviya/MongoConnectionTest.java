package com.goviya;

import com.goviya.repository.UserRepository;
import com.goviya.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testMongoConnection() {
        User testUser = User.builder()
                .phone("+94771234567")
                .name("Test Farmer")
                .role("FARMER")
                .district("Colombo")
                .language("si")
                .build();

        User saved = userRepository.save(testUser);
        assertThat(saved.getId()).isNotNull();
        System.out.println("MongoDB connected! User id: " + saved.getId());

        userRepository.delete(saved);
        System.out.println("MongoDB working perfectly!");
    }
}
