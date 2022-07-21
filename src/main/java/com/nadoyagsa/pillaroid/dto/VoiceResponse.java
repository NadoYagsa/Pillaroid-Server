package com.nadoyagsa.pillaroid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VoiceResponse {
    private int medicineIdx;    // 의약품 번호
    private String name;	    // 제품명
}
