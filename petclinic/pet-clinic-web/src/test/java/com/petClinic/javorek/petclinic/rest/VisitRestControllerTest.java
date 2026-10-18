package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class VisitRestControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    VisitService service;

    private Visit visit(long id) {
        Pet pet = new Pet();
        pet.setId(3L);
        Visit v = new Visit();
        v.setId(id);
        v.setDate(LocalDate.of(2024, 3, 2));
        v.setDescription("Vaccination");
        v.setPet(pet);
        return v;
    }

    @Test
    void listReturnsVisits() throws Exception {
        when(service.findAll()).thenReturn(Set.of(visit(1)));
        mvc.perform(get("/api/visits")).andExpect(status().isOk()).andExpect(jsonPath("$[0].petId").value(3));
    }

    @Test
    void getReturnsVisit() throws Exception {
        when(service.getById(1L)).thenReturn(visit(1));
        mvc.perform(get("/api/visits/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-03-02"));
    }

    @Test
    void getUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Visit", 9L));
        mvc.perform(get("/api/visits/9")).andExpect(status().isNotFound());
    }

    @Test
    void updateReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(visit(1));
        mvc.perform(put("/api/visits/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Changed\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBlankDescriptionReturns400() throws Exception {
        mvc.perform(put("/api/visits/1").contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    void updateUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Visit", 9L));
        mvc.perform(put("/api/visits/9").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Changed\"}"))
                .andExpect(status().isNotFound());
        verify(service, never()).save(any());
    }

    @Test
    void deleteUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Visit", 9L));
        mvc.perform(delete("/api/visits/9")).andExpect(status().isNotFound());
        verify(service, never()).deleteById(9L);
    }

    @Test
    void deleteReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(visit(1));
        mvc.perform(delete("/api/visits/1")).andExpect(status().isNoContent());
        verify(service).deleteById(1L);
    }
}
