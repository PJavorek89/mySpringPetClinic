package com.petClinic.javorek.petclinic.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Person extends BaseEntity {

    private String firstName;
    private String lastName;
}
