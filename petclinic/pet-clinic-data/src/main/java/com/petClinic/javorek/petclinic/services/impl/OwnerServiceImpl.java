package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.Owner;
import com.petClinic.javorek.petclinic.repositories.OwnerRepository;
import com.petClinic.javorek.petclinic.services.OwnerService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class OwnerServiceImpl extends AbstractJpaService<Owner, OwnerRepository> implements OwnerService {

    public OwnerServiceImpl(OwnerRepository repository) {
        super(repository, "Owner");
    }

    @Override
    public List<Owner> findAllByLastNameStartingWith(String lastName) {
        return repository.findByLastNameStartingWithIgnoreCase(lastName);
    }

    @Override
    public List<Owner> search(String lastName) {
        Collection<Owner> owners = lastName == null || lastName.isBlank()
                ? findAll()
                : findAllByLastNameStartingWith(lastName);
        return owners.stream().sorted(Comparator.comparing(Owner::getId)).toList();
    }
}
