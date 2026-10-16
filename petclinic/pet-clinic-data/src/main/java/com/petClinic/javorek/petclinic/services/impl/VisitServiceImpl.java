package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Visit;
import com.petClinic.javorek.petclinic.repositories.VisitRepository;
import com.petClinic.javorek.petclinic.services.VisitService;
import org.springframework.stereotype.Service;

@Service
public class VisitServiceImpl extends AbstractJpaService<Visit, VisitRepository> implements VisitService {

    public VisitServiceImpl(VisitRepository repository) {
        super(repository);
    }
}
