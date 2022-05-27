package com.nadoyagsa.pillaroid.controller;

import java.io.IOException;
import java.util.List;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import com.nadoyagsa.pillaroid.service.BarcodeService;
import com.nadoyagsa.pillaroid.service.MedicineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/medicine")
public class MedicineController {
    private final MedicineService medicineService;
    private final BarcodeService barcodeService;

    @GetMapping
    public ApiResponse<MedicineResponse> getMedicineInfo(
            @RequestParam(required = false) Long idx,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode) throws IOException {
        if (idx != null && !idx.equals("")) {
            return ApiResponse.success(medicineService.getMedicineInfoByCode(idx));    //TODO: 엑셀에서 조회할 때까진 품목일련번호를 idx 대신 받음 (메소드 변경 요망)
        } else if (barcode != null && !barcode.equals("")) {
            Long codeByBarcode = Long.valueOf(barcodeService.getProductCode(barcode));
            return ApiResponse.success(medicineService.getMedicineInfoByCode(codeByBarcode));
        } else if (name != null && !name.equals("")) {
            return ApiResponse.success(medicineService.getMedicineInfoByName(name));
        } else {
            throw BadRequestException.BAD_PARAMETER;
        }
    }

    @GetMapping("/voice")
    public ApiResponse<List<VoiceResponse>> getVoiceMedicineInfo(@RequestParam String name) throws IOException {
        return ApiResponse.success(medicineService.getMedicineListByName(name));
    }
}