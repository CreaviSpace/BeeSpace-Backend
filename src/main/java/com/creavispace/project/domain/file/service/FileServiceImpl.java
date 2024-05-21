package com.creavispace.project.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.file.dto.response.UploadFileResponseDto;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public SuccessResponseDto<UploadFileResponseDto> fileUpload(MultipartFile multipartFile) {
        String fileName = createFileName(multipartFile.getOriginalFilename()); //종복되지 않게 이름을 randomUUID()를 사용해서 생성함
        // String fileFormat = multipartFile.getContentType().substring(multipartFile.getContentType().lastIndexOf("/") + 1); //파일 확장자명 추출
        
        // MultipartFile resizeImage = resizer(fileName, fileFormat, multipartFile, 400);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (Exception e) {
            throw new CreaviCodeException(GlobalErrorCode.S3_SERVER_NOT_FOUND);
        }

        String url = amazonS3.getUrl(bucket, fileName).toString();
        log.info("/file/service : fileUpload success url = {}", url);
        return new SuccessResponseDto<UploadFileResponseDto>(true, "이미지가 저장되었습니다.", new UploadFileResponseDto(url));
    }

     public void deleteImages(List<String> deletedImg) {
        for(String fileUrl : deletedImg){
         String splitStr = ".com/";
         String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());

         amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));

        }
     }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException se) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    // @Transactional 
    // private MultipartFile resizer(String fileName, String fileFormat, MultipartFile originalImage, int width) {

    //     try {
    //         BufferedImage image = ImageIO.read(originalImage.getInputStream());// MultipartFile -> BufferedImage Convert 

    //         int originWidth = image.getWidth();
    //         int originHeight = image.getHeight();

    //         // origin 이미지가 400보다 작으면 패스
    //         if(originWidth < width)
    //             return originalImage;

    //         MarvinImage imageMarvin = new MarvinImage(image);

    //         Scale scale = new Scale();
    //         scale.load();
    //         scale.setAttribute("newWidth", width);
    //         scale.setAttribute("newHeight", width * originHeight / originWidth);//비율유지를 위해 높이 유지
    //         scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

    //         BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
    //         ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //         ImageIO.write(imageNoAlpha, fileFormat, baos);
    //         baos.flush();

    //         return new CustomMultipartFile(fileName,fileFormat,originalImage.getContentType(), baos.toByteArray());

    //     } catch (IOException e) {
    //         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 줄이는데 실패했습니다.");
    //     }
    // }
}
