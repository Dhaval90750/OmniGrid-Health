package com.medcore.his.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.medcore.his.domain.clinical.Prescription;
import com.medcore.his.domain.clinical.PrescriptionLine;
import com.medcore.his.domain.patient.Patient;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    public byte[] generatePatientQrCardPdf(Patient patient, byte[] qrImageBytes) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("MedCore Hospital - Patient ID Card"));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Name: " + patient.getFirstName() + " " + patient.getLastName()));
            document.add(new Paragraph("UHID: " + patient.getUhid()));
            document.add(new Paragraph("DOB: " + patient.getDateOfBirth()));
            document.add(new Paragraph("Blood Group: " + (patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A")));
            
            document.add(new Paragraph(" "));

            if (qrImageBytes != null && qrImageBytes.length > 0) {
                Image qrImage = Image.getInstance(qrImageBytes);
                qrImage.scaleAbsolute(150, 150);
                document.add(qrImage);
            }

            document.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error generating PDF document", e);
        }

        return out.toByteArray();
    }
    public byte[] generatePrescriptionPdf(Prescription prescription) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("MedCore Hospital - Clinical Prescription"));
            document.add(new Paragraph("--------------------------------------------------"));
            
            Patient patient = prescription.getPatient();
            document.add(new Paragraph("Patient: " + patient.getFirstName() + " " + patient.getLastName() + " | UHID: " + patient.getUhid()));
            document.add(new Paragraph("Doctor: " + prescription.getDoctor().getUsername()));
            document.add(new Paragraph("Date: " + prescription.getCreatedAt().toLocalDate()));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            
            table.addCell(new PdfPCell(new Paragraph("Drug")));
            table.addCell(new PdfPCell(new Paragraph("Dosage")));
            table.addCell(new PdfPCell(new Paragraph("Frequency")));
            table.addCell(new PdfPCell(new Paragraph("Duration")));
            
            for (PrescriptionLine line : prescription.getLines()) {
                table.addCell(new PdfPCell(new Paragraph(line.getCustomDrugName())));
                table.addCell(new PdfPCell(new Paragraph(line.getDosage() + " " + line.getRoute())));
                table.addCell(new PdfPCell(new Paragraph(line.getFrequency())));
                table.addCell(new PdfPCell(new Paragraph(line.getDurationDays() + " days")));
            }
            
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Notes: " + (prescription.getNotes() != null ? prescription.getNotes() : "")));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Doctor Signature"));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating Prescription PDF", e);
        }

        return out.toByteArray();
    }
}
