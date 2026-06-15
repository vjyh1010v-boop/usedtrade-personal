package com.pknu26.usedtrade.mapper;

import com.pknu26.usedtrade.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 추가

import java.util.List;

@Mapper
public interface PostMapper {

    int insertPost(PostDTO postDTO);

    List<PostDTO> findPostsWithPaging(
        @Param("loginUserId") Long loginUserId,
        @Param("searchKeyword") String searchKeyword,
        @Param("category") String category,
        @Param("sortCondition") String sortCondition,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );
    
    PostDTO findPostById(@Param("postId") Long postId, 
                         @Param("loginUserId") Long loginUserId);

    int increaseViewCount(@Param("postId") Long postId);

    int updatePost(PostDTO postDTO);

    int updatePostStatus(PostDTO postDTO);

    int deletePostImages(@Param("postId") Long postId);

    int deletePost(@Param("postId") Long postId);

    List<PostDTO> findPostsByUserId(@Param("userId") Long userId);
    
}