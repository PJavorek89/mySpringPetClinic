package com.petClinic.javorek.petclinic;

import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.services.OwnerService;
import com.petClinic.javorek.petclinic.services.PetTypeService;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import com.petClinic.javorek.petclinic.services.VetService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Fills the empty dev database with sample data right after the application starts.
@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final OwnerService ownerService;
    private final PetTypeService petTypeService;
    private final VetService vetService;
    private final SpecialtyService specialtyService;

    public DataLoader(OwnerService ownerService, PetTypeService petTypeService,
                      VetService vetService, SpecialtyService specialtyService) {
        this.ownerService = ownerService;
        this.petTypeService = petTypeService;
        this.vetService = vetService;
        this.specialtyService = specialtyService;
    }

    @Override
    public void run(String... args) {
        if (!petTypeService.findAll().isEmpty()) {
            return;
        }

        PetType dog = petType("dog");
        PetType cat = petType("cat");

        Specialty radiology = specialty("radiology");
        Specialty surgery = specialty("surgery");
        Specialty dentistry = specialty("dentistry");

        vet("Alena", "Horáková", radiology, surgery);
        vet("Tomáš", "Dvořák", dentistry);

        Owner novak = owner("Jan", "Novák", "Hlavní 12", "Praha", "602123456");
        Pet rex = pet("Rex", LocalDate.of(2020, 5, 14), dog, novak);
        visit(rex, LocalDate.of(2024, 3, 2), "Yearly vaccination");

        Owner svobodova = owner("Eva", "Svobodová", "Zahradní 5", "Brno", "777987654");
        pet("Micka", LocalDate.of(2022, 9, 1), cat, svobodova);

        // Pets and visits are saved through the owner thanks to cascade = ALL.
        ownerService.save(novak);
        ownerService.save(svobodova);
    }

    private PetType petType(String name) {
        PetType type = new PetType();
        type.setName(name);
        return petTypeService.save(type);
    }

    private Specialty specialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        return specialtyService.save(specialty);
    }

    private void vet(String firstName, String lastName, Specialty... specialties) {
        Vet vet = new Vet();
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        vet.getSpecialties().addAll(java.util.List.of(specialties));
        vetService.save(vet);
    }

    private Owner owner(String firstName, String lastName, String address, String city, String telephone) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
        return owner;
    }

    // The foreign key is stored on Pet, so owner must be set on the pet (not only added to owner.pets).
    private Pet pet(String name, LocalDate birthDate, PetType type, Owner owner) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setPetType(type);
        pet.setOwner(owner);
        owner.getPets().add(pet);
        return pet;
    }

    private void visit(Pet pet, LocalDate date, String description) {
        Visit visit = new Visit();
        visit.setDate(date);
        visit.setDescription(description);
        visit.setPet(pet);
        pet.getVisits().add(visit);
    }
}
