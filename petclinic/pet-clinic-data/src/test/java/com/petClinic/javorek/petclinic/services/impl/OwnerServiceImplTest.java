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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

    @Test
    void searchWithNullReturnsAllOwnersSortedById() {
        Owner first = ownerWithId(1L);
        Owner second = ownerWithId(2L);
        // findAll() returns a Set, so the order is undefined; search() must sort by id.
        when(ownerRepository.findAll()).thenReturn(List.of(second, first));

        assertThat(service.search(null)).containsExactly(first, second);

        verify(ownerRepository, never()).findByLastNameStartingWithIgnoreCase(any());
    }

    @Test
    void searchWithBlankTextReturnsAllOwners() {
        Owner owner = ownerWithId(1L);
        when(ownerRepository.findAll()).thenReturn(List.of(owner));

        assertThat(service.search("   ")).containsExactly(owner);

        verify(ownerRepository, never()).findByLastNameStartingWithIgnoreCase(any());
    }

    @Test
    void searchWithTextUsesPrefixSearchSortedById() {
        Owner first = ownerWithId(1L);
        Owner second = ownerWithId(2L);
        when(ownerRepository.findByLastNameStartingWithIgnoreCase("Nov")).thenReturn(List.of(second, first));

        assertThat(service.search("Nov")).containsExactly(first, second);

        verify(ownerRepository, never()).findAll();
    }

    @Test
    void searchWithNoMatchReturnsEmptyList() {
        when(ownerRepository.findByLastNameStartingWithIgnoreCase("Xyz")).thenReturn(List.of());

        assertThat(service.search("Xyz")).isEmpty();
    }

    private static Owner ownerWithId(Long id) {
        Owner owner = new Owner();
        owner.setId(id);
        return owner;
    }
}
