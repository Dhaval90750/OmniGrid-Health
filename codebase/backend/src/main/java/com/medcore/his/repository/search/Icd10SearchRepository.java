package com.medcore.his.repository.search;

import com.medcore.his.domain.search.Icd10Index;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Icd10SearchRepository extends ElasticsearchRepository<Icd10Index, String> {
    List<Icd10Index> findByDescriptionContainingIgnoreCaseOrCodeContainingIgnoreCase(String description, String code);
}
