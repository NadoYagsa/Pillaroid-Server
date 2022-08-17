package com.nadoyagsa.pillaroid.service;

import com.nadoyagsa.pillaroid.dto.MedicineResponse;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Optional;

@Service
public class PillService {
    private final MedicineRepository medicineRepository;

    @Autowired
    public PillService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public Optional<MedicineResponse> getMedicineInfoByPillImage(File pillFile) {
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

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", pillFile);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);     //파라미터 1요청 주소 , 2요청 바디 , 3응답 바디

        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody());

        response.getBody();

        // TODO: body의 내용을 json으로 변환
        /*
        Optional<Medicine> medicine = medicineRepository.findMedicineBySerialNumber(serialNumber);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
         */
        return Optional.empty();
    }
}
