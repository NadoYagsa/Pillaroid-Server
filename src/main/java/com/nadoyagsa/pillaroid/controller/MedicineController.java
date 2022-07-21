package com.nadoyagsa.pillaroid.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
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
    private final MedicineExcelUtils medicineExcelUtils;

    // 의약품 번호로 정보 조회
    @GetMapping
    public ApiResponse<MedicineResponse> getMedicineInfo(@RequestParam int idx) throws IOException {
        Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByIdx(idx);

        if (medicineResponse.isPresent())
            return ApiResponse.success(medicineResponse.get());
        else
            throw NotFoundException.MEDICINE_NOT_FOUND;
    }

    // 의약품 용기(제품명, 바코드)로 정보 조회
    @GetMapping("/case")
    public ApiResponse<MedicineResponse> getMedicineInfo(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode) throws IOException {
        if (name != null && !name.equals("")) {
            Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByCaseName(name);

            if (medicineResponse.isPresent())
                return ApiResponse.success(medicineResponse.get());
            else
                throw NotFoundException.MEDICINE_NOT_FOUND;

        } else if (barcode != null && !barcode.equals("")) {
            Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByStandardCode(barcode);

            if (medicineResponse.isPresent())
                return ApiResponse.success(medicineResponse.get());
            else {
                String serialNumber = barcodeService.crawlSerialNumber(barcode);  // 바코드 번호로 품목일련번호 크롤링
                medicineResponse = medicineService.getMedicineInfoBySerialNumber(Integer.parseInt(serialNumber));

                if (medicineResponse.isPresent())
                    return ApiResponse.success(medicineResponse.get());
                else
                    throw NotFoundException.BARCODE_NOT_FOUND;
            }
        }
        else 
            throw BadRequestException.BAD_PARAMETER;
    }

    // 음성을 통한 의약품명으로 의약품 리스트 조회
    @GetMapping("/voice")
    public ApiResponse<List<VoiceResponse>> getVoiceMedicineInfo(@RequestParam String name) throws IOException {
        if (!name.strip().equals(""))
            return ApiResponse.success(medicineService.getMedicineListByName(name.strip()));
        else
            throw BadRequestException.BAD_PARAMETER;
    }

    // 처방전을 통한 의약품명으로 의약품 리스트 조회
    @GetMapping("/prescription")
    public ApiResponse<List<PrescriptionResponse>> getPrescriptionMedicineInfo(@RequestParam String names) throws IOException {
        String[] nameList = names.split(",");

        if (nameList.length > 0)
            return ApiResponse.success(medicineService.getMedicineListByNameList(nameList));
        else
            throw BadRequestException.BAD_PARAMETER;
    }
}
