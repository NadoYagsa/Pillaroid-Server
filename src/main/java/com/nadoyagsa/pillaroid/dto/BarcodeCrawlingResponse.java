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
public class BarcodeCrawlingResponse {
	private String productCode;	//품목일련번호(품목기준코드)
	private String productName;	//제품명
}
