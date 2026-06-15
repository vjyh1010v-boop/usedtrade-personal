package com.pknu26.usedtrade.service;

import com.pknu26.usedtrade.dto.PostDTO;
import com.pknu26.usedtrade.dto.PostImageDTO;
import com.pknu26.usedtrade.mapper.FavoriteMapper;
import com.pknu26.usedtrade.mapper.PostImageMapper;
import com.pknu26.usedtrade.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final FavoriteMapper favoriteMapper;

    // 생성자 주입
    public PostService(PostMapper postMapper, PostImageMapper postImageMapper, FavoriteMapper favoriteMapper) {
        this.postMapper = postMapper;
        this.postImageMapper = postImageMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Transactional
    public Long registerPost(PostDTO postDTO) {
        postMapper.insertPost(postDTO);
        return postDTO.getPostId();
    }

    public List<PostDTO> findPostsWithPaging(Long loginUserId, String searchKeyword, String category, String sortCondition, int offset, int pageSize) {
        return postMapper.findPostsWithPaging(loginUserId, searchKeyword, category, sortCondition, offset, pageSize);
    }

    public List<PostDTO> findPostsByUserId(Long userId) {
    return postMapper.findPostsByUserId(userId);
}

    @Transactional
    public PostDTO findPostDetail(Long postId, Long loginUserId) {
        postMapper.increaseViewCount(postId);

        PostDTO post = postMapper.findPostById(postId, loginUserId);

        if (post == null) {
            return null;
        }

        List<PostImageDTO> images = postImageMapper.findImagesByPostId(postId);
        post.setImages(images);

        boolean isOwner = loginUserId != null && loginUserId.equals(post.getUserId());
        post.setOwner(isOwner);

        return post;
    }

    @Transactional
    public void updatePost(Long postId, Long loginUserId, PostDTO updateDTO) {
        PostDTO originPost = postMapper.findPostById(postId, loginUserId);

        if (originPost == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (!originPost.getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        updateDTO.setPostId(postId);

        postMapper.updatePost(updateDTO);
    }

    @Transactional
    public void updatePostStatus(Long postId, Long loginUserId, String status) {
        PostDTO originPost = postMapper.findPostById(postId, loginUserId);

        if (originPost == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (!originPost.getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("게시글 작성자만 상태를 변경할 수 있습니다.");
        }

        if (!status.equals("SELLING") && !status.equals("RESERVED") && !status.equals("SOLD")) {
            throw new IllegalArgumentException("잘못된 판매 상태입니다.");
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(postId);
        postDTO.setStatus(status);

        postMapper.updatePostStatus(postDTO);
    }

    @Transactional
    public void deletePost(Long postId, Long loginUserId) {
        PostDTO originPost = postMapper.findPostById(postId, loginUserId);

        if (originPost == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (!originPost.getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postMapper.deletePostImages(postId);
        postMapper.deletePost(postId);
    }

    @Transactional
    public Map<String, Object> toggleFavorite(Long userId, Long postId) {

        // 게시글 존재 여부 확인
        PostDTO post = postMapper.findPostById(postId, userId);

        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 현재 사용자가 이 상품을 이미 찜했는지 확인
        int exists = favoriteMapper.existsFavorite(userId, postId);

        boolean favorited;

        if (exists > 0) {
            // 이미 찜한 상태라면 찜 취소
            favoriteMapper.deleteFavorite(userId, postId);
            favorited = false;
        } else {
            // 아직 찜하지 않는 상태라면 찜 추가
            favoriteMapper.insertFavorite(userId, postId);
            favorited = true;
        }

        // 찜 추가 또는 취소 후 최신 찜 개수 조회
        Long favoriteCount = favoriteMapper.countFavoriteByPostId(postId);

        return Map.of(
            "favorited", favorited,
            "favoriteCount", favoriteCount
        );
    }

    // 현재 사용자가 특정 상품을 찜했는지 확인
    public boolean isFavorited(Long userId, Long postId) {
        return favoriteMapper.existsFavorite(userId, postId) > 0;
    }

    // 특정 상품의 전체 찜 개수 조회
    public Long countFavoriteByPostId(Long postId) {
        return favoriteMapper.countFavoriteByPostId(postId);
    }

    // 특정 사용자가 찜한 상품 목록 조회
    public List<PostDTO> findFavoritePostByUserId(Long useId) {
        return favoriteMapper.findFavoritePostsByUserId(useId);
    }



}