package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.PetTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
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

@WebMvcTest(PetTypeRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class PetTypeRestControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    PetTypeService service;

    private PetType type(long id, String name) {
        PetType t = new PetType();
        t.setId(id);
        t.setName(name);
        return t;
    }

    @Test
    void listReturnsTypes() throws Exception {
        when(service.findAll()).thenReturn(Set.of(type(1, "dog")));
        mvc.perform(get("/api/pettypes")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("dog"));
    }

    @Test
    void getUnknownReturns404() throws Exception {
        when(service.getById(9L)).thenThrow(new NotFoundException("PetType", 9L));
        mvc.perform(get("/api/pettypes/9")).andExpect(status().isNotFound());
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(service.save(any())).thenReturn(type(5, "bird"));
        mvc.perform(post("/api/pettypes").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"bird\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/pettypes/5"));
    }

    @Test
    void createBlankNameReturns400() throws Exception {
        mvc.perform(post("/api/pettypes").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void updateReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(type(1, "dog"));
        mvc.perform(put("/api/pettypes/1").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"wolf\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns204() throws Exception {
        when(service.getById(1L)).thenReturn(type(1, "dog"));
        mvc.perform(delete("/api/pettypes/1")).andExpect(status().isNoContent());
        verify(service).deleteById(1L);
    }
}
