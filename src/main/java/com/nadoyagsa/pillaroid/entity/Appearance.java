package com.nadoyagsa.pillaroid.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "appearance")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Appearance {
    @Id
    @Column(name = "appearance_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appearanceIdx;

    @OneToOne
    @JoinColumn(name = "medicine_idx")
    private Medicine medicine;

    @Column
    private String feature;             // 성상

    @Column
    private String formulation;         // 제형

    @Column
    private String shape;               // 모양

    @Column
    private String color;               // 색상

    @Column(name = "dividing_line")
    private String dividingLine;        // 분할선

    @Column(name = "identification_mark")
    private String identificationMark;  // 식별표기
}
