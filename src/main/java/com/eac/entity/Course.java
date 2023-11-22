/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCourse;

    private String nameCourse;

    private Integer idTeacher;

    @ManyToOne
    @JoinColumn(name = "idTeacher", insertable = false, updatable = false)
    private MasterUser teacher;

    private String description;

    private String learningMaterials;

    private Boolean isDeleted;

}
