package com.petClinic.javorek.petclinic.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// @MappedSuperclass: not a table itself, its fields become columns of every subclass table.
@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {

    // IDENTITY: the database generates the id, so new entities no longer have id == null after save.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
