package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.VisitDto;
import com.petClinic.javorek.petclinic.dto.VisitRequest;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.services.VisitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

// Visits are created under /api/owners/{ownerId}/pets/{petId}/visits (see OwnerRestController).
@RestController
@RequestMapping("/api/visits")
public class VisitRestController {

    private final VisitService visitService;
    private final ClinicMapper mapper;

    public VisitRestController(VisitService visitService, ClinicMapper mapper) {
        this.visitService = visitService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<VisitDto> list() {
        return visitService.findAll().stream().sorted(Comparator.comparing(Visit::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{visitId}")
    public VisitDto get(@PathVariable Long visitId) {
        return mapper.toDto(visitService.getById(visitId));
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<Void> update(@PathVariable Long visitId, @Valid @RequestBody VisitRequest request) {
        Visit visit = visitService.getById(visitId);
        mapper.update(visit, request);
        visitService.save(visit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> delete(@PathVariable Long visitId) {
        visitService.getById(visitId);
        visitService.deleteById(visitId);
        return ResponseEntity.noContent().build();
    }
}
