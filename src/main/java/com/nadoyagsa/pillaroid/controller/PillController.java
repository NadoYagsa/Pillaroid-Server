package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.service.PillService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping(value = "/pill")
public class PillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PillService pillService;

    @Autowired
    public PillController(PillService pillService) {
        this.pillService = pillService;
    }

    // 의약품 중 알약 이미지로 정보 조회
    @PostMapping
    public ApiResponse<MedicineResponse> getPillInfo(@RequestParam MultipartFile pillImage) throws IllegalStateException, IOException {
        if (!pillImage.isEmpty() && pillImage.getOriginalFilename() != null && !pillImage.getOriginalFilename().equals("")) {
            logger.info(pillImage.getOriginalFilename());
            String filename = pillImage.getOriginalFilename();
            String fileExtension = filename.substring(filename.lastIndexOf("."));

            // 파일 임시 저장 디렉토리 경로 설정
            // path = getServletContext().getRealPath("/") + files/;             // ????????!??!!?!??!!?
            try {
                URI saveDirPathURI = getClass().getClassLoader().getResource("data/pill_temp").toURI();
                String saveDirPathString = Paths.get(saveDirPathURI).toString();

                // 저장 파일명 정의 (중복 최소화하기 위함)
                String saveFilePath = saveDirPathString.concat(String.format("\\%s_%s", System.currentTimeMillis(), filename));

                while (true) {
                    Path tempPath = Paths.get(saveFilePath);

                    // 같은 이름의 파일 존재 여부 확인
                    if (Files.exists(tempPath) && Files.isRegularFile(tempPath)) {      // 같은 시간에 같은 파일 이름을 저장됨
                        // 이름 중복을 막기 위해 파일명에 _1을 계속해서 덧붙임
                        saveFilePath = saveFilePath.replace(fileExtension, "_1" + fileExtension);
                    }
                    else {
                        break;
                    }
                }
                logger.info(saveFilePath);

                // 저장 경로에 파일 저장
                File savePillFile = new File(saveFilePath);
                pillImage.transferTo(savePillFile);

                // 파일 생성 여부 체크
                if (savePillFile.exists()) {
                    logger.info("파일 생성 완료");

                    // TODO: 확인 요망!
                    Optional<MedicineResponse> medicineResponse = pillService.getMedicineInfoByPillImage(savePillFile);

                    savePillFile.delete();      // 파일 삭제

                    if (medicineResponse.isPresent())
                        return ApiResponse.success(medicineResponse.get());
                    else
                        throw NotFoundException.MEDICINE_NOT_FOUND;
                }
                else
                    throw InternalServerException.INTERNAL_ERROR;

            } catch (URISyntaxException e) {
                throw InternalServerException.INTERNAL_ERROR;
            }
        }
        else
            throw BadRequestException.BAD_PARAMETER;
    }
}
