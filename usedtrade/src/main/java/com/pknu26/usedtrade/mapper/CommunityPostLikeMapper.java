package com.pknu26.usedtrade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityPostLikeMapper {
    void insertLike(@Param("communityPostId") Long communityPostId,
                    @Param("userId") Long userId);

    void deleteLike(@Param("communityPostId") Long communityPostId,
                    @Param("userId") Long userId);

    int countLikes(@Param("communityPostId") Long communityPostId);
    
    int existsLike(@Param("communityPostId") Long communityPostId,
                   @Param("userId") Long userId);
}
