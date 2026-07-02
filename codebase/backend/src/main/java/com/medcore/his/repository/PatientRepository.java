package com.medcore.his.repository;

import com.medcore.his.domain.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    Optional<Patient> findByUhid(String uhid);
    
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.uhid) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.mobileNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Patient> searchPatients(String keyword);

    @Query("SELECT COUNT(p) FROM Patient p")
    long countTotalPatients();
    
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Patient> findFirstByMobileNumber(String mobileNumber);
    
    
    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(String firstName, String lastName, java.time.LocalDate dateOfBirth);
    Optional<Patient> findFirstByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(String firstName, String lastName, java.time.LocalDate dateOfBirth);

    @Query(value = "SELECT * FROM patients p WHERE " +
           "(similarity(p.first_name || ' ' || p.last_name, :fullName) > 0.6) OR " +
           "(p.mobile_number = :mobileNumber AND p.date_of_birth = :dateOfBirth)", nativeQuery = true)
    List<Patient> findPotentialDuplicates(
            @org.springframework.data.repository.query.Param("fullName") String fullName, 
            @org.springframework.data.repository.query.Param("mobileNumber") String mobileNumber, 
            @org.springframework.data.repository.query.Param("dateOfBirth") java.time.LocalDate dateOfBirth);

    @Query("SELECT p FROM Patient p WHERE " +
           "(:uhid IS NULL OR p.uhid = :uhid) AND " +
           "(:mobileNumber IS NULL OR p.mobileNumber = :mobileNumber) AND " +
           "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:nationalId IS NULL OR p.nationalId = :nationalId)")
    List<Patient> advancedSearchPatients(
            @org.springframework.data.repository.query.Param("uhid") String uhid, 
            @org.springframework.data.repository.query.Param("mobileNumber") String mobileNumber, 
            @org.springframework.data.repository.query.Param("firstName") String firstName, 
            @org.springframework.data.repository.query.Param("lastName") String lastName, 
            @org.springframework.data.repository.query.Param("nationalId") String nationalId);
}
