package com.petClinic.javorek.petclinic.services;

import com.petClinic.javorek.petclinic.model.Visit;

public interface VisitService extends CrudService<Visit, Long> {

    // Creates the visit and links it to the pet (throws NotFoundException if the pet is missing).
    Visit addToPet(Long petId, Visit visit);
}
