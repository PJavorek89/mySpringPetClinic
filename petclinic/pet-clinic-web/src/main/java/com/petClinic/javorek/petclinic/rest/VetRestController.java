package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.VetDto;
import com.petClinic.javorek.petclinic.dto.VetRequest;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import com.petClinic.javorek.petclinic.services.VetService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/vets")
public class VetRestController {

    private final VetService vetService;
    private final SpecialtyService specialtyService;
    private final ClinicMapper mapper;

    public VetRestController(VetService vetService, SpecialtyService specialtyService, ClinicMapper mapper) {
        this.vetService = vetService;
        this.specialtyService = specialtyService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<VetDto> list() {
        return vetService.findAll().stream().sorted(Comparator.comparing(Vet::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{vetId}")
    public VetDto get(@PathVariable Long vetId) {
        return mapper.toDto(vetService.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> create(@Valid @RequestBody VetRequest request) {
        Vet vet = new Vet();
        mapper.update(vet, request, specialties(request));
        Vet saved = vetService.save(vet);
        return ResponseEntity.created(Locations.ofNewChild(saved.getId())).body(mapper.toDto(saved));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<Void> update(@PathVariable Long vetId, @Valid @RequestBody VetRequest request) {
        Vet vet = vetService.getById(vetId);
        mapper.update(vet, request, specialties(request));
        vetService.save(vet);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> delete(@PathVariable Long vetId) {
        vetService.getById(vetId);
        vetService.deleteById(vetId);
        return ResponseEntity.noContent().build();
    }

    // Unknown specialty ids -> 404 from getById.
    private Set<Specialty> specialties(VetRequest request) {
        Set<Specialty> result = new HashSet<>();
        if (request.specialtyIds() != null) {
            request.specialtyIds().forEach(id -> result.add(specialtyService.getById(id)));
        }
        return result;
    }
}
