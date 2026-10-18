package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.repositories.OwnerRepository;
import com.petClinic.javorek.petclinic.repositories.PetRepository;
import com.petClinic.javorek.petclinic.repositories.VisitRepository;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetAndVisitServiceTest {

    @Mock
    PetRepository petRepository;
    @Mock
    OwnerRepository ownerRepository;
    @Mock
    VisitRepository visitRepository;

    @Test
    void addToOwnerLinksOwnerAndSaves() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petRepository.save(pet)).thenReturn(pet);

        Pet saved = new PetServiceImpl(petRepository, ownerRepository).addToOwner(1L, pet);

        assertThat(saved.getOwner()).isSameAs(owner);
    }

    @Test
    void addToOwnerThrowsWhenOwnerMissing() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new PetServiceImpl(petRepository, ownerRepository).addToOwner(1L, new Pet()))
                .isInstanceOf(NotFoundException.class);
        verify(petRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addToPetLinksPetAndSaves() {
        Pet pet = new Pet();
        Visit visit = new Visit();
        when(petRepository.findById(2L)).thenReturn(Optional.of(pet));
        when(visitRepository.save(visit)).thenReturn(visit);

        Visit saved = new VisitServiceImpl(visitRepository, petRepository).addToPet(2L, visit);

        assertThat(saved.getPet()).isSameAs(pet);
    }

    @Test
    void addToPetThrowsWhenPetMissing() {
        when(petRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new VisitServiceImpl(visitRepository, petRepository).addToPet(2L, new Visit()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(petRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new PetServiceImpl(petRepository, ownerRepository).getById(3L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Pet with id 3");
    }

    @Test
    void getByIdReturnsEntity() {
        Pet pet = new Pet();
        when(petRepository.findById(3L)).thenReturn(Optional.of(pet));

        assertThat(new PetServiceImpl(petRepository, ownerRepository).getById(3L)).isSameAs(pet);
    }
}
