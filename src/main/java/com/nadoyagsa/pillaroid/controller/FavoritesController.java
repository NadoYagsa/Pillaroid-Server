package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;
import com.nadoyagsa.pillaroid.dto.FavoritesDTO;
import com.nadoyagsa.pillaroid.dto.FavoritesResponse;
import com.nadoyagsa.pillaroid.entity.Favorites;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.FavoritesService;
import com.nadoyagsa.pillaroid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user/favorites")
public class FavoritesController {
    private final FavoritesService favoritesService;
    private final AuthTokenProvider authTokenProvider;
    private final UserService userService;

    @Autowired
    public FavoritesController(FavoritesService favoritesService, AuthTokenProvider authTokenProvider, UserService userService) {
        this.favoritesService = favoritesService;
        this.authTokenProvider = authTokenProvider;
        this.userService = userService;
    }

    // 사용자의 의약품 번호에 해당하는 즐겨찾기 여부 조회
    @GetMapping
    public ApiResponse<FavoritesResponse> getUserFavorites(HttpServletRequest request, @RequestParam int medicineIdx) throws IllegalStateException {
        Optional<User> user = findUserByToken(request);

        if (user.isPresent()) {
            Optional<Favorites> favorites = favoritesService.findFavoritesByUserAndMedicineIdx(user.get().getUserIdx(), medicineIdx);

            if (favorites.isPresent()) {    // 회원의 즐겨찾기 o
                FavoritesResponse response = favorites.get().toFavoritesResponse();
                return ApiResponse.success(response);
            }
            else {                          // 회원의 즐겨찾기 x
                throw NotFoundException.DATA_NOT_FOUND;
            }
        }
        else
            throw UnauthorizedException.UNAUTHORIZED_USER;
    }

    // 사용자의 즐겨찾기 목록 조회
    @GetMapping(value = "/list")
    public ApiResponse<List<FavoritesResponse>> getUserFavorites(HttpServletRequest request) throws IllegalStateException {
        Optional<User> user = findUserByToken(request);

        if (user.isPresent()) {
            List<FavoritesResponse> favoritesList = favoritesService.findFavoritesListByUserIdx(user.get().getUserIdx());

            if (favoritesList.size() > 0)   // 회원의 즐겨찾기 목록 o
                return ApiResponse.success(favoritesList);
            else                            // 회원의 즐겨찾기 목록 x
                throw NotFoundException.DATA_NOT_FOUND;
        }
        else
            throw UnauthorizedException.UNAUTHORIZED_USER;
    }

    // 즐겨찾기 추가
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<FavoritesResponse> postUserFavorites(HttpServletRequest request, @RequestBody FavoritesDTO favoritesDTO) throws IllegalStateException {
        Optional<User> user = findUserByToken(request);

        if (user.isPresent()) {
            favoritesDTO.setUserIdx(user.get().getUserIdx());

            FavoritesResponse savedFavoritesResponse = favoritesService.saveFavorites(favoritesDTO.toFavoritesEntity());
            return ApiResponse.success(savedFavoritesResponse);
            // 저장 안되면 Service에서 Internal_Error로 throw 함
        }
        else
            throw UnauthorizedException.UNAUTHORIZED_USER;
    }

    // 즐겨찾기 삭제
    @DeleteMapping(value = "/{fid}")
    public ApiResponse<String> deleteUserFavorites(HttpServletRequest request, @PathVariable("fid") Long favoritesIdx) throws IllegalStateException {
        Optional<Favorites> favorites = favoritesService.findFavoritesByIdx(favoritesIdx);

        if (favorites.isPresent()) {
            Optional<User> user = findUserByToken(request);

            // 본인의 즐겨찾기 목록인 경우
            if (user.isPresent() && (user.get().getUserIdx().equals(favorites.get().getUser().getUserIdx()))) {
                boolean isDeleted = favoritesService.deleteFavorites(favoritesIdx);

                if (isDeleted)
                    return ApiResponse.SUCCESS;
                else
                    throw InternalServerException.INTERNAL_ERROR;
            }
            else
                throw UnauthorizedException.UNAUTHORIZED_USER;
        }
        else {
            throw BadRequestException.BAD_PARAMETER;
        }
    }

    // 사용자의 즐겨찾기 목록 조회
    @GetMapping(value = "/search")
    public ApiResponse<List<FavoritesResponse>> getUserFavorites(HttpServletRequest request, @RequestParam String keyword) throws IllegalStateException {
        Optional<User> user = findUserByToken(request);

        if (user.isPresent()) {
            List<FavoritesResponse> favoritesList = favoritesService.findFavoritesListByKeyword(user.get().getUserIdx(), keyword);

            if (favoritesList.size() > 0)   // 회원의 즐겨찾기 목록 중 검색 결과 o
                return ApiResponse.success(favoritesList);
            else                            // 회원의 즐겨찾기 목록 중 검색 결과 x
                throw NotFoundException.DATA_NOT_FOUND;
        }
        else
            throw UnauthorizedException.UNAUTHORIZED_USER;
    }

    // 사용자 jwt 토큰으로부터 회원 정보 조회
    public Optional<User> findUserByToken(HttpServletRequest request) {
        try {
            // 접근 사용자 조회
            Long userIdx = authTokenProvider.getClaims(request.getHeader("authorization")).get("userId", Long.class);

            return userService.findUserById(userIdx);
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }
}
