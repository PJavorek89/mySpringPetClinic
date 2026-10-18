package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Vet;
import com.petClinic.javorek.petclinic.repositories.VetRepository;
import com.petClinic.javorek.petclinic.services.VetService;
import org.springframework.stereotype.Service;

@Service
public class VetServiceImpl extends AbstractJpaService<Vet, VetRepository> implements VetService {

    public VetServiceImpl(VetRepository repository) {
        super(repository, "Vet");
    }
}
