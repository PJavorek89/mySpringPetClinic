package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OwnerRepositoryTest {

    private final OwnerRepository ownerRepository;

    @Autowired
    OwnerRepositoryTest(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Test
    void saveGeneratesId() {
        Owner saved = ownerRepository.save(owner("Jan", "Novak"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByLastNameStartingWithIgnoreCaseMatchesPrefix() {
        ownerRepository.save(owner("Jan", "Novak"));
        ownerRepository.save(owner("Petr", "Novotny"));
        ownerRepository.save(owner("Eva", "Svobodova"));

        List<Owner> found = ownerRepository.findByLastNameStartingWithIgnoreCase("nov");

        assertThat(found).extracting(Owner::getLastName).containsExactlyInAnyOrder("Novak", "Novotny");
    }

    private Owner owner(String firstName, String lastName) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        return owner;
    }
}
