package com.nadoyagsa.pillaroid.dto;

import com.nadoyagsa.pillaroid.entity.Favorites;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoritesDTO {
    private long userIdx = 0L;
    private int medicineIdx;

    public Favorites toFavoritesEntity() {
        User user = User.builder()
                .userIdx(userIdx)
                .build();

        Medicine medicine = Medicine.builder()
                .medicineIdx(medicineIdx)
                .build();

        return Favorites.builder()
                .user(user)
                .medicine(medicine)
                .build();
    }
}
