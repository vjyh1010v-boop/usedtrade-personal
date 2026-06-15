package com.pknu26.usedtrade.controller;

import com.pknu26.usedtrade.dto.PostDTO;
import com.pknu26.usedtrade.security.CustomUserDetails;
import com.pknu26.usedtrade.service.FileService;
import com.pknu26.usedtrade.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    public PostController(PostService postService, FileService fileService) {
        this.postService = postService;
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<?> findAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortCondition", defaultValue = "latest") String sortCondition,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        int pageSize = 12;
        int offset = (page - 1) * pageSize;

        List<PostDTO> postList = postService.findPostsWithPaging(loginUserId, searchKeyword, category, sortCondition,
                offset, pageSize);

        return ResponseEntity.ok(postList);
    }

    @GetMapping("/my")
    public ResponseEntity<?> findMyPosts(Authentication authentication) {
        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        List<PostDTO> myPosts = postService.findPostsByUserId(loginUserId);

        return ResponseEntity.ok(myPosts);
    }

    // 내가 찜한 상품 목록 조회
    @GetMapping("/favorites")
    public ResponseEntity<?> findMyFavoritePosts(Authentication authentication) {
        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        List<PostDTO> favoritePosts = postService.findFavoritePostByUserId(loginUserId);

        return ResponseEntity.ok(favoritePosts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> findPostDetail(
            @PathVariable("postId") Long postId,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        PostDTO post = postService.findPostDetail(postId, loginUserId);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<?> registerPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("price") Long price,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setUserId(loginUserId);
        postDTO.setBoardId(1L);
        postDTO.setTitle(title);
        postDTO.setContent(content);
        postDTO.setPrice(price);
        postDTO.setCategory(category);
        postDTO.setLocation(location);
        postDTO.setStatus("SELLING");

        Long postId = postService.registerPost(postDTO);

        fileService.savePostImages(postId, images);

        return ResponseEntity.ok("게시글 등록 성공");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody PostDTO updateDTO,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            postService.updatePost(postId, loginUserId, updateDTO);
            return ResponseEntity.ok("게시글 수정 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{postId}/status")
    public ResponseEntity<?> updatePostStatus(
            @PathVariable("postId") Long postId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String status = request.get("status");

        try {
            postService.updatePostStatus(postId, loginUserId, status);
            return ResponseEntity.ok("판매 상태 변경 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable("postId") Long postId,
            Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            postService.deletePost(postId, loginUserId);
            return ResponseEntity.ok("게시글 삭제 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 찜 추가/취소 토글
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable("postId") Long postId, Authentication authentication) {

        Long loginUserId = getLoginUserId(authentication);

        if (loginUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            Map<String, Object> result = postService.toggleFavorite(loginUserId, postId);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private Long getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            return null;
        }

        return userDetails.getUser().getUserId();
    }

}