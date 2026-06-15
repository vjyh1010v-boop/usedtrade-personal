package com.pknu26.usedtrade.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pknu26.usedtrade.dto.PostDTO;

@Mapper
public interface FavoriteMapper {

    // 사용자가 특정 상품을 찜할 때 사용하는 메서드
    int insertFavorite(@Param("userId") Long userId,
                       @Param("postId") Long postId);

    // 사용자가 특정 상품의 찜을 취소할 때 사용하는 메서드
    int deleteFavorite(@Param("userId") Long userId,
                       @Param("postId") Long postId);

    // 사용자가 특정 상품을 이미 찜했는지 확인하는 메서드
    int existsFavorite(@Param("userId") Long userId,
                       @Param("postId") Long postId);

    // 특정 상품이 총 몇 번 찜되었는지 조회하는 메서드
    Long countFavoriteByPostId(@Param("postId") Long postId);

    // 특정 사용자가 찜한 상품 목록을 조회하는 메서드
    List<PostDTO> findFavoritePostsByUserId(@Param("userId") Long userId);

}
