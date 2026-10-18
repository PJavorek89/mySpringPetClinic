package com.petClinic.javorek.petclinic.mapper;

import com.petClinic.javorek.petclinic.dto.OwnerDto;
import com.petClinic.javorek.petclinic.dto.OwnerRequest;
import com.petClinic.javorek.petclinic.dto.PetDto;
import com.petClinic.javorek.petclinic.dto.PetRequest;
import com.petClinic.javorek.petclinic.dto.PetTypeDto;
import com.petClinic.javorek.petclinic.dto.SpecialtyDto;
import com.petClinic.javorek.petclinic.dto.VetDto;
import com.petClinic.javorek.petclinic.dto.VetRequest;
import com.petClinic.javorek.petclinic.dto.VisitDto;
import com.petClinic.javorek.petclinic.dto.VisitRequest;
import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.model.Visit;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

// Hand-written entity <-> DTO mapping. DTOs carry only ids for back-references (ownerId, petId),
// which is what breaks the Owner <-> Pet <-> Visit cycles that would blow up JSON serialization.
@Component
public class ClinicMapper {

    public OwnerDto toDto(Owner owner) {
        List<PetDto> pets = owner.getPets().stream()
                .sorted(Comparator.comparing(Pet::getId))
                .map(this::toDto)
                .toList();
        return new OwnerDto(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getAddress(),
                owner.getCity(), owner.getTelephone(), pets);
    }

    public Owner toEntity(OwnerRequest request) {
        Owner owner = new Owner();
        update(owner, request);
        return owner;
    }

    public void update(Owner owner, OwnerRequest request) {
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        owner.setAddress(request.address());
        owner.setCity(request.city());
        owner.setTelephone(request.telephone());
    }

    public PetDto toDto(Pet pet) {
        List<VisitDto> visits = pet.getVisits().stream()
                .sorted(Comparator.comparing(Visit::getId))
                .map(this::toDto)
                .toList();
        Long ownerId = pet.getOwner() == null ? null : pet.getOwner().getId();
        return new PetDto(pet.getId(), pet.getName(), pet.getBirthDate(), toDto(pet.getPetType()), ownerId, visits);
    }

    public void update(Pet pet, PetRequest request, PetType type) {
        pet.setName(request.name());
        pet.setBirthDate(request.birthDate());
        pet.setPetType(type);
    }

    public VisitDto toDto(Visit visit) {
        Long petId = visit.getPet() == null ? null : visit.getPet().getId();
        return new VisitDto(visit.getId(), visit.getDate(), visit.getDescription(), petId);
    }

    public Visit toEntity(VisitRequest request) {
        Visit visit = new Visit();
        update(visit, request);
        return visit;
    }

    public void update(Visit visit, VisitRequest request) {
        visit.setDate(request.date() != null ? request.date() : LocalDate.now());
        visit.setDescription(request.description());
    }

    public VetDto toDto(Vet vet) {
        List<SpecialtyDto> specialties = vet.getSpecialties().stream()
                .sorted(Comparator.comparing(Specialty::getId))
                .map(this::toDto)
                .toList();
        return new VetDto(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties);
    }

    public void update(Vet vet, VetRequest request, Set<Specialty> specialties) {
        vet.setFirstName(request.firstName());
        vet.setLastName(request.lastName());
        vet.getSpecialties().clear();
        vet.getSpecialties().addAll(specialties);
    }

    public PetTypeDto toDto(PetType type) {
        return type == null ? null : new PetTypeDto(type.getId(), type.getName());
    }

    public void update(PetType type, PetTypeDto dto) {
        type.setName(dto.name());
    }

    public SpecialtyDto toDto(Specialty specialty) {
        return new SpecialtyDto(specialty.getId(), specialty.getName());
    }

    public void update(Specialty specialty, SpecialtyDto dto) {
        specialty.setName(dto.name());
    }
}
