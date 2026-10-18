package com.petClinic.javorek.petclinic.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// End-to-end through the real stack (controller -> service -> JPA -> H2) using the DataLoader sample data.
// Also proves that DTO mapping works with open-in-view=false (no LazyInitializationException).
@SpringBootTest
@AutoConfigureMockMvc
class ClinicApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createOwnerThenPetThenVisitAndReadDetail() throws Exception {
        MvcResult ownerResult = mvc.perform(post("/api/owners").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Karel","lastName":"Test","address":"Ulice 1","city":"Praha","telephone":"123456"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/owners/")))
                .andReturn();
        String ownerLocation = ownerResult.getResponse().getHeader("Location");
        long ownerId = ((Number) JsonPath.read(ownerResult.getResponse().getContentAsString(), "$.id")).longValue();

        long typeId = ((Number) JsonPath.read(
                mvc.perform(get("/api/pettypes")).andReturn().getResponse().getContentAsString(), "$[0].id")).longValue();

        MvcResult petResult = mvc.perform(post(ownerLocation + "/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fido\",\"birthDate\":\"2020-01-01\",\"typeId\":" + typeId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andReturn();
        String petLocation = petResult.getResponse().getHeader("Location");

        mvc.perform(post(petLocation + "/visits").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Checkup\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/visits/")))
                .andExpect(jsonPath("$.date").exists());

        mvc.perform(get(ownerLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets", hasSize(1)))
                .andExpect(jsonPath("$.pets[0].visits", hasSize(1)))
                .andExpect(jsonPath("$.pets[0].visits[0].description").value("Checkup"));

        mvc.perform(delete(ownerLocation)).andExpect(status().isNoContent());
        mvc.perform(get(ownerLocation)).andExpect(status().isNotFound());
    }

    @Test
    void searchOwnersByLastNamePrefix() throws Exception {
        mvc.perform(get("/api/owners").param("lastName", "nov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Novák"));
    }

    @Test
    void vetsComeWithSpecialties() throws Exception {
        mvc.perform(get("/api/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialties").isNotEmpty());
    }

    @Test
    void unknownIdReturnsProblemDetail404() throws Exception {
        mvc.perform(get("/api/owners/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Owner with id 99999 not found"));
    }

    @Test
    void invalidOwnerReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/owners").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"X1\",\"telephone\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.telephone").exists())
                .andExpect(jsonPath("$.errors.address").exists());
    }

    @Test
    void duplicatePetTypeNameReturns409() throws Exception {
        String body = "{\"name\":\"unikatni-typ\"}";
        mvc.perform(post("/api/pettypes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/pettypes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mvc.perform(post("/api/owners").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest());
    }
}
