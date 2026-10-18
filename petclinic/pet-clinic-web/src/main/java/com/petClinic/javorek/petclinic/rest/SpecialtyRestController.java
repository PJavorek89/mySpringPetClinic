package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.SpecialtyDto;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyRestController {

    private final SpecialtyService service;
    private final ClinicMapper mapper;

    public SpecialtyRestController(SpecialtyService service, ClinicMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<SpecialtyDto> list() {
        return service.findAll().stream().sorted(Comparator.comparing(Specialty::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{specialtyId}")
    public SpecialtyDto get(@PathVariable Long specialtyId) {
        return mapper.toDto(service.getById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> create(@Valid @RequestBody SpecialtyDto request) {
        Specialty entity = new Specialty();
        mapper.update(entity, request);
        Specialty saved = service.save(entity);
        return ResponseEntity.created(Locations.ofNewChild(saved.getId())).body(mapper.toDto(saved));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<Void> update(@PathVariable Long specialtyId, @Valid @RequestBody SpecialtyDto request) {
        Specialty entity = service.getById(specialtyId);
        mapper.update(entity, request);
        service.save(entity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> delete(@PathVariable Long specialtyId) {
        service.getById(specialtyId);
        service.deleteById(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
