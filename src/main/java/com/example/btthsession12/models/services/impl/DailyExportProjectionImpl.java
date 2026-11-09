package com.example.btthsession12.models.services.impl;
import com.example.btthsession12.models.services.SupplyService;
import com.example.btthsession12.services.DailyExportProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyExportProjectionImpl implements DailyExportProjection {
    private final SupplyService supplyService;

    @Override
    public Long getSupplyId() {
        return 0L;
    }

    @Override
    public String getSupplyName() {
        return "";
    }

    @Override
    public Long getTotalExported() {
        return 0L;
    }
}
