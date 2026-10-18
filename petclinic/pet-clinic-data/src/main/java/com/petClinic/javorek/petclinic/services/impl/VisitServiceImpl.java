package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.repositories.PetRepository;
import com.petClinic.javorek.petclinic.repositories.VisitRepository;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import com.petClinic.javorek.petclinic.services.VisitService;
import org.springframework.stereotype.Service;

@Service
public class VisitServiceImpl extends AbstractJpaService<Visit, VisitRepository> implements VisitService {

    private final PetRepository petRepository;

    public VisitServiceImpl(VisitRepository repository, PetRepository petRepository) {
        super(repository, "Visit");
        this.petRepository = petRepository;
    }

    @Override
    public Visit addToPet(Long petId, Visit visit) {
        visit.setPet(petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet", petId)));
        return repository.save(visit);
    }
}
