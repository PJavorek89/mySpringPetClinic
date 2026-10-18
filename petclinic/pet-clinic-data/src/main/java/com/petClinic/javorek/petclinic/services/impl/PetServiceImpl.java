package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.repositories.OwnerRepository;
import com.petClinic.javorek.petclinic.repositories.PetRepository;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.PetService;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl extends AbstractJpaService<Pet, PetRepository> implements PetService {

    private final OwnerRepository ownerRepository;

    public PetServiceImpl(PetRepository repository, OwnerRepository ownerRepository) {
        super(repository, "Pet");
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Pet addToOwner(Long ownerId, Pet pet) {
        // The FK lives on the pet, so setting the owner side is what persists the link.
        pet.setOwner(ownerRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner", ownerId)));
        return repository.save(pet);
    }
}
