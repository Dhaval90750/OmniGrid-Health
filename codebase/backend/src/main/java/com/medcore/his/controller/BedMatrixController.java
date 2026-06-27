package com.medcore.his.controller;

import com.medcore.his.domain.master.Bed;
import com.medcore.his.domain.master.Ward;
import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.repository.BedRepository;
import com.medcore.his.repository.WardRepository;
import com.medcore.his.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/admin/bed-matrix")
public class BedMatrixController {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final AdmissionService admissionService;

    @Autowired
    public BedMatrixController(WardRepository wardRepository, BedRepository bedRepository, AdmissionService admissionService) {
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
        this.admissionService = admissionService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getBedMatrix() {
        List<Ward> wards = wardRepository.findAll();
        List<Bed> allBeds = bedRepository.findAll();
        List<Admission> activeAdmissions = admissionService.getActiveAdmissions();

        Map<UUID, String> bedToPatientMap = new HashMap<>();
        for (Admission admission : activeAdmissions) {
            if (admission.getBed() != null && admission.getPatient() != null) {
                bedToPatientMap.put(
                    admission.getBed().getId(),
                    admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName()
                );
            }
        }

        Map<Ward, List<Bed>> bedsByWard = allBeds.stream()
                .filter(bed -> bed.getWard() != null)
                .collect(Collectors.groupingBy(Bed::getWard));

        List<Map<String, Object>> responseWards = new ArrayList<>();
        
        // Ensure even empty wards are listed
        for (Ward ward : wards) {
            List<Bed> wardBeds = bedsByWard.getOrDefault(ward, new ArrayList<>());
            long occupiedCount = wardBeds.stream().filter(b -> "OCCUPIED".equalsIgnoreCase(b.getStatus())).count();
            
            List<Map<String, Object>> bedDetails = wardBeds.stream().map(bed -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bed.getId());
                map.put("bedId", bed.getBedNumber());
                map.put("status", bed.getStatus());
                map.put("patient", bedToPatientMap.getOrDefault(bed.getId(), ""));
                return map;
            }).collect(Collectors.toList());

            Map<String, Object> wardData = new HashMap<>();
            wardData.put("wardId", ward.getId());
            wardData.put("wardName", ward.getName());
            wardData.put("totalBeds", wardBeds.size());
            wardData.put("occupied", occupiedCount);
            wardData.put("beds", bedDetails);
            
            responseWards.add(wardData);
        }

        return ResponseEntity.ok(Map.of("wards", responseWards));
    }
}
