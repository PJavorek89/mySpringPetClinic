package com.petClinic.javorek.petclinic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pet_types")
public class PetType extends BaseEntity {

    private String name;
}
