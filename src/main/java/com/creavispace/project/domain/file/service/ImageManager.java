package com.creavispace.project.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Jsoup.parse;

@Component
@RequiredArgsConstructor
public class ImageManager {

    private final FileService fileService;
    public List<String> deleteUnusedImagesAndFilterUsedImages(List<String> images, String content){
        // Content 삭제된 이미지
        List<String> deletedImages = deleteImagesByContent(images, content);
        // S3저장소에서 삭제
        fileService.deleteImages(deletedImages);
        // 저장할 이미지 생성
        List<String> saveImg = new ArrayList<>(images);
        saveImg.removeAll(deletedImages);
        return saveImg;
    }

    private List<String> deleteImagesByContent(List<String> images, String content){
        List<String> contentImages = imagesByContent(content);
        List<String> deletedImg = new ArrayList<>(images);
        deletedImg.removeAll(contentImages);
        return deletedImg;
    }

    private List<String> imagesByContent(String content) {
        List<String> contentImages = new ArrayList<>();

        Document doc = parse(content);
        Elements imageElements = doc.select("img");

        for(Element imageElement : imageElements){
            String imageUrl = imageElement.attr("src");
            contentImages.add(imageUrl);
        }
        return contentImages;
    }
}
