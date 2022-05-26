package com.nadoyagsa.pillaroid.service;

import com.nadoyagsa.pillaroid.component.MedicineCrawlUtil;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.Medicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MedicineService {
    private final MedicineCrawlUtil medicineCrawlUtil;
    private final MedicineExcelUtils medicineExcelUtils;

    @Autowired
    public MedicineService(MedicineCrawlUtil medicineCrawlUtil, MedicineExcelUtils medicineExcelUtils) {
        this.medicineCrawlUtil = medicineCrawlUtil;
        this.medicineExcelUtils = medicineExcelUtils;
    }

    public Medicine getMedicineInfo() {
        return medicineCrawlUtil.getMedicineInfo("");
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
