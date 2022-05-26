package com.nadoyagsa.pillaroid.service;

import java.io.IOException;

import com.nadoyagsa.pillaroid.component.MedicineCrawlUtil;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.Medicine;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MedicineService {
    private final MedicineExcelUtils medicineExcelUtils;

    public MedicineResponse getMedicineInfoByCode(String code) throws IOException {
        return medicineExcelUtils.findMedicineExcelByCode(code);
    }

    public MedicineResponse getMedicineInfoByName(String name) throws IOException {
        return medicineExcelUtils.findMedicineExcelByName(name);
    }

    public boolean updateMedicineInfoInExcel() {
        try {
            medicineExcelUtils.updateMedicineExcel();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
