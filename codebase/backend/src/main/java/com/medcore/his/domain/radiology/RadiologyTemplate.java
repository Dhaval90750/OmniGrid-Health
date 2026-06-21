package com.medcore.his.domain.radiology;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "radiology_templates")
@Getter
@Setter
public class RadiologyTemplate extends BaseEntity {

    @Column(name = "template_name", nullable = false, length = 255)
    private String templateName;

    @Column(nullable = false, length = 50)
    private String modality;

    @Column(name = "body_part", nullable = false, length = 100)
    private String bodyPart;

    @Column(name = "content_template", columnDefinition = "TEXT", nullable = false)
    private String contentTemplate;
}
