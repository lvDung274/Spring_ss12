package com.example.btthsession12.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopExportResponse {
    private String topSupplyName;
    private Long totalExportQuantity;
}
