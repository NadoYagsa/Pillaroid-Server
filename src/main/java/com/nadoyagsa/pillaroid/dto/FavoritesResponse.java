package com.nadoyagsa.pillaroid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FavoritesResponse {
    private long favoritesIdx;      // 즐겨찾기 번호
    private String medicineName;    // 의약품 이름
}
