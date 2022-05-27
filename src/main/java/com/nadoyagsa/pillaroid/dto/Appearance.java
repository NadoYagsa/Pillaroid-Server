package com.nadoyagsa.pillaroid.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
}
