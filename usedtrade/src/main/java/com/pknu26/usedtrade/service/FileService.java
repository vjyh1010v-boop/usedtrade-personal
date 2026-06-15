package com.pknu26.usedtrade.service;

import com.pknu26.usedtrade.dto.PostImageDTO;
import com.pknu26.usedtrade.mapper.PostImageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final PostImageMapper postImageMapper;

    @Value("${file.upload-dir}")
    private String uploadPath;

    public FileService(PostImageMapper postImageMapper) {
        this.postImageMapper = postImageMapper;
    }

    public void savePostImages(Long postId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);

            if (image.isEmpty()) {
                continue;
            }

            String originalName = image.getOriginalFilename();
            String extension = getExtension(originalName);
            String storedName = UUID.randomUUID() + extension;
            String imageUrl = "/uploads/" + storedName;

            try {
                image.transferTo(new File(uploadPath + storedName));
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }

            PostImageDTO postImageDTO = new PostImageDTO();
            postImageDTO.setPostId(postId);
            postImageDTO.setOriginalName(originalName);
            postImageDTO.setStoredName(storedName);
            postImageDTO.setImageUrl(imageUrl);
            postImageDTO.setIsMain(i == 0 ? "Y" : "N");

            postImageMapper.insertPostImage(postImageDTO);
        }
    }

    public void saveCommunityPostImages(Long communityPostId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (image.isEmpty()) {
                continue;
            }

            String originalName = image.getOriginalFilename();
            String extension = getExtension(originalName);
            String storedName = UUID.randomUUID() + extension;
            String imageUrl = "/uploads/" + storedName;

            try {
                image.transferTo(new File(uploadPath + storedName));
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }

            PostImageDTO postImageDTO = new PostImageDTO();
            postImageDTO.setCommunityPostId(communityPostId);
            postImageDTO.setOriginalName(originalName);
            postImageDTO.setStoredName(storedName);
            postImageDTO.setImageUrl(imageUrl);
            postImageDTO.setIsMain(i == 0 ? "Y" : "N");

            postImageMapper.insertCommunityPostImage(postImageDTO);
        }
    }

    public void replaceCommunityPostImages(Long communityPostId,
                                           List<Long> keepImageIds,
                                           List<MultipartFile> newImages) {
        List<PostImageDTO> allImages = postImageMapper.findImagesByCommunityPostId(communityPostId);
        postImageMapper.deleteImagesByCommunityPostId(communityPostId);

        Set<Long> keepSet = new HashSet<>(keepImageIds != null ? keepImageIds : List.of());
        for (PostImageDTO img : allImages) {
            if (!keepSet.contains(img.getImageId())) {
                new File(uploadPath + img.getStoredName()).delete();
            }
        }

        Map<Long, PostImageDTO> imgMap = allImages.stream()
                .collect(Collectors.toMap(PostImageDTO::getImageId, i -> i));
        List<Long> ordered = keepImageIds != null ? keepImageIds : List.of();
        int idx = 0;
        for (Long id : ordered) {
            PostImageDTO src = imgMap.get(id);
            if (src == null) continue;
            PostImageDTO dto = new PostImageDTO();
            dto.setCommunityPostId(communityPostId);
            dto.setOriginalName(src.getOriginalName());
            dto.setStoredName(src.getStoredName());
            dto.setImageUrl(src.getImageUrl());
            dto.setIsMain(idx++ == 0 ? "Y" : "N");
            postImageMapper.insertCommunityPostImage(dto);
        }

        if (newImages == null) return;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        for (MultipartFile file : newImages) {
            if (file.isEmpty()) continue;
            String ext = getExtension(file.getOriginalFilename());
            String stored = UUID.randomUUID() + ext;
            try {
                file.transferTo(new File(uploadPath + stored));
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
            PostImageDTO dto = new PostImageDTO();
            dto.setCommunityPostId(communityPostId);
            dto.setOriginalName(file.getOriginalFilename());
            dto.setStoredName(stored);
            dto.setImageUrl("/uploads/" + stored);
            dto.setIsMain(idx++ == 0 ? "Y" : "N");
            postImageMapper.insertCommunityPostImage(dto);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}