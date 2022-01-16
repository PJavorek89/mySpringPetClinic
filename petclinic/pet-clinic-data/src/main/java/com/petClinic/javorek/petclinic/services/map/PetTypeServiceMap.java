package com.petClinic.javorek.petclinic.services.map;


import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.services.CrudService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PetTypeServiceMap extends AbstractMapService<PetType, Long> implements CrudService<PetType, Long> {
    @Override
    public Set<PetType> findAll() {
        return super.findAll();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(PetType petType) {
        super.delete(petType);

    }

    @Override
    public PetType save(PetType petType) {
        return super.save(findById(petType.getId(), petType);
        //todo - id on petType
    }

    @Override
    public PetType findById(Long id) {
        return super.findById(id);
    }
}
