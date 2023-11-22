package com.eac.repository;

import com.eac.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByIsDeleted(Boolean isdeleted);

    List<Course> findByIsDeletedAndIdTeacher(Boolean isdeleted, int idUser);
}