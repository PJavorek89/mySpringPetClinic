package com.petClinic.javorek.petclinic.services.impl;

import com.petClinic.javorek.petclinic.model.BaseEntity;
import com.petClinic.javorek.petclinic.services.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashSet;
import java.util.Set;

// Shared CRUD logic: every method just delegates to the Spring Data repository.
public abstract class AbstractJpaService<T extends BaseEntity, R extends JpaRepository<T, Long>>
        implements CrudService<T, Long> {

    protected final R repository;

    protected AbstractJpaService(R repository) {
        this.repository = repository;
    }

    @Override
    public Set<T> findAll() {
        return new HashSet<>(repository.findAll());
    }

    @Override
    public T findById(Long id) {
        return repository.findById(id).orElse(null);
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
