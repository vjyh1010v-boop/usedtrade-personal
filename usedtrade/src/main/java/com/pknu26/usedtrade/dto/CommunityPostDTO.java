package com.pknu26.usedtrade.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommunityPostDTO {

    private Long communityPostId;
    private Long userId;
    private String title;
    private String content;
    private String category;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String mainImageUrl;
    private String nickname;
    private int likeCount;
}
