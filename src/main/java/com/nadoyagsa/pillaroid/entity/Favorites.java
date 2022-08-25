package com.nadoyagsa.pillaroid.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nadoyagsa.pillaroid.dto.FavoritesResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "favorites")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Favorites {
    @Id
    @Column(name = "favorites_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoritesIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "medicine_idx")
    private Medicine medicine;

    @JsonIgnore
    public FavoritesResponse toFavoritesResponse() {
        return FavoritesResponse.builder()
                .favoritesIdx(favoritesIdx)
                .medicineName(medicine.getName())
                .build();
    }
}
