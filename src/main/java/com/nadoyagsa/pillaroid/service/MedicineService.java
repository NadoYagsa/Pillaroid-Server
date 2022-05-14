package com.nadoyagsa.pillaroid.service;

import com.nadoyagsa.pillaroid.component.JsoupComponent;
import com.nadoyagsa.pillaroid.dto.Medicine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {
    private final JsoupComponent jsoupComponent;

    @Autowired
    public MedicineService(JsoupComponent jsoupComponent) {
        this.jsoupComponent = jsoupComponent;
    }

    public Medicine getMedicineInfo() {
        return jsoupComponent.getMedicineInfo("");
    }
}
