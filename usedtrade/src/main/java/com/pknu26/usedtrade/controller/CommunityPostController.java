package com.pknu26.usedtrade.controller;

import com.pknu26.usedtrade.dto.CommunityPostDTO;
import com.pknu26.usedtrade.dto.PostImageDTO;
import com.pknu26.usedtrade.security.CustomUserDetails;
import com.pknu26.usedtrade.service.CommunityPostService;
import com.pknu26.usedtrade.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;
    private final FileService fileService;

    @GetMapping("/community")
    public String communityPage() {
        return "community/community";
    }

    @GetMapping("/community/{communityPostId}")
    public String communityDetailPage(
            @PathVariable("communityPostId") Long communityPostId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        CommunityPostDTO post = communityPostService.findCommunityPostById(communityPostId);
        List<PostImageDTO> images = communityPostService.findImagesByCommunityPostId(communityPostId);
        int likeCount = communityPostService.getLikeCount(communityPostId);
        boolean isLiked = userDetails != null &&
                communityPostService.isLiked(communityPostId, userDetails.getUser().getUserId());
        model.addAttribute("post", post);
        model.addAttribute("images", images);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("isLiked", isLiked);
        return "community/community-detail";
    }

    @PostMapping("/api/community/posts/{communityPostId}/likes")
    @ResponseBody
    public ResponseEntity<?> toggleLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("communityPostId") Long communityPostId) {
        Map<String, Object> result = communityPostService.toggleLike(
                communityPostId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/community/posts")
    @ResponseBody
    public ResponseEntity<?> findAllCommunityPosts(
            @RequestParam(value = "category", defaultValue = "ALL") String category,
            @RequestParam(value = "sort", defaultValue = "LATEST") String sort,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "12") int limit) {
        Map<String, Object> result =
                communityPostService.findCommunityPostsPaged(category, sort, offset, limit);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/community/posts")
    @ResponseBody
    public String registerCommunityPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            HttpServletRequest request) {

        CommunityPostDTO communityPostDTO = new CommunityPostDTO();
        communityPostDTO.setUserId(userDetails.getUser().getUserId());
        communityPostDTO.setTitle(title);
        communityPostDTO.setContent(content);
        communityPostDTO.setCategory(category);

        Long communityPostId = communityPostService.registerCommunityPost(communityPostDTO);

        List<MultipartFile> images = Collections.emptyList();
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            images = multipartRequest.getFiles("images");
        }
        fileService.saveCommunityPostImages(communityPostId, images);

        return "커뮤니티 게시글 등록 성공";
    }

    @PutMapping("/api/community/posts/{communityPostId}")
    @ResponseBody
    public ResponseEntity<String> updateCommunityPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("communityPostId") Long communityPostId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam(value = "keepImageIds", required = false) List<Long> keepImageIds,
            HttpServletRequest request) {
        CommunityPostDTO communityPostDTO = new CommunityPostDTO();
        communityPostDTO.setCommunityPostId(communityPostId);
        communityPostDTO.setTitle(title);
        communityPostDTO.setContent(content);
        communityPostDTO.setCategory(category);
        try {
            communityPostService.updateCommunityPost(communityPostDTO, userDetails.getUser().getUserId());

            List<MultipartFile> newImages = Collections.emptyList();
            if (request instanceof MultipartHttpServletRequest mr) {
                newImages = mr.getFiles("newImages");
            }
            fileService.replaceCommunityPostImages(communityPostId, keepImageIds, newImages);

            return ResponseEntity.ok("커뮤니티 게시글 수정 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/community/posts/{communityPostId}")
    @ResponseBody
    public ResponseEntity<String> deleteCommunityPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("communityPostId") Long communityPostId) {
        try {
            communityPostService.deleteCommunityPost(communityPostId, userDetails.getUser().getUserId());
            return ResponseEntity.ok("커뮤니티 게시글 삭제 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
