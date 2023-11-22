/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "assignment")
public class Assignment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAssignment;

    private Integer idCourse;

    @ManyToOne
    @JoinColumn(name = "idCourse", insertable = false, updatable = false)
    private Course course;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+7")
    private LocalDateTime deadline;

    private String assignmentFile;

    private Boolean isDeleted;

}
