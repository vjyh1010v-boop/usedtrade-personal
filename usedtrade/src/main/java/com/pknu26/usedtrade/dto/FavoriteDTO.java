package com.pknu26.usedtrade.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FavoriteDTO {

    private Long favoriteId; 
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

}
