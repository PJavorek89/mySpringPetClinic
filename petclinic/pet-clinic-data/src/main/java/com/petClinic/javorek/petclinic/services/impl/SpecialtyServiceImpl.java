package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Specialty;
import com.petClinic.javorek.petclinic.repositories.SpecialtyRepository;
import com.petClinic.javorek.petclinic.services.SpecialtyService;
import org.springframework.stereotype.Service;

@Service
public class SpecialtyServiceImpl extends AbstractJpaService<Specialty, SpecialtyRepository> implements SpecialtyService {

    public SpecialtyServiceImpl(SpecialtyRepository repository) {
        super(repository);
    }
}
