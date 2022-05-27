package com.nadoyagsa.pillaroid.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MedicineResponse {
	private Long idx;					//품목일련번호(추후 idx로 변경 예정)
	private Long code;					//품목일련번호
	private String name;				//제품명
	private String appearanceInfo;      // 외형정보
	private String ingredient;      // 성분정보
	private String save;            // 저장방법
	private String efficacy;        // 효능효과
	private String usage;           // 용법용량
	private String precautions;     // 사용상 주의사항
}
