package com.medcore.his.config;

import com.medcore.his.domain.patient.Patient;
import com.medcore.his.domain.staff.StaffProfile;
import com.medcore.his.domain.auxiliary.BloodInventory;
import com.medcore.his.repository.PatientRepository;
import com.medcore.his.repository.StaffProfileRepository;
import com.medcore.his.repository.BloodInventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.medcore.his.domain.auth.User;
import com.medcore.his.domain.auth.Role;
import com.medcore.his.repository.UserRepository;
import com.medcore.his.repository.RoleRepository;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
public class DatabaseSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(PatientRepository patientRepo, 
                                   StaffProfileRepository staffRepo, 
                                   BloodInventoryRepository bloodRepo,
                                   UserRepository userRepo,
                                   RoleRepository roleRepo,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("Checking Role Seeding...");
            Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_ADMIN");
                role.setDescription("Administrator with full access");
                return roleRepo.save(role);
            });

            System.out.println("Checking User Seeding...");
            String adminUsername = System.getenv("MEDCORE_ADMIN_USERNAME");
            if (adminUsername == null || adminUsername.isEmpty()) {
                adminUsername = "admin";
            }
            String adminPassword = System.getenv("MEDCORE_ADMIN_PASSWORD");
            if (adminPassword == null || adminPassword.isEmpty()) {
                adminPassword = "admin123";
            }
            User admin = userRepo.findByUsername(adminUsername).orElse(new User());
            admin.setUsername(adminUsername);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            if (admin.getFirstName() == null) admin.setFirstName("System");
            if (admin.getLastName() == null) admin.setLastName("Administrator");
            if (admin.getEmail() == null) admin.setEmail("admin@omnigrid.health");
            if (admin.getRoles().isEmpty()) admin.getRoles().add(adminRole);
            userRepo.save(admin);
            System.out.println("Seeded/Updated Admin: " + adminUsername + " / " + adminPassword);
            if (staffRepo.count() == 0) {
                System.out.println("Seeding StaffProfiles...");
                
                StaffProfile s1 = new StaffProfile();
                s1.setEmployeeCode("EMP-001");
                s1.setFullName("Dr. Anjali Desai");
                s1.setRole("Consultant");
                s1.setDepartment("Pulmonology");
                s1.setContactNumber("+91-9800011122");
                s1.setActive(true);
                staffRepo.save(s1);
                
                StaffProfile s2 = new StaffProfile();
                s2.setEmployeeCode("DR-102");
                s2.setFullName("Dr. Gregory House");
                s2.setRole("Consultant");
                s2.setDepartment("Diagnostic Medicine");
                s2.setContactNumber("+1-555-0102");
                s2.setActive(true);
                staffRepo.save(s2);
                
                StaffProfile s3 = new StaffProfile();
                s3.setEmployeeCode("NRS-101");
                s3.setFullName("Nurse Joy");
                s3.setRole("Head Nurse");
                s3.setDepartment("General Medicine");
                s3.setContactNumber("+1-555-0103");
                s3.setActive(true);
                staffRepo.save(s3);
            }

            if (bloodRepo.count() == 0) {
                System.out.println("Seeding BloodInventory...");
                
                BloodInventory b1 = new BloodInventory();
                b1.setUnitNumber("BU-1001");
                b1.setBloodGroup("O+");
                b1.setComponentType("Whole Blood");
                b1.setCollectionDate(LocalDate.now().minusDays(2));
                b1.setExpiryDate(LocalDate.now().plusDays(35));
                b1.setStatus("Available");
                bloodRepo.save(b1);

                BloodInventory b2 = new BloodInventory();
                b2.setUnitNumber("BU-1002");
                b2.setBloodGroup("A-");
                b2.setComponentType("PRBC");
                b2.setCollectionDate(LocalDate.now().minusDays(5));
                b2.setExpiryDate(LocalDate.now().plusDays(42));
                b2.setStatus("Available");
                bloodRepo.save(b2);
            }

            System.out.println("Database Seeding Complete.");
        };
    }
}
