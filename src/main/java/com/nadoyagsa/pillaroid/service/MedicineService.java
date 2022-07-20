package com.nadoyagsa.pillaroid.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;

import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

    private final MedicineExcelUtils medicineExcelUtils;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository, MedicineExcelUtils medicineExcelUtils) {
        this.medicineRepository = medicineRepository;
        this.medicineExcelUtils = medicineExcelUtils;
    }

    public Optional<MedicineResponse> getMedicineInfoByIdx(int idx) throws IOException {
        Optional<Medicine> medicine = medicineRepository.findById(idx);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public Optional<MedicineResponse> getMedicineInfoByCaseName(String name) throws IOException {
        Optional<Medicine> medicine = medicineRepository.findFirstByNameStartingWith(name);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public Optional<MedicineResponse> getMedicineInfoByStandardCode(String barcode) throws IOException {
        Optional<Medicine> medicine = medicineRepository.findMedicineByStandardCode(barcode);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public List<VoiceResponse> getMedicineListByName(String name) throws IOException {
        List<Medicine> medicineList = medicineRepository.findAllByNameContaining(name);

        if (medicineList.size() == 0)
            throw NotFoundException.MEDICINE_NOT_FOUND;
        else
            return medicineList.stream().map(Medicine::toVoiceResponse).collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getMedicineListByNameList(String[] nameList) throws IOException {
        List<PrescriptionResponse> medicineList = new ArrayList<>();
        for (String name : nameList) {
            Optional<Medicine> medicine = medicineRepository.findFirstByNameStartingWith(name);

            if (medicine.isPresent())
                medicineList.add(medicine.get().toPrescriptionResponse());
        }
        return medicineList;
    }

    public boolean updateMedicineInfoInExcel() {    //TODO: DB에 새로운 알약 정보 추가해야 함!
        try {
            medicineExcelUtils.updateMedicineExcel();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
