package com.pknu26.usedtrade.service;

import com.pknu26.usedtrade.dto.CommunityPostDTO;
import com.pknu26.usedtrade.dto.PostImageDTO;
import com.pknu26.usedtrade.mapper.CommunityPostLikeMapper;
import com.pknu26.usedtrade.mapper.CommunityPostMapper;
import com.pknu26.usedtrade.mapper.PostImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostMapper communityPostMapper;
    private final PostImageMapper postImageMapper;
    private final CommunityPostLikeMapper likeMapper;

    @Transactional
    public Long registerCommunityPost(CommunityPostDTO communityPostDTO) {
        communityPostMapper.insertCommunityPost(communityPostDTO);
        return communityPostDTO.getCommunityPostId();
    }

    public List<CommunityPostDTO> findAllCommunityPosts() {
        return communityPostMapper.findAllCommunityPosts();
    }

    public Map<String, Object> findCommunityPostsPaged(
            String category, String sort, int offset, int limit) {
        List<CommunityPostDTO> posts =
                communityPostMapper.findCommunityPostsPaged(category, sort, offset, limit);
        int totalCount = communityPostMapper.countCommunityPosts(category);
        return Map.of("posts", posts, "totalCount", totalCount);
    }

    @Transactional
    public CommunityPostDTO findCommunityPostById(Long communityPostId) {
        communityPostMapper.incrementViewCount(communityPostId);
        return communityPostMapper.findCommunityPostById(communityPostId);
    }

    public List<PostImageDTO> findImagesByCommunityPostId(Long communityPostId) {
        return postImageMapper.findImagesByCommunityPostId(communityPostId);
    }

    @Transactional
    public void deleteCommunityPost(Long communityPostId, Long userId) {
        CommunityPostDTO post = communityPostMapper.findCommunityPostById(communityPostId);
        if (post == null || !post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        postImageMapper.deleteImagesByCommunityPostId(communityPostId);
        communityPostMapper.deleteCommunityPost(communityPostId);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long communityPostId, Long userId) {
        boolean isLiked;
        if (likeMapper.existsLike(communityPostId, userId) > 0) {
            likeMapper.deleteLike(communityPostId, userId);
            isLiked = false;
        } else {
            likeMapper.insertLike(communityPostId, userId);
            isLiked = true;
        }
        int likeCount = likeMapper.countLikes(communityPostId);
        return Map.of("likeCount", likeCount, "isLiked", isLiked);
    }

    public int getLikeCount(Long communityPostId) {
        return likeMapper.countLikes(communityPostId);
    }

    public boolean isLiked(Long communityPostId, Long userId) {
        return likeMapper.existsLike(communityPostId, userId) > 0;
    }

    @Transactional
    public void updateCommunityPost(CommunityPostDTO communityPostDTO, Long userId) {
        CommunityPostDTO post = communityPostMapper.findCommunityPostById(communityPostDTO.getCommunityPostId());
        if (post == null || !post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        communityPostMapper.updateCommunityPost(communityPostDTO);
    }
}
