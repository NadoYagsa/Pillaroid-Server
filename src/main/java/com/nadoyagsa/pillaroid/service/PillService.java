package com.nadoyagsa.pillaroid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.PillModelResponse;
import com.nadoyagsa.pillaroid.dto.PillResponse;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Service
public class PillService {
    private final MedicineRepository medicineRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PillService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public Optional<PillResponse> getMedicineInfoByPillImage(MultipartFile pillImage) throws IOException {
        // 플라스크 서버로 이미지 전달 후 알약 조회
        URI uri = UriComponentsBuilder
                //TODO: 실험용으로 서버 주소 변경
                .fromUriString("http://ec2-15-165-205-12.ap-northeast-2.compute.amazonaws.com:5000")
                .path("/pill/predict")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource pillImageByteArray = new ByteArrayResource(pillImage.getBytes()){
            @Override
            public String getFilename(){
                return pillImage.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", pillImageByteArray);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        logger.info("1");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(uri, requestEntity, String.class);     // 요청 주소, 요청 바디, 응답 바디

            if (response.getStatusCodeValue() == 200) {
                ObjectMapper mapper = new ObjectMapper();

                // Json 결과를 Object로 변환
                PillModelResponse pillModelResponse = mapper.readValue(response.getBody(), PillModelResponse.class);

                // 가장 높은 확률의 알약 품목일련번호 반환
                int maxProbPillSerialNumber = pillModelResponse.getPredictions().get(0).getSerialNumber();

                Optional<Medicine> medicine = medicineRepository.findMedicineBySerialNumber(maxProbPillSerialNumber);
                if (medicine.isEmpty())
                    return Optional.empty();
                else
                    return Optional.ofNullable(medicine.get().toPillResponse());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400) {
                // 알약 크롭 이미지가 없음, 즉 알약이 존재하지 않음
                throw NotFoundException.MEDICINE_NOT_FOUND;
            }
            else if (e.getStatusCode().value() == 404) {
                // Flask 서버로 이미지가 전달되지 않음
                throw BadRequestException.BAD_PARAMETER;
            }
        }

        return Optional.empty();
    }
}
