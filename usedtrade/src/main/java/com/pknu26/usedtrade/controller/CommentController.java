package com.pknu26.usedtrade.controller;

import com.pknu26.usedtrade.dto.CommentDTO;
import com.pknu26.usedtrade.security.CustomUserDetails;
import com.pknu26.usedtrade.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/community/posts/{communityPostId}/comments")
    public List<CommentDTO> getComments(
            @PathVariable("communityPostId") Long communityPostId) {
        return commentService.getComments(communityPostId);
    }

    @PostMapping("/api/community/posts/{communityPostId}/comments")
    public ResponseEntity<String> addComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("communityPostId") Long communityPostId,
            @RequestParam("content") String content) {
        commentService.addComment(communityPostId, content, userDetails.getUser().getUserId());
        return ResponseEntity.ok("댓글 등록 성공");
    }

    @PutMapping("/api/comments/{commentsId}")
    public ResponseEntity<String> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("commentsId") Long commentsId,
            @RequestParam("content") String content) {
        try {
            commentService.updateComment(commentsId, content, userDetails.getUser().getUserId());
            return ResponseEntity.ok("댓글 수정 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/comments/{commentsId}")
    public ResponseEntity<String> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("commentsId") Long commentsId) {
        try {
            commentService.deleteComment(commentsId, userDetails.getUser().getUserId());
            return ResponseEntity.ok("댓글 삭제 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
