package com.petClinic.javorek.petclinic.services;

import com.petClinic.javorek.petclinic.model.Owner;

import java.util.List;

public interface OwnerService extends CrudService<Owner, Long> {

    List<Owner> findAllByLastNameStartingWith(String lastName);

    /** Owners whose last name starts with the given text (case-insensitive), sorted by id.
     *  Null or blank returns all owners. */
    List<Owner> search(String lastName);
}
