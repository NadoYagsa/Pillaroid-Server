package com.nadoyagsa.pillaroid.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "medicine")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Medicine {
    @Id
    @Column(name = "medicine_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer medicineIdx;

    @Column(nullable = false, unique = true)
    private String name;            // 의약품 이름

    @Column(name = "serial_number", nullable = false, unique = true)
    private int serialNumber;       // 의약품 품목일련번호

    @Column(name = "standard_code")
    private String standardCode;    // 의약품 표준코드

    @Column
    private String efficacy;        // 효능효과

    @Column
    private String usage;           // 용법용량

    @Column
    private String precaution;     // 사용상 주의사항

    @Column
    private String ingredient;      // 성분정보

    @Column
    private String save;            // 저장방법
}
