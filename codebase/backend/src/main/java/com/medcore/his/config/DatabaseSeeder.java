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

import java.time.LocalDate;

@Configuration
public class DatabaseSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(PatientRepository patientRepo, 
                                   StaffProfileRepository staffRepo, 
                                   BloodInventoryRepository bloodRepo) {
        return args -> {
            if (staffRepo.count() == 0) {
                System.out.println("Seeding StaffProfiles...");
                
                StaffProfile s1 = new StaffProfile();
                s1.setEmployeeCode("EMP-001");
                s1.setFullName("Dr. Anjali Desai");
                s1.setRole("Consultant");
                s1.setDepartment("Pulmonology");
                s1.setContactNumber("+91-9800011122");
                s1.setIsActive(true);
                staffRepo.save(s1);

                StaffProfile s2 = new StaffProfile();
                s2.setEmployeeCode("EMP-002");
                s2.setFullName("Dr. Rahul Sharma");
                s2.setRole("Resident");
                s2.setDepartment("Cardiology");
                s2.setContactNumber("+91-9800011133");
                s2.setIsActive(true);
                staffRepo.save(s2);
                
                StaffProfile s3 = new StaffProfile();
                s3.setEmployeeCode("EMP-003");
                s3.setFullName("Nurse Priya Patel");
                s3.setRole("Nurse");
                s3.setDepartment("ICU");
                s3.setContactNumber("+91-9800011144");
                s3.setIsActive(true);
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
