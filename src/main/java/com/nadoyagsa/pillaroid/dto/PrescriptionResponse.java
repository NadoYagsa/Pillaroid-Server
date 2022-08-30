package com.nadoyagsa.pillaroid.dto;

import com.nadoyagsa.pillaroid.entity.Appearance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PrescriptionResponse {
    private int medicineIdx;		// 의약품 번호
    private String name;	        // 제품명
    private Appearance appearance;  // 외형정보
    private String efficacy;        // 효능효과
    private String dosage;          // 용법용량

    private Long favoritesIdx;
    private AlarmResponse alarmResponse;
}
