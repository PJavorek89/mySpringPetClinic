package com.petClinic.javorek.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    // "date" is a reserved word in some databases, so name the column explicitly.
    @Column(name = "visit_date")
    private LocalDate date;

    private String description;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}
