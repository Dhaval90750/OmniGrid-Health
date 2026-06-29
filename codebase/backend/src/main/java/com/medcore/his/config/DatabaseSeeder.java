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
                                   com.medcore.his.repository.PermissionRepository permissionRepo,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("Checking Role Seeding...");
            
            // Seed Standard Permissions
            String[] modules = {"Patient Registration", "Clinical Notes", "Operations", "Pharmacy", "Dashboards", "Inventory", "Billing", "Admission/ADT", "Lab Orders/Results", "Radiology", "System Config"};
            for (String mod : modules) {
                com.medcore.his.domain.auth.Permission p = permissionRepo.findByModuleAndAccessLevel(mod, "FULL_ACCESS").orElse(null);
                if (p == null) {
                    p = new com.medcore.his.domain.auth.Permission();
                    p.setCode(mod.toUpperCase().replace(" ", "_").replace("/", "_") + "_FULL_ACCESS");
                    p.setModule(mod);
                    p.setAccessLevel("FULL_ACCESS");
                    p.setDescription("Full access to " + mod);
                    permissionRepo.save(p);
                }
            }

            // Create Helper Function for Roles
            java.util.function.Function<String, Role> getOrCreateRole = (roleName) -> {
                return roleRepo.findByName(roleName).orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription(roleName + " access");
                    return roleRepo.save(role);
                });
            };

            Role adminRole = getOrCreateRole.apply("ROLE_ADMIN");
            adminRole.getPermissions().addAll(permissionRepo.findAll());
            roleRepo.save(adminRole);
            
            Role doctorRole = getOrCreateRole.apply("ROLE_DOCTOR");
            Role nurseRole = getOrCreateRole.apply("ROLE_NURSE");
            Role pharmacistRole = getOrCreateRole.apply("ROLE_PHARMACIST");
            Role labRole = getOrCreateRole.apply("ROLE_LAB_TECH");
            Role radRole = getOrCreateRole.apply("ROLE_RADIOLOGIST");
            Role billerRole = getOrCreateRole.apply("ROLE_BILLER");
            Role invRole = getOrCreateRole.apply("ROLE_INVENTORY_MANAGER");
            Role receptionistRole = getOrCreateRole.apply("ROLE_RECEPTIONIST");

            System.out.println("Checking User Seeding...");
            
            // Helper Function for Users
            java.util.function.BiConsumer<String, Role> seedUser = (username, role) -> {
                User u = userRepo.findByUsername(username).orElse(null);
                if (u == null) {
                    u = new User();
                    u.setUsername(username);
                    u.setPasswordHash(passwordEncoder.encode(username + "123"));
                    u.setFirstName(username.substring(0, 1).toUpperCase() + username.substring(1));
                    u.setLastName("User");
                    u.setEmail(username + "@medcore.health");
                    u.getRoles().add(role);
                    userRepo.save(u);
                }
            };

            String adminUsername = System.getenv("MEDCORE_ADMIN_USERNAME");
            if (adminUsername == null || adminUsername.isEmpty()) adminUsername = "admin";
            seedUser.accept(adminUsername, adminRole);
            seedUser.accept("doctor1", doctorRole);
            seedUser.accept("nurse1", nurseRole);
            seedUser.accept("pharmacist1", pharmacistRole);
            seedUser.accept("labtech1", labRole);
            seedUser.accept("rad1", radRole);
            seedUser.accept("biller1", billerRole);
            seedUser.accept("inv1", invRole);
            seedUser.accept("receptionist1", receptionistRole);

            System.out.println("Seeded all standard role accounts.");
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
