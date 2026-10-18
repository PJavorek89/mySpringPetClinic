package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.repositories.PetTypeRepository;
import com.petClinic.javorek.petclinic.services.PetTypeService;
import org.springframework.stereotype.Service;

@Service
public class PetTypeServiceImpl extends AbstractJpaService<PetType, PetTypeRepository> implements PetTypeService {

    public PetTypeServiceImpl(PetTypeRepository repository) {
        super(repository, "PetType");
    }
}
