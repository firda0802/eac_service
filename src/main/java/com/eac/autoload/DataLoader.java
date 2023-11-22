package com.eac.autoload;

import com.eac.entity.MasterUser;
import com.eac.entity.RoleUser;
import com.eac.repository.MasterUserRepository;
import com.eac.repository.RoleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {


    @Autowired
    private MasterUserRepository masterUserRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if(roleUserRepository.findAll().isEmpty()){
            RoleUser roleUser1 = new RoleUser("Admin");
            RoleUser roleUser2 = new RoleUser("Teacher");
            RoleUser roleUser3 = new RoleUser("Student");
            roleUserRepository.saveAndFlush(roleUser1);
            roleUserRepository.saveAndFlush(roleUser2);
            roleUserRepository.saveAndFlush(roleUser3);
        }

        if(masterUserRepository.findAll().isEmpty()){
            MasterUser admin = new MasterUser();
            admin.setEmail("admin@gmail.com");
            admin.setName("Admin Firda");
            admin.setIdRole(1);
            admin.setIsDeleted(false);
            admin.setPassword("admin123");
            masterUserRepository.saveAndFlush(admin);
        }

    }
}
