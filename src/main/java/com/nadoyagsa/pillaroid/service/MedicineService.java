package com.nadoyagsa.pillaroid.service;

import java.io.IOException;
import java.util.List;

import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;

import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MedicineService {
    private final MedicineExcelUtils medicineExcelUtils;

    public MedicineResponse getMedicineInfoByCode(Long code) throws IOException {
        return medicineExcelUtils.findMedicineExcelByCode(code);
    }

    public MedicineResponse getMedicineInfoByName(String name) throws IOException {
        return medicineExcelUtils.findMedicineExcelByName(name);
    }

    public List<VoiceResponse> getMedicineListByName(String name) throws IOException {
        return medicineExcelUtils.findVoiceMedicineListByName(name);
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
