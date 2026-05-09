package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String province; // Tỉnh/Thành phố

    @Column(nullable = false)
    private String district; // Quận/Huyện

    @Column(nullable = false)
    private String ward;     // Phường/Xã

    @Column(nullable = false)
    private String detail;   // Số nhà, tên đường
}