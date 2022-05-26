package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.dto.Medicine;
import com.nadoyagsa.pillaroid.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/medicine")
public class MedicineController {
    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    //TODO: 추후에 medicine명 혹은 품목일련번호로 전달받아야 함
    @GetMapping("/info")
    public Medicine getMedicineInfo(HttpServletRequest request) {
        return medicineService.getMedicineInfo();
    }
}
