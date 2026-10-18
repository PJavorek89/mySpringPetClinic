package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.OwnerService;
import com.petClinic.javorek.petclinic.services.PetService;
import com.petClinic.javorek.petclinic.services.PetTypeService;
import com.petClinic.javorek.petclinic.services.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class OwnerRestControllerTest {

    private static final String OWNER_BODY = "{\"firstName\":\"Jan\",\"lastName\":\"Novak\",\"address\":\"Hlavni 12\","
            + "\"city\":\"Praha\",\"telephone\":\"602123456\"}";
    private static final String PET_BODY = "{\"name\":\"Rex\",\"birthDate\":\"2020-05-14\",\"typeId\":1}";

    @Autowired
    MockMvc mvc;
    @MockitoBean
    OwnerService ownerService;
    @MockitoBean
    PetService petService;
    @MockitoBean
    PetTypeService petTypeService;
    @MockitoBean
    VisitService visitService;

    private Owner owner(long id) {
        Owner o = new Owner();
        o.setId(id);
        o.setFirstName("Jan");
        o.setLastName("Novak");
        o.setAddress("Hlavni 12");
        o.setCity("Praha");
        o.setTelephone("602123456");
        return o;
    }

    private PetType type() {
        PetType t = new PetType();
        t.setId(1L);
        t.setName("dog");
        return t;
    }

    private Pet pet(long id, Owner owner) {
        Pet p = new Pet();
        p.setId(id);
        p.setName("Rex");
        p.setBirthDate(LocalDate.of(2020, 5, 14));
        p.setPetType(type());
        p.setOwner(owner);
        return p;
    }

    @Test
    void listWithoutFilterReturnsAll() throws Exception {
        when(ownerService.findAll()).thenReturn(Set.of(owner(1)));
        mvc.perform(get("/api/owners")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listWithLastNameUsesPrefixSearch() throws Exception {
        when(ownerService.findAllByLastNameStartingWith("No")).thenReturn(List.of(owner(1)));
        mvc.perform(get("/api/owners").param("lastName", "No")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Novak"));
    }

    @Test
    void getReturnsDetailWithPetsAndVisits() throws Exception {
        Owner o = owner(1);
        Pet p = pet(2, o);
        Visit v = new Visit();
        v.setId(3L);
        v.setDescription("Vaccination");
        v.setPet(p);
        p.getVisits().add(v);
        o.getPets().add(p);
        when(ownerService.getById(1L)).thenReturn(o);
        mvc.perform(get("/api/owners/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.pets[0].name").value("Rex"))
                .andExpect(jsonPath("$.pets[0].visits[0].description").value("Vaccination"));
    }

    @Test
    void getUnknownReturnsProblemDetail404() throws Exception {
        when(ownerService.getById(9L)).thenThrow(new NotFoundException("Owner", 9L));
        mvc.perform(get("/api/owners/9")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Owner with id 9 not found"));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(ownerService.save(any())).thenReturn(owner(5));
        mvc.perform(post("/api/owners").contentType(MediaType.APPLICATION_JSON).content(OWNER_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/owners/5"));
    }

    @Test
    void createInvalidReturns400WithErrors() throws Exception {
        mvc.perform(post("/api/owners").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"J4n\",\"lastName\":\"\",\"address\":\"a\",\"city\":\"c\","
                                + "\"telephone\":\"12x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.telephone").exists());
    }

    @Test
    void updateReturns204() throws Exception {
        when(ownerService.getById(1L)).thenReturn(owner(1));
        mvc.perform(put("/api/owners/1").contentType(MediaType.APPLICATION_JSON).content(OWNER_BODY))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns204() throws Exception {
        when(ownerService.getById(1L)).thenReturn(owner(1));
        mvc.perform(delete("/api/owners/1")).andExpect(status().isNoContent());
        verify(ownerService).deleteById(1L);
    }

    @Test
    void deleteUnknownReturns404() throws Exception {
        when(ownerService.getById(9L)).thenThrow(new NotFoundException("Owner", 9L));
        mvc.perform(delete("/api/owners/9")).andExpect(status().isNotFound());
    }

    @Test
    void addPetReturns201WithLocation() throws Exception {
        when(petTypeService.getById(1L)).thenReturn(type());
        when(petService.addToOwner(eq(1L), any())).thenReturn(pet(7, owner(1)));
        mvc.perform(post("/api/owners/1/pets").contentType(MediaType.APPLICATION_JSON).content(PET_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/owners/1/pets/7"));
    }

    @Test
    void addPetWithFutureBirthDateReturns400() throws Exception {
        mvc.perform(post("/api/owners/1/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rex\",\"birthDate\":\"2999-01-01\",\"typeId\":1}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.birthDate").exists());
    }

    @Test
    void addPetToUnknownOwnerReturns404() throws Exception {
        when(petTypeService.getById(1L)).thenReturn(type());
        when(petService.addToOwner(eq(9L), any())).thenThrow(new NotFoundException("Owner", 9L));
        mvc.perform(post("/api/owners/9/pets").contentType(MediaType.APPLICATION_JSON).content(PET_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPetOfAnotherOwnerReturns404() throws Exception {
        when(petService.getById(2L)).thenReturn(pet(2, owner(1)));
        mvc.perform(get("/api/owners/5/pets/2")).andExpect(status().isNotFound());
    }

    @Test
    void getPetReturnsPet() throws Exception {
        when(petService.getById(2L)).thenReturn(pet(2, owner(1)));
        mvc.perform(get("/api/owners/1/pets/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rex"));
    }

    @Test
    void updatePetReturns204() throws Exception {
        when(petService.getById(2L)).thenReturn(pet(2, owner(1)));
        when(petTypeService.getById(1L)).thenReturn(type());
        mvc.perform(put("/api/owners/1/pets/2").contentType(MediaType.APPLICATION_JSON).content(PET_BODY))
                .andExpect(status().isNoContent());
        verify(petService).save(any());
    }

    @Test
    void addVisitReturns201WithLocationAndDefaultsDate() throws Exception {
        Pet p = pet(2, owner(1));
        when(petService.getById(2L)).thenReturn(p);
        Visit saved = new Visit();
        saved.setId(8L);
        saved.setDate(LocalDate.now());
        saved.setDescription("Checkup");
        saved.setPet(p);
        when(visitService.addToPet(eq(2L), any())).thenReturn(saved);
        mvc.perform(post("/api/owners/1/pets/2/visits").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Checkup\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/visits/8"))
                .andExpect(jsonPath("$.petId").value(2));
    }

    @Test
    void addVisitWithoutDescriptionReturns400() throws Exception {
        mvc.perform(post("/api/owners/1/pets/2/visits").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.description").exists());
    }
}
