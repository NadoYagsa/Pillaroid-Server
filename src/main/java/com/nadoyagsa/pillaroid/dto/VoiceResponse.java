package com.nadoyagsa.pillaroid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VoiceResponse {
    private Long idx;		//품목일련번호(추후 idx로 변경 예정)
    private String name;	//제품명
}
