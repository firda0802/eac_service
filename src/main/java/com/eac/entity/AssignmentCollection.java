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
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "assignment_collection")
public class AssignmentCollection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAssignmentColl;

    private Integer idAssignment;

    @ManyToOne
    @JoinColumn(name = "idAssignment", insertable = false, updatable = false)
    private Assignment assignment;

    private Integer idUser;

    @ManyToOne
    @JoinColumn(name = "idUser", insertable = false, updatable = false)
    private MasterUser student;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+7")
    private LocalDateTime submitAssignment;

    private String submitFile;

    private int score;

}
