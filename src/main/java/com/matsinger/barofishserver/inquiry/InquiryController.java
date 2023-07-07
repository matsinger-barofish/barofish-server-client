package com.matsinger.barofishserver.inquiry;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inquiry")
public class InquiryController {
    private final InquiryService inquiryService;
    private final ProductService productService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<Page<InquiryDto>>> selectInquiryListByAdmin(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                     @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                     @RequestParam(value = "orderby", defaultValue = "createdAt") InquiryOrderBy orderBy,
                                                                                     @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                                     @RequestParam(value = "writer", required = false) String writer,
                                                                                     @RequestParam(value = "productName", required = false) String productName,
                                                                                     @RequestParam(value = "partnerName", required = false) String partnerName,
                                                                                     @RequestParam(value = "type", required = false) String type,
                                                                                     @RequestParam(value = "isSecret", required = false) Boolean isSecret,
                                                                                     @RequestParam(value = "isAnswered", required = false) Boolean isAnswered,
                                                                                     @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                                     @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE) {
        CustomResponse<Page<InquiryDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Inquiry> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (writer != null)
                    predicates.add(builder.like(root.get("user").get("userInfo").get("name"), "%" + writer + "%"));
                if (productName != null)
                    predicates.add(builder.like(root.get("product").get("title"), "%" + productName + "%"));
                if (partnerName != null)
                    predicates.add(builder.like(root.get("product").get("store").get("storeInfo").get("name"),
                            "%" + partnerName + "%"));
                if (type != null)
                    predicates.add(builder.and(root.get("type").in(Arrays.stream(type.split(",")).map(InquiryType::valueOf).toList())));
                if (isSecret != null) predicates.add(builder.equal(root.get("isSecret"), isSecret));
                if (isAnswered != null)
                    predicates.add(isAnswered ? builder.isNotNull(root.get("answer")) : builder.isNull(root.get("answer")));
                if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
                if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.get().getId()));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<Inquiry> inquiries = inquiryService.selectAllInquiryList(pageRequest, spec);
            Page<InquiryDto> inquiryDtos = inquiries.map(inquiry -> inquiryService.convert2Dto(inquiry, inquiry.getProductId(), inquiry.getUserId()));
            res.setData(Optional.of(inquiryDtos));
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
        CustomResponse<List<InquiryDto>> res = new CustomResponse<>();
        try {
            List<Inquiry> inquiries = inquiryService.selectInquiryListWithProductId(productId);
            res.setData(Optional.of(inquiries.stream().map(inquiry -> inquiryService.convert2Dto(inquiry, inquiry.getProductId(), inquiry.getUserId())).toList()));
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
    public ResponseEntity<CustomResponse<InquiryDto>> answerInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer inquiryId,
                                                                    @RequestPart(value = "data") InquiryAnswerReq data) {
        CustomResponse<InquiryDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Inquiry inquiry = inquiryService.selectInquiry(inquiryId);
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    inquiry.getProduct().getStoreId() != tokenInfo.get().getId())
                return res.throwError("다른 가계의 문의 내용입니다.", "NOT_ALLOWED");
            String content = utils.validateString(data.getContent(), 500L, "내용");
            inquiry.setAnswer(content);
            inquiry.setAnsweredAt(utils.now());
            Inquiry result = inquiryService.updateInquiry(inquiry);
            res.setData(Optional.ofNullable(result.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
