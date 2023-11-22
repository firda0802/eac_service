package com.eac.controller;

import com.eac.entity.Assignment;
import com.eac.entity.MasterUser;
import com.eac.messages.Messages;
import com.eac.messages.ReqGantiPassword;
import com.eac.messages.ReqLogin;
import com.eac.messages.RespLogin;
import com.eac.repository.MasterUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MasterUserRepository masterUserRepository;

    @PostMapping(value = "/login")
    public ResponseEntity<Messages> login(@RequestBody ReqLogin reqLogin) {

        Messages resp = new Messages();
        Optional<MasterUser> cekUser = masterUserRepository.findByEmailAndPasswordAndIsDeleted(reqLogin.getEmail(), reqLogin.getPassword(), false);
        if (cekUser.isPresent()) {
            MasterUser user = cekUser.get();
            resp.success();
            RespLogin respLogin = new RespLogin();
            respLogin.setIdUser(user.getIdUser());
            respLogin.setName(user.getName());
            respLogin.setRole(user.getRoleUser().getRoleName());
            resp.setData(respLogin);
        } else {
            resp.failed("Email atau password salah");
        }
        return ResponseEntity.ok().body(resp);
    }


    @PostMapping(value = "/create")
    public ResponseEntity<Messages> create(@RequestBody MasterUser req) {

        Messages resp = new Messages();
        Optional<MasterUser> cekUser = masterUserRepository.findByEmailAndIsDeleted(req.getEmail(), false);
        if (cekUser.isPresent()) {
            resp.failed("Email sudah digunakan");
        } else {
            if (req.getIdRole() > 1) {
                req.setIsDeleted(false);
                masterUserRepository.saveAndFlush(req);
                resp.success();
            } else {
                resp.failed("Tidak bisa membuat admin");
            }

        }
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/list/{id}")
    public ResponseEntity<Messages> listUser(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        if (id > 0) {
            resp.setData(masterUserRepository.findByIdRoleAndIsDeleted(id, false));
        } else {
            resp.setData(masterUserRepository.findByIsDeleted(false));
        }
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/one/{id}")
    public ResponseEntity<Messages> getOneUser(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        List<MasterUser> list = new ArrayList<>();
        Optional<MasterUser> oneUser = masterUserRepository.findById(id);
        if(oneUser.isPresent()){
            MasterUser m =oneUser.get();
            list.add(m);
        }
        resp.setData(list);
        return ResponseEntity.ok().body(resp);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Messages> updateUser(@PathVariable(name = "id") Integer id, @RequestBody MasterUser req) {
        Messages resp = new Messages();
        Optional<MasterUser> cekData = masterUserRepository.findById(id);
        if (cekData.isPresent()) {
            MasterUser mu = cekData.get();
            if (mu.getEmail().equals(req.getEmail())) {
                mu.setName(req.getName());
                if (!req.getPassword().isEmpty()) {
                    mu.setPassword(req.getPassword());
                }
                mu.setEmail(req.getEmail());
                masterUserRepository.saveAndFlush(mu);
                resp.success();
            } else {
                Optional<MasterUser> cekUser = masterUserRepository.findByEmailAndIsDeleted(req.getEmail(), false);
                if (cekUser.isPresent()) {
                    resp.failed("Email sudah digunakan");
                } else {
                    mu.setName(req.getName());
                    if (!req.getPassword().isEmpty()) {
                        mu.setPassword(req.getPassword());
                    }
                    mu.setEmail(req.getEmail());
                    masterUserRepository.saveAndFlush(mu);
                    resp.success();
                }
            }
        } else {
            resp.failed("Id tidak ditemukan");
        }

        return ResponseEntity.ok().body(resp);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Messages> deleteUser(@PathVariable(name = "id") Integer id) {
        Messages resp = new Messages();
        Optional<MasterUser> cekData = masterUserRepository.findById(id);
        if (cekData.isPresent()) {
            MasterUser mu = cekData.get();
            mu.setIsDeleted(true);
            masterUserRepository.saveAndFlush(mu);
            resp.success();
        } else {
            resp.failed("Id tidak ditemukan");
        }

        return ResponseEntity.ok().body(resp);
    }

    @PutMapping(value = "/password/{id}")
    public ResponseEntity<Messages> changePassword(@PathVariable(name = "id") Integer id, @RequestBody ReqGantiPassword req) {
        Messages resp = new Messages();
        Optional<MasterUser> cekData = masterUserRepository.findByIdUserAndPassword(id, req.getPasswordLama());
        if (cekData.isPresent()) {
            MasterUser mu = cekData.get();
            mu.setPassword(req.getPasswordBaru());
            masterUserRepository.saveAndFlush(mu);
            resp.success();
        } else {
            resp.failed("Password lama salah");
        }

        return ResponseEntity.ok().body(resp);
    }


}
