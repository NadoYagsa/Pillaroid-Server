package com.nadoyagsa.pillaroid.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.service.BarcodeService;
import com.nadoyagsa.pillaroid.dto.BarcodeCrawlingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/barcode")
public class BarcodeController {

	private final BarcodeService barcodeService;

	@GetMapping
	public ApiResponse<BarcodeCrawlingResponse> getProductCode(@RequestParam String barcode, HttpServletResponse response) throws IOException {	//TODO: 품목기준코드로 의약품 조회하는 컨트롤러로 리다이렉트 (안드로이드에선 Medicine 객체를 받도록)
		return ApiResponse.success(barcodeService.getProductCode(barcode));	//TODO: getProductCode 메서드는 추후, 품목기준코드만 반환하면 됨
	}
}
