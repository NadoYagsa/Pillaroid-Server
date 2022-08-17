package com.nadoyagsa.pillaroid.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PillModelResponse {
    private boolean success;
    private List<Prediction> predictions;

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Prediction {
        private int serialNumber;		// 의약품 품목일련번호
        private double probability;     // 의약품 모델 일치 확률
    }
}
