package com.matsinger.barofishserver.utils.S3;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.review.application.ReviewQueryService;
import com.matsinger.barofishserver.domain.review.domain.Review;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepository;
import com.matsinger.barofishserver.domain.store.application.StoreQueryService;
import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class S3UploaderTest {

    @Autowired
    private S3Uploader s3;
    @Autowired
    private ProductQueryService productQueryService;
    @Autowired
    private StoreQueryService storeQueryService;
    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired private ReviewQueryService reviewQueryService;


    @DisplayName("sample test")
    @Test
    void testMethodNameHere() {
        File imageFile = new File("com/matsinger/barofishserver/utils/S3/1.png");

    }

    @DisplayName("s3에 이미지를 업로드할 수 있다.")
    @Test
    void s3UploadTest() throws Exception {
        // given
        Product findProduct = productQueryService.findById(10000);
        Store findStore = storeQueryService.findById(10000);
        User findUser = userQueryService.findById(10000);

        Review createdReview = Review.builder()
                .productId(findProduct.getId())
                .store(findStore)
                .storeId(findStore.getId())
                .userId(findUser.getId())
                .content("테스트 리뷰")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .images("[]")
                .orderProductInfoId(10000)
                .build();
        Review savedReview = reviewRepository.save(createdReview);

        List<MultipartFile> files = new ArrayList<>();
        File imageFile = new File("src/test/java/com/matsinger/barofishserver/utils/S3/2.png");

        Path path = Paths.get("src/test/java/com/matsinger/barofishserver/utils/S3/2.png");
        String name = "2.png";
        String originalFileName = "2.png";
        String contentType = "image/png";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MockMultipartFile mockMultipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        files.add(mockMultipartFile);

        // when
        String imgUrls = s3.uploadFiles(
                files,
                new ArrayList<>(Arrays.asList("review", String.valueOf(savedReview.getId())))
        ).toString();
        savedReview.setImages(imgUrls);
        Review imageSavedReview = reviewRepository.save(savedReview);

        String imageProcessedUrls = imageSavedReview.getImages().substring(1, imageSavedReview.getImages().length() - 1);
        String[] imageParsedUrls = imageProcessedUrls.split(", ");

        for (String parsedUrl : imageParsedUrls) {
            assertThat(s3.isExists(parsedUrl.replace("https://barofish-dev.s3.ap-northeast-2.amazonaws.com/", "")))
                    .isEqualTo(true);
        }

        // then
        Review findReview = reviewQueryService.findById(createdReview.getId());
        String processedUrls = findReview.getImages().substring(1, findReview.getImages().length() - 1);
        String[] parsedUrls = processedUrls.split(", ");

        for (String parsedUrl : parsedUrls) {
            s3.deleteFile(parsedUrl.replace("https://barofish-dev.s3.ap-northeast-2.amazonaws.com/", ""));
            assertThat(s3.isExists(parsedUrl.replace("https://barofish-dev.s3.ap-northeast-2.amazonaws.com/", "")))
                    .isEqualTo(false);
        }
    }
}