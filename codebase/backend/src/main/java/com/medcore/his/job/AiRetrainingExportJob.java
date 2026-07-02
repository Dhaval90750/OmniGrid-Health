package com.medcore.his.job;

import com.medcore.his.domain.clinical.ClinicalNote;
import com.medcore.his.repository.ClinicalNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AiRetrainingExportJob {

    private final ClinicalNoteRepository clinicalNoteRepository;

    @Autowired
    public AiRetrainingExportJob(ClinicalNoteRepository clinicalNoteRepository) {
        this.clinicalNoteRepository = clinicalNoteRepository;
    }

    /**
     * Runs quarterly on the 1st of Jan, Apr, Jul, Oct at 3:00 AM.
     * Exports verified clinical notes to JSONL for Mistral/Llama fine-tuning.
     */
    @Scheduled(cron = "0 0 3 1 1,4,7,10 ?")
    public void exportRetrainingData() {
        // Fetch notes from the last 90 days that were verified by a human
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        List<ClinicalNote> verifiedNotes = clinicalNoteRepository.findByIsFinalizedTrueAndCreatedAtGreaterThanEqual(ninetyDaysAgo);
        
        if (verifiedNotes.isEmpty()) {
            return;
        }

        String fileName = "ai_retraining_export_" + System.currentTimeMillis() + ".jsonl";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (ClinicalNote note : verifiedNotes) {
                // Escape quotes and newlines for proper JSONL format
                String combined = (note.getHistoryOfPresentIllness() != null ? note.getHistoryOfPresentIllness() : "") + " "
                                + (note.getPastMedicalHistory() != null ? note.getPastMedicalHistory() : "") + " "
                                + (note.getObjectiveNotes() != null ? note.getObjectiveNotes() : "") + " "
                                + (note.getTreatmentPlan() != null ? note.getTreatmentPlan() : "");
                String text = combined.replace("\"", "\\\"").replace("\n", "\\n");
                
                // Construct a basic JSON structure for instruct-tuning
                String jsonl = String.format("{\"instruction\": \"Summarize the following clinical encounter\", \"output\": \"%s\"}", text);
                writer.println(jsonl);
            }
            System.out.println("Exported " + verifiedNotes.size() + " records to " + fileName + " for quarterly AI retraining.");
        } catch (IOException e) {
            System.err.println("Failed to write AI retraining export: " + e.getMessage());
        }
    }
}
