package com.petClinic.javorek.petclinic.services;

import com.petClinic.javorek.petclinic.model.Pet;

public interface PetService extends CrudService<Pet, Long> {

    // Creates the pet and links it to the owner (throws NotFoundException if the owner is missing).
    Pet addToOwner(Long ownerId, Pet pet);
}
