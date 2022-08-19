package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.PillResponse;
import com.nadoyagsa.pillaroid.service.PillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/pill")
public class PillController {
    private final PillService pillService;

    @Autowired
    public PillController(PillService pillService) {
        this.pillService = pillService;
    }

    // 의약품 중 알약 이미지로 정보 조회
    @PostMapping
    public ApiResponse<PillResponse> getPillInfo(@RequestParam MultipartFile pillImage) throws IllegalStateException, IOException {
        if (!pillImage.isEmpty() && pillImage.getOriginalFilename() != null && !pillImage.getOriginalFilename().equals("")) {
            Optional<PillResponse> medicineResponse = pillService.getMedicineInfoByPillImage(pillImage);

            if (medicineResponse.isPresent())   // 조회된 알약이 있을 시
                return ApiResponse.success(medicineResponse.get());
            else                                // 조회된 알약이 없을 시
                throw NotFoundException.MEDICINE_NOT_FOUND;
        }
        else
            throw BadRequestException.BAD_PARAMETER;
    }
}
