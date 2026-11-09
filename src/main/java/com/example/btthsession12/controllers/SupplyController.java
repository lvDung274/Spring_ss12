package com.example.btthsession12.controllers;

import com.example.btthsession12.models.dto.req.SupplyReq;
import com.example.btthsession12.models.dto.response.ApiResponse;
import com.example.btthsession12.models.dto.response.TopExportResponse;
import com.example.btthsession12.models.entities.Supply;
import com.example.btthsession12.services.DailyExportProjection;
import com.example.btthsession12.models.services.SupplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/supplies")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;

    // Chức năng 1: Thêm mới vật tư y tế
    @PostMapping
    public ResponseEntity<ApiResponse<Supply>> createSupply(@RequestBody SupplyReq request) {
        Supply created = supplyService.createSupply(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Tạo vật tư thành công", created));
    }

    // Chức năng 2: Cập nhật thông tin vật tư
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Supply>> updateSupply(
            @PathVariable Long id,
            @RequestBody Map<String, Object> rawBody) {

        // Ràng buộc: Từ chối nếu Body chứa id hoặc quantity
        if (rawBody.containsKey("id") || rawBody.containsKey("quantity")) {
            log.warn("Client cố tình gửi dữ liệu cấm (id hoặc quantity) khi cập nhật vật tư ID: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.success(HttpStatus.BAD_REQUEST.value(), "Không được phép cập nhật trường id hoặc quantity", null));
        }

        SupplyReq supplyRequest = new SupplyReq();
        supplyRequest.setName((String) rawBody.get("name"));
        supplyRequest.setSpecification((String) rawBody.get("specification"));
        supplyRequest.setProvider((String) rawBody.get("provider"));

        Supply updated = supplyService.updateSupply(id, supplyRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cập nhật vật tư thành công", updated));
    }

    // Chức năng 3: Xóa vật tư (Xóa mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        supplyService.softDeleteSupply(id);
        return ResponseEntity.noContent().build();
    }

    // Chức năng 4: Hiển thị danh sách vật tư hiện có (isDeleted = false)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Supply>>> getAllSupplies() {
        List<Supply> supplies = supplyService.getAllActiveSupplies();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Lấy danh sách thành công", supplies));
    }

    // Chức năng 5: Tìm kiếm vật tư theo tên (Tìm kiếm tương đối)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Supply>>> searchSupplies(@RequestParam("name") String name) {
        List<Supply> supplies = supplyService.searchSupplies(name);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Tìm kiếm thành công", supplies));
    }

    // Chức năng 6: Xuất vật tư y tế (Xuất kho)
    @PatchMapping("/{id}/export")
    public ResponseEntity<ApiResponse<Void>> exportSupply(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> requestBody) {
        Integer amount = requestBody.get("amount");
        supplyService.exportSupply(id, amount);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Xuất kho thành công", null));
    }

    // Chức năng 7: Nhập vật tư y tế (Nhập kho)
    @PatchMapping("/{id}/import")
    public ResponseEntity<ApiResponse<Void>> importSupply(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> requestBody) {
        Integer amount = requestBody.get("amount");
        supplyService.importSupply(id, amount);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Nhập kho thành công", null));
    }

    // Chức năng 8: Thống kê vật tư đã xuất kho trong ngày
    @GetMapping("/statistics/daily-export")
    public ResponseEntity<ApiResponse<List<DailyExportProjection>>> getDailyExportStatistics() {
        List<DailyExportProjection> stats = supplyService.getDailyExportStatistics();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Thống kê xuất kho trong ngày thành công", stats));
    }

    // Chức năng 9: Thống kê vật tư xuất kho nhiều nhất
    @GetMapping("/statistics/top-export")
    public ResponseEntity<ApiResponse<TopExportResponse>> getTopExportSupply() {
        TopExportResponse top = supplyService.getTopExportSupply();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Lấy thống kê top xuất kho thành công", top));
    }
}
