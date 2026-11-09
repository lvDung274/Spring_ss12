package com.example.btthsession12.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplys")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Supply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String specification; // Thông số kỹ thuật

    private String provider; // Nhà sản xuất

    @Column(name = "unit", nullable = false)
    private String unit; // Đơn vị tính

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 0; // Mặc định bằng 0 khi khởi tạo

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
