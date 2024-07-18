package com.creavispace.project.domain.file.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Jsoup.parse;

@Component
public class ImageManager {

    public List<String> usedImageFilter(List<String> images, String content){
        // 삭제할 이미지 객체 생성
        List<String> deletedImages = deleteImagesByContent(images, content);
        // 게시글 작성중에 업로드한 모든 이미지
        List<String> saveImg = new ArrayList<>(images);
        // 게시글 작성중에 업로드한 모든 이미지 - 삭제된 이미지
        saveImg.removeAll(deletedImages);
        return saveImg;
    }

    public List<String> deleteImagesByContent(List<String> images, String content){
        // 실제 저장된 Content 이미지
        List<String> contentImages = imagesByContent(content);

        // 삭제할 이미지가 저장된 객체 생성
        List<String> deletedImg = new ArrayList<>(images);

        // 게시글 작성중에 업로드한 모든 이미지 - 실제 저장된 Content 이미지
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
