package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.repositories.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Covers AbstractJpaService's shared CRUD methods (via OwnerServiceImpl) and the Owner-specific search.
@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock
    OwnerRepository ownerRepository;

    @InjectMocks
    OwnerServiceImpl service;

    @Test
    void findAllReturnsAllOwnersAsSet() {
        Owner a = new Owner();
        Owner b = new Owner();
        when(ownerRepository.findAll()).thenReturn(List.of(a, b));

        Set<Owner> result = service.findAll();

        assertThat(result).containsExactlyInAnyOrder(a, b);
    }

    @Test
    void findByIdReturnsOwner() {
        Owner owner = new Owner();
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThat(service.findById(1L)).isSameAs(owner);
    }

    @Test
    void findByIdReturnsNullWhenMissing() {
        when(ownerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.findById(99L)).isNull();
    }

    @Test
    void saveDelegatesToRepository() {
        Owner owner = new Owner();
        when(ownerRepository.save(owner)).thenReturn(owner);

        assertThat(service.save(owner)).isSameAs(owner);
    }

    @Test
    void deleteDelegatesToRepository() {
        Owner owner = new Owner();

        service.delete(owner);

        verify(ownerRepository).delete(owner);
    }

    @Test
    void deleteByIdDelegatesToRepository() {
        service.deleteById(1L);

        verify(ownerRepository).deleteById(1L);
    }

    @Test
    void findAllByLastNameStartingWithDelegatesToRepository() {
        Owner owner = new Owner();
        when(ownerRepository.findByLastNameStartingWithIgnoreCase("Nov")).thenReturn(List.of(owner));

        assertThat(service.findAllByLastNameStartingWith("Nov")).containsExactly(owner);
    }
}
