package com.universidad.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class CatalogoApplicationTests {

    @Test
    void contextLoads() {
        // El contexto completo arranca con el perfil dev (H2 en memoria)
    }
}