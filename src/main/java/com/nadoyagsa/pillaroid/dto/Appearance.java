package com.nadoyagsa.pillaroid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appearance {
    private String appearance;          // 성상
    private String formulation;         // 제형
    private String shape;               // 모양
    private String color;               // 색상
    private String dividingLine;        // 분할선
    private String identificationMark;  // 식별표기

    public String toString() {
        return "성상: " + appearance + "\\n" +
                "제형: " + formulation + "\\n" +
                "모양: " + shape + "\\n" +
                "색상: " + color + "\\n" +
                "분할선: " + dividingLine + "\\n" +
                "식별표기: " + identificationMark;
    }
}
