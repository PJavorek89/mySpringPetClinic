package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.repositories.PetRepository;
import com.petClinic.javorek.petclinic.services.PetService;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl extends AbstractJpaService<Pet, PetRepository> implements PetService {

    public PetServiceImpl(PetRepository repository) {
        super(repository);
    }
}
