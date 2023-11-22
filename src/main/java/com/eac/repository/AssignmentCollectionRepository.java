package com.eac.repository;

import com.eac.entity.Assignment;
import com.eac.entity.AssignmentCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AssignmentCollectionRepository extends JpaRepository<AssignmentCollection, Integer> {
    AssignmentCollection findByIdAssignmentAndIdUser(int idCourse, int idUser);
    List<AssignmentCollection> findByIdUser(int idUser);

    @Query("SELECT a FROM AssignmentCollection a " +
            "INNER JOIN Assignment b ON a.idAssignment = b.idAssignment " +
            "INNER JOIN Course c ON b.idCourse = c.idCourse " +
            "WHERE c.idTeacher = :teacherId")
    List<AssignmentCollection> getByDesc(@Param("teacherId") Integer teacherId);
}