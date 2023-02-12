package com.example;

import com.example.testing.AutoConfigurePostgres;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigurePostgres
class ApplicationTest {

    @Test
    void contextLoads() {
    }
}
