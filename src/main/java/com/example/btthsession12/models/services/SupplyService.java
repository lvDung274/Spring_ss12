package com.example.btthsession12.models.services;

import com.example.btthsession12.models.dto.req.SupplyReq;
import com.example.btthsession12.models.dto.response.TopExportResponse;
import com.example.btthsession12.models.entities.Supply;

import java.util.List;

public interface SupplyService {
    Supply createSupply(SupplyReq request);
    Supply updateSupply(Long id, SupplyReq request);
    void softDeleteSupply(Long id);
    List<Supply> getAllActiveSupplies();
    List<Supply> searchSupplies(String nameKeyword);
    void exportSupply(Long id, Integer amount);
    void importSupply(Long id, Integer amount);
    List<com.example.btthsession12.services.DailyExportProjection> getDailyExportStatistics();
    TopExportResponse getTopExportSupply();
}
