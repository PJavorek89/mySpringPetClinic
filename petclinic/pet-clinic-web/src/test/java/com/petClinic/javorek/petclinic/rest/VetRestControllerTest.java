package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import com.petClinic.javorek.petclinic.services.VetService;
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

@WebMvcTest(VetRestController.class)
@Import({ClinicMapper.class, GlobalExceptionHandler.class})
class VetRestControllerTest {

    private static final String BODY = "{\"firstName\":\"Alena\",\"lastName\":\"Horakova\",\"specialtyIds\":[1]}";

    @Autowired
    MockMvc mvc;
    @MockitoBean
    VetService vetService;
    @MockitoBean
    SpecialtyService specialtyService;

    private Specialty surgery() {
        Specialty s = new Specialty();
        s.setId(1L);
        s.setName("surgery");
        return s;
    }

    private Vet vet(long id) {
        Vet v = new Vet();
        v.setId(id);
        v.setFirstName("Alena");
        v.setLastName("Horakova");
        v.getSpecialties().add(surgery());
        return v;
    }

    @Test
    void listReturnsVetsWithSpecialties() throws Exception {
        when(vetService.findAll()).thenReturn(Set.of(vet(1)));
        mvc.perform(get("/api/vets")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialties[0].name").value("surgery"));
    }

    @Test
    void getUnknownReturns404() throws Exception {
        when(vetService.getById(9L)).thenThrow(new NotFoundException("Vet", 9L));
        mvc.perform(get("/api/vets/9")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(specialtyService.getById(1L)).thenReturn(surgery());
        when(vetService.save(any())).thenReturn(vet(7));
        mvc.perform(post("/api/vets").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/vets/7"));
    }

    @Test
    void createWithoutNamesReturns400() throws Exception {
        mvc.perform(post("/api/vets").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void updateReturns204() throws Exception {
        when(vetService.getById(1L)).thenReturn(vet(1));
        when(specialtyService.getById(1L)).thenReturn(surgery());
        mvc.perform(put("/api/vets/1").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns204() throws Exception {
        when(vetService.getById(1L)).thenReturn(vet(1));
        mvc.perform(delete("/api/vets/1")).andExpect(status().isNoContent());
        verify(vetService).deleteById(1L);
    }
}
