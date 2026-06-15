package com.pknu26.usedtrade.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostImageDTO {

    private Long imageId;
    private Long postId;
    private Long communityPostId;
    private String originalName;
    private String storedName;
    private String imageUrl;
    private String isMain;
    private LocalDateTime createdAtImg;
}