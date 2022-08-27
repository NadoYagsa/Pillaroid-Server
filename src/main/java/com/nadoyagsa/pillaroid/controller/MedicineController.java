package com.nadoyagsa.pillaroid.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import com.nadoyagsa.pillaroid.entity.Favorites;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.BarcodeService;
import com.nadoyagsa.pillaroid.service.MedicineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/medicine")
public class MedicineController {
    private final MedicineService medicineService;
    private final BarcodeService barcodeService;
    private final MedicineExcelUtils medicineExcelUtils;

    private final AuthTokenProvider authTokenProvider;

    // 의약품 번호로 정보 조회
    @GetMapping
    public ApiResponse<MedicineResponse> getMedicineInfo(HttpServletRequest request, @RequestParam int idx) {
        Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByIdx(idx);

        if (medicineResponse.isPresent()) {
            if (request.getHeader("authorization") != null) {       // 로그인 된 사용자라면 즐겨찾기 여부를 보여줌
                Long userIdx = findUserIdxByToken(request);
                Optional<Favorites> favorites = medicineService.findFavoritesByUserAndMedicineIdx(userIdx, idx);

                if (favorites.isPresent()) {    // 즐겨찾기 설정을 했을 시
                    medicineResponse.get().setFavoritesIdx(favorites.get().getFavoritesIdx());
                }
            }
            return ApiResponse.success(medicineResponse.get());
        }
        else
            throw NotFoundException.MEDICINE_NOT_FOUND;
    }

    // 의약품 용기(제품명, 바코드)로 정보 조회
    @GetMapping("/case")
    public ApiResponse<MedicineResponse> getMedicineInfo(HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode) throws IOException {
        if (name != null && !name.equals("")) {     // 제품명
            Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByCaseName(name);

            if (medicineResponse.isPresent()) {
                return reflectFavoritesAboutMedicine(request, medicineResponse.get());
            }
            else
                throw NotFoundException.MEDICINE_NOT_FOUND;
        } else if (barcode != null && !barcode.equals("")) {        // 바코드
            Optional<MedicineResponse> medicineResponse = medicineService.getMedicineInfoByStandardCode(barcode);

            if (medicineResponse.isPresent()) {
                return reflectFavoritesAboutMedicine(request, medicineResponse.get());
            }
            else {
                String serialNumber = barcodeService.crawlSerialNumber(barcode);  // 바코드 번호로 품목일련번호 크롤링
                medicineResponse = medicineService.getMedicineInfoBySerialNumber(Integer.parseInt(serialNumber));

                if (medicineResponse.isPresent()) {
                    return reflectFavoritesAboutMedicine(request, medicineResponse.get());
                }
                else
                    throw NotFoundException.BARCODE_NOT_FOUND;
            }
        }
        else 
            throw BadRequestException.BAD_PARAMETER;
    }

    // 음성을 통한 의약품명으로 의약품 리스트 조회
    @GetMapping("/voice")
    public ApiResponse<List<VoiceResponse>> getVoiceMedicineInfo(@RequestParam String name) {
        if (!name.strip().equals(""))
            return ApiResponse.success(medicineService.getMedicineListByName(name.strip()));
        else
            throw BadRequestException.BAD_PARAMETER;
    }

    // 처방전을 통한 의약품명으로 의약품 리스트 조회
    @GetMapping("/prescription")
    public ApiResponse<List<PrescriptionResponse>> getPrescriptionMedicineInfo(HttpServletRequest request, @RequestParam String names) {
        String[] nameList = names.split(",");

        if (nameList.length > 0) {
            List<PrescriptionResponse> medicineList;

            if (request.getHeader("authorization") != null) {       // 로그인 된 사용자라면 즐겨찾기 여부를 보여줌
                Long userIdx = findUserIdxByToken(request);
                medicineList = medicineService.getMedicineListByNameList(nameList, userIdx);
            }
            else {                                                        // 로그인하지 않은 사용자
                medicineList = medicineService.getMedicineListByNameList(nameList);
            }

            return ApiResponse.success(medicineList);
        }
        else
            throw BadRequestException.BAD_PARAMETER;
    }

    // 사용자 jwt 토큰으로부터 회원 정보 조회
    public Long findUserIdxByToken(HttpServletRequest request) {
        try {
            return authTokenProvider.getClaims(request.getHeader("authorization")).get("userId", Long.class);
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }

    private ApiResponse<MedicineResponse> reflectFavoritesAboutMedicine(HttpServletRequest request, MedicineResponse medicineResponse) {
        if (request.getHeader("authorization") != null) {       // 로그인 된 사용자라면 즐겨찾기 여부를 보여줌
            Long userIdx = findUserIdxByToken(request);
            Optional<Favorites> favorites = medicineService.findFavoritesByUserAndMedicineIdx(userIdx, medicineResponse.getMedicineIdx());

            if (favorites.isPresent()) {    // 즐겨찾기 설정을 했을 시
                medicineResponse.setFavoritesIdx(favorites.get().getFavoritesIdx());
            }
        }
        return ApiResponse.success(medicineResponse);
    }
}
