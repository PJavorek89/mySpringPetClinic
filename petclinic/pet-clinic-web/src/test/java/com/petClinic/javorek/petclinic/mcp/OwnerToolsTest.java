package com.petClinic.javorek.petclinic.mcp;

import com.petClinic.javorek.petclinic.dto.OwnerDto;
import com.petClinic.javorek.petclinic.dto.PetDto;
import com.petClinic.javorek.petclinic.dto.PetTypeDto;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerToolsTest {

    @Mock
    OwnerService ownerService;

    @Mock
    ClinicMapper clinicMapper;

    @InjectMocks
    OwnerTools ownerTools;

    @Test
    void findOwnersDelegatesToSearchAndMapsToDto() {
        // ARRANGE - tell the mocks what to return
        Owner novak = Owner.builder().id(1L).lastName("Novák").build();
        OwnerDto novakDto = new OwnerDto(1L, "Jan", "Novák", "Test_address",
                "Test_city", "0000", new ArrayList<>());
        when(ownerService.search("Nov")).thenReturn(List.of(novak));
        when(clinicMapper.toDto(novak)).thenReturn(novakDto);

        // ACT - call the method under test
        List<OwnerDto> result = ownerTools.findOwners("Nov");

        // ASSERT
        assertThat(result).containsExactly(novakDto);
    }

    @Test
    void findOwnersReturnsPetIdsNeededByOtherTools() {
        Owner novak = Owner.builder().id(1L).lastName("Novák").build();
        PetDto rex = new PetDto(10L, "Rex", LocalDate.of(2020, 5, 1),
                new PetTypeDto(2L, "dog"), 1L, new ArrayList<>());
        OwnerDto novakDto = new OwnerDto(1L, "Jan", "Novák", "Test_address",
                "Test_city", "0000", List.of(rex));
        when(ownerService.search("Nov")).thenReturn(List.of(novak));
        when(clinicMapper.toDto(novak)).thenReturn(novakDto);

        List<OwnerDto> result = ownerTools.findOwners("Nov");

        // the agent needs ownerId and petId for add_pet and record_visit
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).pets()).extracting(PetDto::id).containsExactly(10L);
    }

    @Test
    void findOwnersWithNullListsAllOwners() {
        Owner novak = Owner.builder().id(1L).lastName("Novák").build();
        OwnerDto novakDto = new OwnerDto(1L, "Jan", "Novák", "Test_address",
                "Test_city", "0000", new ArrayList<>());
        when(ownerService.search(null)).thenReturn(List.of(novak));
        when(clinicMapper.toDto(novak)).thenReturn(novakDto);

        assertThat(ownerTools.findOwners(null)).containsExactly(novakDto);
    }

    @Test
    void findOwnersWithBlankTextPassesItToService() {
        when(ownerService.search("")).thenReturn(List.of());

        assertThat(ownerTools.findOwners("")).isEmpty();
    }

    @Test
    void findOwnersWithNoMatchReturnsEmptyList() {
        when(ownerService.search("Xyz")).thenReturn(List.of());

        assertThat(ownerTools.findOwners("Xyz")).isEmpty();
    }
}
