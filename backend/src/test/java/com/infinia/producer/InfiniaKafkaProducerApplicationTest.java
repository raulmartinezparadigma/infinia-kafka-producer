package com.infinia.producer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "stability.api.key=dummy-test-key",
    "app.jwt.secret=dummy-super-secret-key-for-testing-purposes-only-1234567890",
    "app.jwt.expiration=86400000", // 24 hours in ms for testing
    "cloudflare.account.id=dummy-account-id",
    "cloudflare.api.token=dummy-api-token",
    "app.base-url=http://localhost:8081"
})
class InfiniaKafkaProducerApplicationTest {

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

}
