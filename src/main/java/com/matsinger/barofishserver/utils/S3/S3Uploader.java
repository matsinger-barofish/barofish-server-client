package com.matsinger.barofishserver.utils.S3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.imageUrl}")
    private String s3Url;

    public String getS3Url() {
        return this.s3Url;
    }

    public List<String> uploadFiles(List<MultipartFile> files, ArrayList<String> path) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = upload(file, path);
            fileUrls.add(imageUrl);
        }
        return fileUrls;
    }

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, ArrayList<String> dirName) {
        if (multipartFile.getContentType().startsWith("image") && !validateImageType(multipartFile))
            throw new BusinessException("허용되지 않는 확장자입니다.");
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new BusinessException("MultipartFile -> File 전환 실패"));
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 변환에 실패했습니다.");
        }
        return upload(uploadFile, dirName);
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public String upload(File uploadFile, ArrayList<String> dirName) {
        String fileName = String.join("/", dirName) + "/" + buildFileName(uploadFile.getName());
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile); // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead) // PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String filePath) {
        String parsedUrl = filePath.split(s3Url + "/")[1];

        try {
            if (isExists(parsedUrl)) {
                amazonS3Client.deleteObject(this.bucket, parsedUrl);
            }
        } catch (Exception e) {
            log.info("{} 파일을 삭제하는데 실패했습니다.", filePath);
        }
    }

    public boolean isExists(String filePath) {
        return amazonS3Client.doesObjectExist(bucket, filePath);
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
        } else {
        }
    }

    public Boolean validateImageType(MultipartFile file) {
        List<String> allowedImageType = List.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/svg+xml");
        return allowedImageType.contains(file.getContentType());
    }

    public List<String> parseListData(String data) {
        data = data.substring(1, data.length() - 1);
        List<String> result = new ArrayList<>();
        for (String str : List.of(data.split(","))) {
            result.add(str.trim());
        }
        return result;
    }

    public List<String> processFileUpdateInput(List<FileUpdateInput> files, ArrayList<String> path) throws Exception {
        List<String> result = new ArrayList<>();
        for (FileUpdateInput file : files) {
            if (file.getExistingFile() != null) {
                if (file.getExistingFile() == null)
                    throw new BusinessException("파일을 입력해주세요.");
                result.add(file.getExistingFile());
            } else if (file.getNewFile() != null) {
                String fileUrl = upload(file.getNewFile(), path);
                result.add(fileUrl);
            }
        }
        return result;
    }

    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String TIME_SEPARATOR = "_";

    public static String buildFileName(String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return fileName + TIME_SEPARATOR + now + fileExtension;
    }

    public String htmlString2File(String content, ArrayList<String> path) {
        try {
            File tmpFile = File.createTempFile("tmp", ".html");
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(content);
            writer.close();
            String imgUrl = upload(tmpFile, path);
            return imgUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadEditorStringToS3(String content, ArrayList<String> path) {
        Pattern imgPattern = Pattern.compile("<img src=\"data:(image/.*?);base64,(.*?)\">");
        Matcher matcher = imgPattern.matcher(content);
        List<String> imgUrls = new ArrayList<>();
        while (matcher.find()) {
            String base64String = matcher.group();
            String[] splitedString = base64String.replaceAll("<img src=\"data:(image/.*?);base64,(.*?)\"(.*?)>", // (.*?)
                    "$1&--&$2").split("&--&");
            String mimetype = splitedString[0];
            File file = convertBase64ToFile(splitedString[1], mimetype);
            String imgUrl = upload(file, path);
            content = content.replace(base64String,
                    base64String.replaceAll("(<img src=)(\"data:image/.*?;base64,)(.*?)\"(.*?)>",
                            "$1\"" + imgUrl + "\"$4"));
        }
        String res = htmlString2File(content, path);
        return res;
    }

    public File convertBase64ToFile(String base64, String mimetype) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            File tmpFile = mimetype != null && mimetype.equals("image/svg+xml") ? File.createTempFile("tmp",
                    ".svg") : File.createTempFile("tmp", ".jpg");
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(bytes);
            fos.close();
            return tmpFile;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

    }

    public File extractBase64FromImageUrl(String imageUrl) {
        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException("이미지 url이 올바르지 않습니다.");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return convertBase64ToFile(base64Image, null);
    }
}