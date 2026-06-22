package com.medcore.his.repository.search;

import com.medcore.his.domain.search.DrugIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrugSearchRepository extends ElasticsearchRepository<DrugIndex, String> {
    List<DrugIndex> findByGenericNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(String genericName, String brandName);
}
