package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.repositories.OwnerRepository;
import com.petClinic.javorek.petclinic.repositories.PetRepository;
import com.petClinic.javorek.petclinic.repositories.PetTypeRepository;
import com.petClinic.javorek.petclinic.repositories.SpecialtyRepository;
import com.petClinic.javorek.petclinic.repositories.VetRepository;
import com.petClinic.javorek.petclinic.repositories.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// The CRUD logic is shared (see OwnerServiceImplTest); here we only check that each service
// is wired to its own repository.
@ExtendWith(MockitoExtension.class)
class OtherServicesTest {

    @Mock
    PetRepository petRepository;
    @Mock
    OwnerRepository ownerRepository;
    @Mock
    PetTypeRepository petTypeRepository;
    @Mock
    VetRepository vetRepository;
    @Mock
    VisitRepository visitRepository;
    @Mock
    SpecialtyRepository specialtyRepository;

    @Test
    void petServiceUsesPetRepository() {
        Pet pet = new Pet();
        when(petRepository.findAll()).thenReturn(List.of(pet));

        assertThat(new PetServiceImpl(petRepository, ownerRepository).findAll()).containsExactly(pet);
    }

    @Test
    void petTypeServiceUsesPetTypeRepository() {
        PetType type = new PetType();
        when(petTypeRepository.findAll()).thenReturn(List.of(type));

        assertThat(new PetTypeServiceImpl(petTypeRepository).findAll()).containsExactly(type);
    }

    @Test
    void vetServiceUsesVetRepository() {
        Vet vet = new Vet();
        when(vetRepository.findAll()).thenReturn(List.of(vet));

        assertThat(new VetServiceImpl(vetRepository).findAll()).containsExactly(vet);
    }

    @Test
    void visitServiceUsesVisitRepository() {
        Visit visit = new Visit();
        when(visitRepository.findAll()).thenReturn(List.of(visit));

        assertThat(new VisitServiceImpl(visitRepository, petRepository).findAll()).containsExactly(visit);
    }

    @Test
    void specialtyServiceUsesSpecialtyRepository() {
        Specialty specialty = new Specialty();
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));

        assertThat(new SpecialtyServiceImpl(specialtyRepository).findAll()).containsExactly(specialty);
    }
}
