package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class SpecialtyRestControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    SpecialtyService service;

    private Specialty specialty(long id, String name) {
        Specialty s = new Specialty();
        s.setId(id);
        s.setName(name);
        return s;
    }

    @Test
    void listReturnsSpecialties() throws Exception {
        when(service.findAll()).thenReturn(Set.of(specialty(1, "surgery")));
        mvc.perform(get("/api/specialties")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("surgery"));
    }

    @Test
    void getUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("Specialty", 9L));
        mvc.perform(get("/api/specialties/9")).andExpect(status().isNotFound());
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(service.save(any())).thenReturn(specialty(4, "dentistry"));
        mvc.perform(post("/api/specialties").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"dentistry\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/specialties/4"));
    }

    @Test
    void createTooLongNameReturns400() throws Exception {
        mvc.perform(post("/api/specialties").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + "x".repeat(81) + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(specialty(1, "surgery"));
        mvc.perform(put("/api/specialties/1").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"x\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(specialty(1, "surgery"));
        mvc.perform(delete("/api/specialties/1")).andExpect(status().isNoContent());
        verify(service).deleteById(1L);
    }
}
