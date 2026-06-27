package com.medcore.his.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class PublicQueueResponseDTO {
    private UUID doctorId;
    private String doctorName;
    private String department;
    private String room;
    private Integer currentToken;
    private List<Integer> nextTokens;
}
