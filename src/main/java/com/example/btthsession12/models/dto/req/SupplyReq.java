package com.example.btthsession12.models.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyReq {
    private String name;
    private String specification;
    private String provider;
    private String unit;
}