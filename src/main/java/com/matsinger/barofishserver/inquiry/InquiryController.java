package com.matsinger.barofishserver.inquiry;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inquiry")
public class InquiryController {
    private final InquiryService inquiryService;
    private final ProductService productService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<InquiryDto>>> selectInquiryListByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<InquiryDto>> res = new CustomResponse();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Inquiry>
                    inquiries =
                    tokenInfo.get().getType().equals(TokenAuthType.PARTNER) ? inquiryService.selectStoreInquiryList(
                            tokenInfo.get().getId()) : inquiryService.selectAllInquiryList();
            List<InquiryDto> inquiryDtos = inquiries.stream().map(inquiry -> {
                return inquiryService.convert2Dto(inquiry, inquiry.getProductId(), inquiry.getUserId());
            }).toList();
            res.setData(Optional.ofNullable(inquiryDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<InquiryDto>> selectInquiry(@PathVariable("id") Integer id) {
        CustomResponse<InquiryDto> res = new CustomResponse<>();
        try {
            Inquiry inquiry = inquiryService.selectInquiry(id);
            InquiryDto inquiryDto = inquiryService.convert2Dto(inquiry, inquiry.getProductId(), inquiry.getUserId());
            res.setData(Optional.ofNullable(inquiryDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CustomResponse<List<InquiryDto>>> selectInquiryListWithProduct(@PathVariable("productId") Integer productId) {
        CustomResponse<List<InquiryDto>> res = new CustomResponse();
        try {
            List<Inquiry> inquiries = inquiryService.selectInquiryListWithProductId(productId);
            res.setData(Optional.ofNullable(inquiries.stream().map(inquiry -> {

                return inquiryService.convert2Dto(inquiry, inquiry.getProductId(), inquiry.getUserId());
            }).toList()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class InquiryAddReq {
        Integer productId;
        InquiryType type;
        String content;
        Boolean isSecret;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Inquiry>> addInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                              @RequestPart(value = "data") InquiryAddReq data) {
        CustomResponse<Inquiry> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Product product = productService.selectProduct(data.getProductId());
            String content = utils.validateString(data.getContent(), 500L, "내용");
            Inquiry
                    inquiry =
                    Inquiry.builder().type(data.getType()).userId(tokenInfo.get().getId()).isSecret(data.getIsSecret()).productId(
                            product.getId()).content(content).createdAt(utils.now()).build();
            Inquiry result = inquiryService.addInquiry(inquiry);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class InquiryAnswerReq {
        String content;
    }

    @PostMapping("/answer/{id}")
    public ResponseEntity<CustomResponse<Inquiry>> answerInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @PathVariable("id") Integer inquiryId,
                                                                 @RequestPart(value = "data") InquiryAnswerReq data) {
        CustomResponse<Inquiry> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Inquiry inquiry = inquiryService.selectInquiry(inquiryId);
            if (inquiry.getProduct().getStoreId() != tokenInfo.get().getId())
                return res.throwError("다른 가계의 문의 내용입니다.", "NOT_ALLOWED");
            String content = utils.validateString(data.getContent(), 500L, "내용");
            inquiry.setAnswer(content);
            inquiry.setAnsweredAt(utils.now());
            Inquiry result = inquiryService.updateInquiry(inquiry);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
