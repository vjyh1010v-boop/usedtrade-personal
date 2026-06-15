package com.pknu26.usedtrade.mapper;

import com.pknu26.usedtrade.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    void insertComment(CommentDTO commentDTO);

    List<CommentDTO> findCommentsByCommunityPostId(Long communityPostId);

    CommentDTO findCommentById(Long commentsId);

    void updateComment(CommentDTO commentDTO);

    void deleteComment(Long commentsId);
}
