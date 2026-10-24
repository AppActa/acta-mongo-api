package br.com.acta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "acta.auth-api.base-url=http://localhost:8080/api/v1/")
class ActaMongoApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
