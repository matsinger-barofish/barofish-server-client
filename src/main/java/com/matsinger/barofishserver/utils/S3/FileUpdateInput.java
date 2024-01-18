package com.matsinger.barofishserver.utils.S3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileUpdateInput {
    private String existingFile;
    private MultipartFile newFile;
}
