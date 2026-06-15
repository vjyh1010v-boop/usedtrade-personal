package com.pknu26.usedtrade.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long commentsId;
    private Long communityPostId;
    private Long postId;
    private Long userId;
    private String contentComments;
    private String isSecret;
    private LocalDateTime createdAtComments;
    private LocalDateTime updatedAtComments;
    private String nickname;
}
