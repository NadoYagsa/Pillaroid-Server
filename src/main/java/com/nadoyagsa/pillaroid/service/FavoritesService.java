package com.nadoyagsa.pillaroid.service;

import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.dto.FavoritesResponse;
import com.nadoyagsa.pillaroid.entity.Favorites;
import com.nadoyagsa.pillaroid.repository.FavoritesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;

    @Autowired
    public FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }

    // 즐겨찾기 번호로 조회
    public Optional<Favorites> findFavoritesByIdx(Long favoritesIdx) {
        return favoritesRepository.findById(favoritesIdx);
    }

    // 회원 번호로 즐겨찾기 목록 조회
    public List<FavoritesResponse> findFavoritesListByUserIdx(Long userIdx) {
        List<Favorites> favoritesList = favoritesRepository.findAllByUser_UserIdx(userIdx);

        List<FavoritesResponse> favoritesResponseList = new ArrayList<>();
        if (favoritesList.size() > 0)
            favoritesResponseList = favoritesList.stream().map(Favorites::toFavoritesResponse).collect(Collectors.toList());

        return favoritesResponseList;
    }

    // 의약품 키워드로 즐겨찾기 목록 조회
    public List<FavoritesResponse> findFavoritesListByKeyword(Long userIdx, String keyword) {
        List<Favorites> favoritesList = favoritesRepository.findFavoritesByKeyword(userIdx, keyword);

        List<FavoritesResponse> favoritesResponseList = new ArrayList<>();
        if (favoritesList.size() > 0) {
            favoritesResponseList = favoritesList.stream().map(Favorites::toFavoritesResponse).collect(Collectors.toList());
        }

        return favoritesResponseList;
    }

    // 즐겨찾기 추가
    public boolean saveFavorites(Favorites favorites) {
        try {
            favoritesRepository.save(favorites);
            return true;
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }

    // 즐겨찾기 해제
    public boolean deleteFavorites(Long favoritesIdx) {
        try {
            favoritesRepository.deleteById(favoritesIdx);
            return true;
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }
}
