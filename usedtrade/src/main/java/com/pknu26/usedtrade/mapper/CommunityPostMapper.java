package com.pknu26.usedtrade.mapper;

import com.pknu26.usedtrade.dto.CommunityPostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityPostMapper {

    int insertCommunityPost(CommunityPostDTO communityPostDTO);

    List<CommunityPostDTO> findAllCommunityPosts();

    List<CommunityPostDTO> findCommunityPostsPaged(
            @Param("category") String category,
            @Param("sort") String sort,
            @Param("offset") int offset,
            @Param("limit") int limit);

    int countCommunityPosts(@Param("category") String category);

    CommunityPostDTO findCommunityPostById(Long communityPostId);

    void incrementViewCount(Long communityPostId);

    void deleteCommunityPost(Long communityPostId);

    void updateCommunityPost(CommunityPostDTO communityPostDTO);
}
