package com.example.btthsession12.models.services.impl;

import com.example.btthsession12.models.dto.req.SupplyReq;
import com.example.btthsession12.models.dto.response.TopExportResponse;
import com.example.btthsession12.models.entities.InventoryTransaction;
import com.example.btthsession12.models.entities.Supply;
import com.example.btthsession12.models.entities.TransactionType;
import com.example.btthsession12.repositories.InventoryTransactionRepository;
import com.example.btthsession12.models.repositories.SupplyRepository;
import com.example.btthsession12.services.DailyExportProjection;
import com.example.btthsession12.models.services.SupplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyServiceImpl implements SupplyService {
    private final SupplyRepository supplyRepository;
    private final InventoryTransactionRepository transactionRepository;

    // Logger riêng cho lịch sử kho (Chức năng 7 - ghi vào history.log)
    private static final Logger historyLog = LoggerFactory.getLogger("HISTORY_LOGGER");

    // Chức năng 1: Thêm mới vật tư y tế
    @Override
    @Transactional
    public Supply createSupply(SupplyReq request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên vật tư không được để trống");
        }

        Supply supply = Supply.builder()
                .name(request.getName())
                .specification(request.getSpecification())
                .provider(request.getProvider())
                .unit(request.getUnit())
                .quantity(0) // Mặc định quantity = 0
                .isDeleted(false)
                .build();

        Supply saved = supplyRepository.save(supply);
        log.info("Đã tạo mới vật tư: [{}] với ID: [{}]", saved.getName(), saved.getId());
        return saved;
    }

    // Chức năng 2: Cập nhật thông tin vật tư
    @Override
    @Transactional
    public Supply updateSupply(Long id, SupplyReq request) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư với ID: " + id));

        supply.setName(request.getName());
        supply.setSpecification(request.getSpecification());
        supply.setProvider(request.getProvider());

        return supplyRepository.save(supply);
    }

    // Chức năng 3: Xóa vật tư (Xóa mềm)
    @Override
    @Transactional
    public void softDeleteSupply(Long id) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vật tư không tồn tại hoặc đã bị xóa trước đó"));
        supply.setIsDeleted(true);
        supplyRepository.save(supply);
    }

    // Chức năng 4: Hiển thị danh sách vật tư hiện có
    @Override
    public List<Supply> getAllActiveSupplies() {
        List<Supply> supplies = supplyRepository.findByIsDeletedFalse();
        log.debug("Đã truy vấn thành công {} bản ghi vật tư hoạt động từ Database", supplies.size());
        return supplies;
    }

    // Chức năng 5: Tìm kiếm vật tư theo tên (Tìm kiếm tương đối)
    @Override
    public List<Supply> searchSupplies(String nameKeyword) {
        List<Supply> results = supplyRepository.findAllByNameContainingIgnoreCase(nameKeyword);
        if (results.isEmpty()) {
            log.info("Không tìm thấy vật tư nào khớp với từ khóa: [{}]", nameKeyword);
        }
        return results;
    }

    // Chức năng 6: Xuất vật tư y tế (Xuất kho)
    @Override
    @Transactional
    public void exportSupply(Long id, Integer amount) {
        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư"));

        if (supply.getQuantity() < amount) {
            log.error("Thất bại khi xuất kho ID [{}]: Yêu cầu [{}], hiện có [{}]", id, amount, supply.getQuantity());
            throw new IllegalArgumentException("Số lượng tồn kho không đủ để xuất");
        }

        supply.setQuantity(supply.getQuantity() - amount);
        supplyRepository.save(supply);

        InventoryTransaction tx = InventoryTransaction.builder()
                .supply(supply)
                .type(TransactionType.EXPORT)
                .amount(amount)
                .build();
        transactionRepository.save(tx);
    }

    // Chức năng 7: Nhập vật tư y tế (Nhập kho)
    @Override
    @Transactional
    public void importSupply(Long id, Integer amount) {
        Supply supply = supplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư"));

        int oldQuantity = supply.getQuantity();
        supply.setQuantity(oldQuantity + amount);
        supplyRepository.save(supply);

        // Ghi log vào file history.log phục vụ đối soát kho
        historyLog.info("Nhập kho ID [{}], số lượng [+{}] , tồn cũ [{}]", id, amount, oldQuantity);

        InventoryTransaction tx = InventoryTransaction.builder()
                .supply(supply)
                .type(TransactionType.IMPORT)
                .amount(amount)
                .build();
        transactionRepository.save(tx);
    }

    // Chức năng 8: Thống kê vật tư đã xuất kho trong ngày
    @Override
    public List<DailyExportProjection> getDailyExportStatistics() {
        log.info("Thời điểm bắt đầu chạy thống kê xuất kho trong ngày: {}", LocalDateTime.now());
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<DailyExportProjection> stats = transactionRepository.getDailyExportStats(
                TransactionType.EXPORT, startOfDay, endOfDay
        );
        log.info("Thời gian hoàn thành thống kê xuất kho trong ngày: {}", LocalDateTime.now());
        return stats;
    }

    // Chức năng 9: Thống kê vật tư xuất kho nhiều nhất
    @Override
    public TopExportResponse getTopExportSupply() {
        List<TopExportResponse> topList = transactionRepository.findTopExportSupply(TransactionType.EXPORT);
        if (topList.isEmpty()) {
            throw new RuntimeException("Chưa có dữ liệu giao dịch để thống kê");
        }
        return topList.get(0);
    }
}
