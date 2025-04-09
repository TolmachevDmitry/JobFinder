package com.tolmic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "vector_view")
public class VectorView {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long vectorViewId;

    private float[] view;

}
