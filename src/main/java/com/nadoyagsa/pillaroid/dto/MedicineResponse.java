package com.nadoyagsa.pillaroid.dto;

import com.nadoyagsa.pillaroid.entity.Appearance;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MedicineResponse {
	private int medicineIdx;		// 의약품 번호
	private String name;            // 의약품 이름
	private Appearance appearance;	// 외형 정보
	private String efficacy;        // 효능효과
	private String dosage;          // 용법용량
	private String precaution;     	// 사용상 주의사항
	private String ingredient;      // 성분정보
	private String save;            // 저장방법

	@Setter
	private Long favoritesIdx = null;

	@Setter
	private AlarmResponse alarmResponse = null;
}
