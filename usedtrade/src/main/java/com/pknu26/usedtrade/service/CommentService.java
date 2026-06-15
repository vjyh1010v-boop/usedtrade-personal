package com.pknu26.usedtrade.service;

import com.pknu26.usedtrade.dto.CommentDTO;
import com.pknu26.usedtrade.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    @Transactional
    public void addComment(Long communityPostId, String content, Long userId) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommunityPostId(communityPostId);
        commentDTO.setContentComments(content);
        commentDTO.setUserId(userId);
        commentMapper.insertComment(commentDTO);
    }

    public List<CommentDTO> getComments(Long communityPostId) {
        return commentMapper.findCommentsByCommunityPostId(communityPostId);
    }

    @Transactional
    public void updateComment(Long commentsId, String content, Long userId) {
        CommentDTO comment = commentMapper.findCommentById(commentsId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        comment.setContentComments(content);
        commentMapper.updateComment(comment);
    }

    @Transactional
    public void deleteComment(Long commentsId, Long userId) {
        CommentDTO comment = commentMapper.findCommentById(commentsId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        commentMapper.deleteComment(commentsId);
    }
}
