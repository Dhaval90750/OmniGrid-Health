package com.medcore.his.domain.operations;

import com.medcore.his.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_logs")
@Getter
@Setter
public class WasteLog extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 50)
    private String wasteCategory; // RED, YELLOW, BLUE, WHITE, BLACK

    @Column(nullable = false)
    private java.math.BigDecimal weightKg;

    @Column(nullable = false)
    private LocalDateTime collectionTime;

    @Column
    private LocalDateTime disposalTime;

    @Column(nullable = false)
    private String status = "COLLECTED"; // COLLECTED, IN_TRANSIT, DISPOSED
}
