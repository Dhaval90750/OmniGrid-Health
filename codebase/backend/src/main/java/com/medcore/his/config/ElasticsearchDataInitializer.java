package com.medcore.his.config;

import com.medcore.his.domain.search.DrugIndex;
import com.medcore.his.domain.search.Icd10Index;
import com.medcore.his.repository.search.DrugSearchRepository;
import com.medcore.his.repository.search.Icd10SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ElasticsearchDataInitializer implements CommandLineRunner {

    private final Icd10SearchRepository icd10SearchRepository;
    private final DrugSearchRepository drugSearchRepository;

    @Autowired
    public ElasticsearchDataInitializer(Icd10SearchRepository icd10SearchRepository, DrugSearchRepository drugSearchRepository) {
        this.icd10SearchRepository = icd10SearchRepository;
        this.drugSearchRepository = drugSearchRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadIcd10Data();
        loadDrugData();
    }

    private void loadIcd10Data() {
        try {
            if (icd10SearchRepository.count() == 0) {
                System.out.println("Initializing ICD-10 Elasticsearch index from CSV...");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("icd10.csv").getInputStream()))) {
                    String line;
                    boolean header = true;
                    List<Icd10Index> buffer = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
                        if (header) {
                            header = false;
                            continue;
                        }
                        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        if (values.length >= 3) {
                            Icd10Index index = new Icd10Index();
                            index.setId(UUID.randomUUID().toString());
                            index.setCode(values[0].replace("\"", ""));
                            index.setDescription(values[1].replace("\"", ""));
                            index.setCategory(values[2].replace("\"", ""));
                            buffer.add(index);
                        }
                    }
                    icd10SearchRepository.saveAll(buffer);
                    System.out.println("Loaded " + buffer.size() + " ICD-10 codes.");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load ICD-10 data: Elasticsearch unreachable - " + e.getMessage());
        }
    }

    private void loadDrugData() {
        try {
            if (drugSearchRepository.count() == 0) {
                System.out.println("Initializing Drugs Elasticsearch index from CSV...");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("drugs.csv").getInputStream()))) {
                    String line;
                    boolean header = true;
                    List<DrugIndex> buffer = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
                        if (header) {
                            header = false;
                            continue;
                        }
                        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        if (values.length >= 5) {
                            DrugIndex index = new DrugIndex();
                            index.setId(UUID.randomUUID().toString());
                            index.setGenericName(values[0].replace("\"", ""));
                            index.setBrandName(values[1].replace("\"", ""));
                            index.setDosageForm(values[2].replace("\"", ""));
                            index.setStrength(values[3].replace("\"", ""));
                            index.setClassification(values[4].replace("\"", ""));
                            buffer.add(index);
                        }
                    }
                    drugSearchRepository.saveAll(buffer);
                    System.out.println("Loaded " + buffer.size() + " Drugs.");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load Drugs data: Elasticsearch unreachable - " + e.getMessage());
        }
    }
}
