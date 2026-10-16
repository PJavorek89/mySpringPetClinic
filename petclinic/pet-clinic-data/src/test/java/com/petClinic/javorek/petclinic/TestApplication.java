package com.petClinic.javorek.petclinic;

import org.springframework.boot.autoconfigure.SpringBootApplication;

// The data module has no application class of its own (it lives in pet-clinic-web).
// @DataJpaTest needs a @SpringBootConfiguration to bootstrap, so tests get this one.
@SpringBootApplication
class TestApplication {
}
