package com.nadoyagsa.pillaroid.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineCrawl {
    private AppearanceCrawl appearanceInfo;      // 외형정보
    private String ingredient;      // 성분정보
    private String save;            // 저장방법
    private String efficacy;        // 효능효과
    private String dosage;          // 용법용량
    private String precaution;      // 사용상 주의사항
}
