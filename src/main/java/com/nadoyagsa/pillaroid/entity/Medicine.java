package com.nadoyagsa.pillaroid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.dto.NotificationResponse;
import com.nadoyagsa.pillaroid.dto.PillResponse;
import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
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

    @JsonIgnore
    @Column(name = "serial_number", nullable = false, unique = true)
    private int serialNumber;       // 의약품 품목일련번호

    @JsonIgnore
    @Column(name = "standard_code")
    private String standardCode;    // 의약품 표준코드

    @OneToOne
    @JoinColumn(name = "appearance_idx")
    private Appearance appearance;  // 외형 정보

    @Column
    private String efficacy;        // 효능효과

    @Column
    private String dosage;          // 용법용량

    @Column
    private String precaution;      // 사용상 주의사항

    @Column
    private String ingredient;      // 성분정보

    @Column
    private String save;            // 저장방법

    @JsonIgnore
    public MedicineResponse toMedicineResponse() {
        return MedicineResponse.builder()
                .medicineIdx(medicineIdx)
                .name(name)
                .appearance(appearance)
                .efficacy(efficacy)
                .dosage(dosage)
                .precaution(precaution)
                .ingredient(ingredient)
                .save(save)
                .build();
    }

    @JsonIgnore
    public PillResponse toPillResponse() {
        return PillResponse.builder()
                .medicineIdx(medicineIdx)
                .build();
    }

    @JsonIgnore
    public VoiceResponse toVoiceResponse() {
        return VoiceResponse.builder()
                .medicineIdx(medicineIdx)
                .name(name)
                .build();
    }

    @JsonIgnore
    public PrescriptionResponse toPrescriptionResponse(Long favoritesIdx, NotificationResponse notificationResponse) {
        return PrescriptionResponse.builder()
                .medicineIdx(medicineIdx)
                .name(name)
                .appearance(appearance)
                .efficacy(efficacy)
                .dosage(dosage)
                .favoritesIdx(favoritesIdx)
                .notificationResponse(notificationResponse)
                .build();
    }
}
