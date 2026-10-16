package com.petClinic.javorek.petclinic.services;

import com.petClinic.javorek.petclinic.model.Owner;

import java.util.List;

public interface OwnerService extends CrudService<Owner, Long> {

    List<Owner> findAllByLastNameStartingWith(String lastName);
}
