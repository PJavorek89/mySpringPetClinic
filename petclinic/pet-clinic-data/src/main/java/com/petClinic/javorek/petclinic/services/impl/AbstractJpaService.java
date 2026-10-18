package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.BaseEntity;
import com.petClinic.javorek.petclinic.services.CrudService;
import com.petClinic.javorek.petclinic.services.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

// Shared CRUD logic: every method just delegates to the Spring Data repository.
// @Transactional: with open-in-view=false every service call needs its own transaction,
// and save/delete must commit (or roll back) as one unit.
@Transactional
public abstract class AbstractJpaService<T extends BaseEntity, R extends JpaRepository<T, Long>>
        implements CrudService<T, Long> {

    protected final R repository;
    private final String entityName;

    protected AbstractJpaService(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<T> findAll() {
        return new HashSet<>(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public T getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(entityName, id));
    }

    @Override
    public T save(T object) {
        return repository.save(object);
    }

    @Override
    public void delete(T object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
