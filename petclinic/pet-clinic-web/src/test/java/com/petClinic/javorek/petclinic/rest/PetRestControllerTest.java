package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class PetRestControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    PetService service;

    private Pet pet(long id) {
        PetType type = new PetType();
        type.setId(1L);
        type.setName("dog");
        Owner owner = new Owner();
        owner.setId(2L);
        Pet p = new Pet();
        p.setId(id);
        p.setName("Rex");
        p.setBirthDate(LocalDate.of(2020, 5, 14));
        p.setPetType(type);
        p.setOwner(owner);
        return p;
    }

    @Test
    void listReturnsPets() throws Exception {
        when(service.findAll()).thenReturn(Set.of(pet(1)));
        mvc.perform(get("/api/pets")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type.name").value("dog"))
                .andExpect(jsonPath("$[0].ownerId").value(2));
    }

    @Test
    void getReturnsPet() throws Exception {
        when(service.getById(1L)).thenReturn(pet(1));
        mvc.perform(get("/api/pets/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rex"))
                .andExpect(jsonPath("$.birthDate").value("2020-05-14"));
    }

    @Test
    void deleteUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Pet", 9L));
        mvc.perform(delete("/api/pets/9")).andExpect(status().isNotFound());
        verify(service, never()).deleteById(9L);
    }

    @Test
    void getUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Pet", 9L));
        mvc.perform(get("/api/pets/9")).andExpect(status().isNotFound());
    }

    @Test
    void getInvalidIdReturns400() throws Exception {
        mvc.perform(get("/api/pets/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(pet(1));
        mvc.perform(delete("/api/pets/1")).andExpect(status().isNoContent());
        verify(service).deleteById(1L);
    }
}
