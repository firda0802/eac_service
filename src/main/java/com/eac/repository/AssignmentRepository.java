package com.eac.repository;

import com.eac.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByIdCourseAndIsDeleted(Integer idCourse, Boolean deleted);
    List<Assignment> findByIsDeleted(Boolean deleted);

    @Query("SELECT a FROM Assignment a " +
            "INNER JOIN Course c ON a.idCourse = c.idCourse " +
            "WHERE c.idTeacher = :teacherId and a.isDeleted = :isDeleted")
    List<Assignment> getByDesc(@Param("teacherId") Integer teacherId, @RequestParam("isDeleted") Boolean isDeleted);
}