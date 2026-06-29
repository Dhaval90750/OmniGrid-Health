package com.medcore.his.service;

import com.medcore.his.repository.AdmissionRepository;
import com.medcore.his.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.medcore.his.domain.clinical.Admission;
import com.medcore.his.service.RiskAlertService;
import java.util.ArrayList;

@Service
public class AnalyticsService {

    private final InvoiceRepository invoiceRepository;
    private final AdmissionRepository admissionRepository;
    private final RiskAlertService riskAlertService;
    private final com.medcore.his.repository.VisitRepository visitRepository;

    @Autowired
    public AnalyticsService(InvoiceRepository invoiceRepository, AdmissionRepository admissionRepository, RiskAlertService riskAlertService, com.medcore.his.repository.VisitRepository visitRepository) {
        this.invoiceRepository = invoiceRepository;
        this.admissionRepository = admissionRepository;
        this.riskAlertService = riskAlertService;
        this.visitRepository = visitRepository;
    }

    public Map<String, Object> getExecutiveOverview() {
        Map<String, Object> executive = new HashMap<>();
        
        // Revenue Today
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        BigDecimal todayRevenue = invoiceRepository.sumNetAmountsForDateRange(startOfDay, endOfDay);
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;
        
        executive.put("total_revenue_today", todayRevenue);
        
        // Active IPD Census
        long activeAdmissions = admissionRepository.countByStatus("ADMITTED");
        executive.put("active_ipd_census", activeAdmissions);
        
        // Bed Occupancy (Assuming 200 total beds for the dashboard metric calculation)
        double occupancy = (activeAdmissions / 200.0) * 100.0;
        executive.put("bed_occupancy_percent", Math.round(occupancy * 10.0) / 10.0);
        
        // Real ER Waiting Time calculation
        List<com.medcore.his.domain.clinical.Visit> erVisits = visitRepository.findAllActiveQueuesByDate(LocalDateTime.now());
        long totalWaitMins = 0;
        long erCount = 0;
        
        for (com.medcore.his.domain.clinical.Visit v : erVisits) {
            if (v.getDepartment() != null && v.getDepartment().getName().toLowerCase().contains("emergency")) {
                if ("WAITING".equals(v.getStatus())) {
                    long wait = java.time.Duration.between(v.getCreatedAt(), LocalDateTime.now()).toMinutes();
                    totalWaitMins += wait;
                    erCount++;
                }
            }
        }
        
        long avgWait = erCount > 0 ? (totalWaitMins / erCount) : 0;
        executive.put("er_waiting_time_mins", avgWait);
        
        return executive;
    }

    public Map<String, Object> getQualityKpis() {
        Map<String, Object> qualityKpis = new HashMap<>();
        
        List<Object[]> alosData = admissionRepository.findDischargedAdmissionsForAlos();
        double totalDays = 0;
        int count = 0;
        
        for (Object[] record : alosData) {
            LocalDateTime adm = (LocalDateTime) record[0];
            LocalDateTime dis = (LocalDateTime) record[1];
            long seconds = java.time.Duration.between(adm, dis).getSeconds();
            totalDays += (seconds / 86400.0);
            count++;
        }
        
        double alos = count > 0 ? (Math.round((totalDays / count) * 10.0) / 10.0) : 0.0;
        
        qualityKpis.put("average_length_of_stay", alos); // Days
        qualityKpis.put("hospital_acquired_infections", 0.0); // Real value defaults to 0 without infection table
        qualityKpis.put("surgical_site_infections", 0.0); // Real value defaults to 0
        qualityKpis.put("patient_satisfaction_score", 0.0); // Real value defaults to 0
        
        return qualityKpis;
    }

    public List<Map<String, Object>> getRevenueByDepartment() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        List<Object[]> opdRev = invoiceRepository.sumOpdRevenueByDepartment(startOfMonth, endOfMonth);
        List<Object[]> ipdRev = invoiceRepository.sumIpdRevenueByDepartment(startOfMonth, endOfMonth);
        
        Map<String, BigDecimal> combined = new HashMap<>();
        
        for (Object[] record : opdRev) {
            String dept = (String) record[0];
            BigDecimal rev = (BigDecimal) record[1];
            combined.put(dept, combined.getOrDefault(dept, BigDecimal.ZERO).add(rev));
        }
        
        for (Object[] record : ipdRev) {
            String dept = (String) record[0];
            BigDecimal rev = (BigDecimal) record[1];
            combined.put(dept, combined.getOrDefault(dept, BigDecimal.ZERO).add(rev));
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : combined.entrySet()) {
            result.add(Map.of("department", entry.getKey(), "revenue", entry.getValue()));
        }
        
        // Sort descending by revenue
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        
        return result;
    }
    
    public List<Map<String, Object>> getAiRiskAlerts() {
        List<Map<String, Object>> activeAlerts = new ArrayList<>();
        List<Admission> activeAdmissions = admissionRepository.findByStatusOrderByAdmissionDateDesc("ADMITTED");
        
        for (Admission admission : activeAdmissions) {
            Map<String, Object> sepsisRisk = riskAlertService.evaluateSepsisRisk(admission.getPatient().getId());
            if ("HIGH".equals(sepsisRisk.get("riskLevel"))) {
                activeAlerts.add(Map.of(
                    "patient_id", admission.getPatient().getUhid(),
                    "name", admission.getPatient().getFirstName() + " " + admission.getPatient().getLastName(),
                    "ward", admission.getWard().getName(),
                    "alert_type", "Sepsis Risk",
                    "probability", sepsisRisk.get("sirsScore"),
                    "status", "CRITICAL"
                ));
            }
        }
        
        return activeAlerts;
    }
}
