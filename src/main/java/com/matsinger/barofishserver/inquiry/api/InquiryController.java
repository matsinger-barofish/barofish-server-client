package com.matsinger.barofishserver.inquiry.api;

import com.matsinger.barofishserver.inquiry.application.InquiryCommandService;
import com.matsinger.barofishserver.inquiry.application.InquiryQueryService;
import com.matsinger.barofishserver.inquiry.dto.*;
import com.matsinger.barofishserver.inquiry.domain.InquiryOrderBy;
import com.matsinger.barofishserver.inquiry.domain.InquiryType;
import com.matsinger.barofishserver.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
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
    private final InquiryQueryService inquiryQueryService;
    private final InquiryCommandService inquiryCommandService;
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
            Page<Inquiry> inquiries = inquiryQueryService.selectAllInquiryList(pageRequest, spec);
            Page<InquiryDto> inquiryDtos = inquiries.map(inquiry -> inquiryCommandService.convert2Dto(inquiry, null));
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
            Inquiry inquiry = inquiryQueryService.selectInquiry(id);
            InquiryDto inquiryDto = inquiryCommandService.convert2Dto(inquiry, null);
            res.setData(Optional.ofNullable(inquiryDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CustomResponse<List<InquiryDto>>> selectInquiryListWithProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                         @PathVariable("productId") Integer productId) {
        CustomResponse<List<InquiryDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Integer userId = null;
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER))
                userId = tokenInfo.get().getId();
            List<Inquiry> inquiries = inquiryQueryService.selectInquiryListWithProductId(productId);
            Integer finalUserId = userId;
            res.setData(Optional.of(inquiries.stream().map(inquiry -> inquiryCommandService.convert2Dto(inquiry,
                    finalUserId)).toList()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Inquiry>> addInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                              @RequestPart(value = "data") InquiryAddReq data) {
        CustomResponse<Inquiry> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Product product = productService.findById(data.getProductId());
            String content = utils.validateString(data.getContent(), 500L, "내용");
            Inquiry
                    inquiry =
                    Inquiry.builder().type(data.getType()).userId(tokenInfo.get().getId()).isSecret(data.getIsSecret()).productId(
                            product.getId()).content(content).createdAt(utils.now()).build();
            Inquiry result = inquiryCommandService.addInquiry(inquiry);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
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
            Inquiry inquiry = inquiryQueryService.selectInquiry(inquiryId);
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    inquiry.getProduct().getStoreId() != tokenInfo.get().getId())
                return res.throwError("다른 가계의 문의 내용입니다.", "NOT_ALLOWED");
            String content = utils.validateString(data.getContent(), 500L, "내용");
            inquiry.setAnswer(content);
            inquiry.setAnsweredAt(utils.now());
            Inquiry result = inquiryCommandService.updateInquiry(inquiry);
            res.setData(Optional.ofNullable(result.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<InquiryDto>> updateInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer inquiryId,
                                                                    @RequestPart(value = "data") InquiryUpdateReq data) {
        CustomResponse<InquiryDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Inquiry inquiry = inquiryQueryService.selectInquiry(inquiryId);
            if (inquiry.getUserId() != userId) return res.throwError("타인의 문의 내용입니다.", "INPUT_CHECK_REQUIRED");
            if (inquiry.getAnsweredAt() != null) return res.throwError("답변 완료된 문의입니다.", "NOT_ALLOWED");
            if (data.getType() != null) {
                inquiry.setType(data.getType());
            }
            if (data.getContent() != null) {
                String content = data.getContent().trim();
                if (content.length() == 0) return res.throwError("내용을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                inquiry.setContent(content);
            }
            if (data.getIsSecret() != null) {
                inquiry.setIsSecret(data.getIsSecret());
            }
            inquiry = inquiryCommandService.updateInquiry(inquiry);
            res.setData(Optional.ofNullable(inquiryCommandService.convert2Dto(inquiry, userId)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteInquiryByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("id") Integer inquiryId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Inquiry inquiry = inquiryQueryService.selectInquiry(inquiryId);
            if (inquiry.getUserId() != userId) return res.throwError("타인의 문의 내용입니다.", "INPUT_CHECK_REQUIRED");
            inquiryCommandService.deleteInquiry(inquiryId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/list")
    public ResponseEntity<CustomResponse<Boolean>> deleteInquiryByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @RequestPart(value = "data") InquiryDeleteReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.getInquiryIds() == null || data.getInquiryIds().size() == 0)
                return res.throwError("삭제할 문의를 선택해주세요.", "INPUT_CHECK_REQUIRED");
            inquiryCommandService.deleteInquiryWithIds(data.getInquiryIds());
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
