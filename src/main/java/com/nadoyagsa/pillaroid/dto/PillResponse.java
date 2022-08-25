package com.nadoyagsa.pillaroid.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PillResponse {
	private int medicineIdx;		// 의약품 번호
}
