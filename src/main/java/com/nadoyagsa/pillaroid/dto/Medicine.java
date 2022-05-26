package com.nadoyagsa.pillaroid.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {
    private Appearance appearanceInfo;      // 외형정보(성상)
    private String ingredient;              // 성분정보
    private String save;                    // 저장방법
    private String efficacy;                // 효능효과
    private String usage;                   // 용법용량
    private String precautions;             // 사용상 주의사항
}
