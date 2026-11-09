package com.example.btthsession12.services;

public interface DailyExportProjection {
    Long getSupplyId();
    String getSupplyName();
    Long getTotalExported();
}