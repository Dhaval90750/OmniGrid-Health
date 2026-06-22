package com.medcore.his.domain.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "drugs")
public class DrugIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String genericName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String dosageForm;

    @Field(type = FieldType.Keyword)
    private String strength;

    @Field(type = FieldType.Keyword)
    private String classification;
}
