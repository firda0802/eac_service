package com.eac.repository;

import com.eac.entity.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface MasterUserRepository extends JpaRepository<MasterUser, Integer> {
    Optional<MasterUser> findByEmailAndPasswordAndIsDeleted(String email, String password, Boolean isDeleted);

    Optional<MasterUser> findByEmailAndIsDeleted(String email, Boolean del);

    List<MasterUser> findByIdRoleAndIsDeleted(Integer idRole, Boolean isDeleted);

    @Query("select a from MasterUser a where a.isDeleted = :isDeleted and a.idRole != 1")
    List<MasterUser> findByIsDeleted(@RequestParam("isDeleted") Boolean isDeleted);

    Optional<MasterUser> findByIdUserAndPassword(int id, String password);
}