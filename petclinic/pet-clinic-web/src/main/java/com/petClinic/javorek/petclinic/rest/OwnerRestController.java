package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.OwnerDto;
import com.petClinic.javorek.petclinic.dto.OwnerRequest;
import com.petClinic.javorek.petclinic.dto.PetDto;
import com.petClinic.javorek.petclinic.dto.PetRequest;
import com.petClinic.javorek.petclinic.dto.VisitDto;
import com.petClinic.javorek.petclinic.dto.VisitRequest;
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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerRestController {

    private final OwnerService ownerService;
    private final PetService petService;
    private final PetTypeService petTypeService;
    private final VisitService visitService;
    private final ClinicMapper mapper;

    public OwnerRestController(OwnerService ownerService, PetService petService, PetTypeService petTypeService,
                               VisitService visitService, ClinicMapper mapper) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.petTypeService = petTypeService;
        this.visitService = visitService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<OwnerDto> list(@RequestParam(required = false) String lastName) {
        List<Owner> owners = lastName == null || lastName.isBlank()
                ? List.copyOf(ownerService.findAll())
                : ownerService.findAllByLastNameStartingWith(lastName);
        return owners.stream().sorted(Comparator.comparing(Owner::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{ownerId}")
    public OwnerDto get(@PathVariable Long ownerId) {
        return mapper.toDto(ownerService.getById(ownerId));
    }

    @PostMapping
    public ResponseEntity<OwnerDto> create(@Valid @RequestBody OwnerRequest request) {
        Owner saved = ownerService.save(mapper.toEntity(request));
        return ResponseEntity.created(Locations.ofNewChild(saved.getId())).body(mapper.toDto(saved));
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<Void> update(@PathVariable Long ownerId, @Valid @RequestBody OwnerRequest request) {
        Owner owner = ownerService.getById(ownerId);
        mapper.update(owner, request);
        ownerService.save(owner);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> delete(@PathVariable Long ownerId) {
        ownerService.getById(ownerId); // gives a 404 for unknown ids instead of an exception from deleteById
        ownerService.deleteById(ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ownerId}/pets")
    public ResponseEntity<PetDto> addPet(@PathVariable Long ownerId, @Valid @RequestBody PetRequest request) {
        Pet pet = new Pet();
        mapper.update(pet, request, petTypeOf(request));
        Pet saved = petService.addToOwner(ownerId, pet);
        return ResponseEntity.created(Locations.ofNewChild(saved.getId())).body(mapper.toDto(saved));
    }

    @GetMapping("/{ownerId}/pets/{petId}")
    public PetDto getPet(@PathVariable Long ownerId, @PathVariable Long petId) {
        return mapper.toDto(ownersPet(ownerId, petId));
    }

    @PutMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<Void> updatePet(@PathVariable Long ownerId, @PathVariable Long petId,
                                          @Valid @RequestBody PetRequest request) {
        Pet pet = ownersPet(ownerId, petId);
        mapper.update(pet, request, petTypeOf(request));
        petService.save(pet);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ownerId}/pets/{petId}/visits")
    public ResponseEntity<VisitDto> addVisit(@PathVariable Long ownerId, @PathVariable Long petId,
                                             @Valid @RequestBody VisitRequest request) {
        ownersPet(ownerId, petId);
        Visit saved = visitService.addToPet(petId, mapper.toEntity(request));
        return ResponseEntity.created(Locations.of("/api/visits/{id}", saved.getId())).body(mapper.toDto(saved));
    }

    // typeId comes from the request body, so an unknown id is invalid input (400), not a missing resource (404).
    private PetType petTypeOf(PetRequest request) {
        try {
            return petTypeService.getById(request.typeId());
        } catch (NotFoundException e) {
            throw new InvalidReferenceException("typeId", "Pet type with id " + request.typeId() + " does not exist");
        }
    }

    // A pet is only reachable through its own owner; any other combination is a 404.
    private Pet ownersPet(Long ownerId, Long petId) {
        Pet pet = petService.getById(petId);
        if (pet.getOwner() == null || !pet.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Pet", petId);
        }
        return pet;
    }
}
