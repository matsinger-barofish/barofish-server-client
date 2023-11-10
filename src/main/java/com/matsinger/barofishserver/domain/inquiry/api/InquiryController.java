package com.matsinger.barofishserver.domain.inquiry.api;

import com.matsinger.barofishserver.domain.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.domain.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.domain.inquiry.application.InquiryCommandService;
import com.matsinger.barofishserver.domain.inquiry.application.InquiryQueryService;
import com.matsinger.barofishserver.domain.inquiry.domain.InquiryOrderBy;
import com.matsinger.barofishserver.domain.inquiry.domain.InquiryType;
import com.matsinger.barofishserver.domain.inquiry.dto.*;
import com.matsinger.barofishserver.domain.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.jwt.exception.JwtExceptionMessage;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.criteria.Predicate;
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
    private final AdminLogCommandService adminLogCommandService;
    private final AdminLogQueryService adminLogQueryService;
    private final NotificationCommandService notificationCommandService;
    private final StoreService storeService;
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

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

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
            if (tokenInfo.getType().equals(TokenAuthType.PARTNER))
                predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.getId()));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
        Page<Inquiry> inquiries = inquiryQueryService.selectAllInquiryList(pageRequest, spec);
        Page<InquiryDto> inquiryDtos = inquiries.map(inquiry -> inquiryCommandService.convert2Dto(inquiry, null));
        res.setData(Optional.of(inquiryDtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<InquiryDto>> selectInquiry(@PathVariable("id") Integer id) {
        CustomResponse<InquiryDto> res = new CustomResponse<>();

        Inquiry inquiry = inquiryQueryService.selectInquiry(id);
        InquiryDto inquiryDto = inquiryCommandService.convert2Dto(inquiry, null);
        res.setData(Optional.ofNullable(inquiryDto));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/user")
    public ResponseEntity<CustomResponse<List<InquiryDto>>> selectInquiryListWithUserId(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<InquiryDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        List<Inquiry> inquiries = inquiryQueryService.selectInquiryListWithUserId(userId);
        res.setData(Optional.of(inquiries.stream().map(inquiry -> inquiryCommandService.convert2Dto(inquiry,
                userId)).toList()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CustomResponse<List<InquiryDto>>> selectInquiryListWithProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                         @PathVariable("productId") Integer productId) {
        CustomResponse<List<InquiryDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        List<Inquiry> inquiries = inquiryQueryService.selectInquiryListWithProductId(productId);
        Integer finalUserId = userId;
        res.setData(Optional.of(inquiries.stream().map(inquiry -> inquiryCommandService.convert2Dto(inquiry,
                finalUserId)).toList()));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Inquiry>> addInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                              @RequestPart(value = "data") InquiryAddReq data) throws Exception {
        CustomResponse<Inquiry> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        Product product = productService.findById(data.getProductId());
        String content = utils.validateString(data.getContent(), 500L, "내용");
        Inquiry
                inquiry =
                Inquiry.builder().type(data.getType()).userId(tokenInfo.getId()).isSecret(data.getIsSecret()).productId(
                        product.getId()).content(content).createdAt(utils.now()).build();
        Inquiry result = inquiryCommandService.addInquiry(inquiry);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }


    @PostMapping("/answer/{id}")
    public ResponseEntity<CustomResponse<InquiryDto>> answerInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer inquiryId,
                                                                    @RequestPart(value = "data") InquiryAnswerReq data) throws Exception {
        CustomResponse<InquiryDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

        Integer adminId = null;
        if (tokenInfo.getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.getId();
        Inquiry inquiry = inquiryQueryService.selectInquiry(inquiryId);
        if (tokenInfo.getType().equals(TokenAuthType.PARTNER) &&
                inquiry.getProduct().getStoreId() != tokenInfo.getId())
            return res.throwError("다른 가계의 문의 내용입니다.", "NOT_ALLOWED");
        String content = utils.validateString(data.getContent(), 500L, "내용");
        inquiry.setAnswer(content);
        inquiry.setAnsweredAt(utils.now());
        Store store = storeService.selectStore(inquiry.getProduct().getStoreId());

        Inquiry result = inquiryCommandService.updateInquiry(inquiry);
        res.setData(Optional.ofNullable(result.convert2Dto()));
        notificationCommandService.sendFcmToUser(inquiry.getUserId(),
                NotificationMessageType.INQUIRY_ANSWER,
                NotificationMessage.builder().storeName(store.getName()).build());
        if (adminId != null) {
            AdminLog
                    adminLog =
                    AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.INQUIRY).targetId(
                            inquiryId.toString()).content("답변을 등록하였습니다.").createdAt(utils.now()).build();
            adminLogCommandService.saveAdminLog(adminLog);
        }
        return ResponseEntity.ok(res);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<InquiryDto>> updateInquiry(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer inquiryId,
                                                                    @RequestPart(value = "data") InquiryUpdateReq data) {
        CustomResponse<InquiryDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

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
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteInquiryByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("id") Integer inquiryId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        Inquiry inquiry = inquiryQueryService.selectInquiry(inquiryId);
        if (inquiry.getUserId() != userId) return res.throwError("타인의 문의 내용입니다.", "INPUT_CHECK_REQUIRED");
        inquiryCommandService.deleteInquiry(inquiryId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/list")
    public ResponseEntity<CustomResponse<Boolean>> deleteInquiryByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @RequestPart(value = "data") InquiryDeleteReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        if (data.getInquiryIds() == null || data.getInquiryIds().size() == 0)
            return res.throwError("삭제할 문의를 선택해주세요.", "INPUT_CHECK_REQUIRED");
        inquiryCommandService.deleteInquiryWithIds(data.getInquiryIds());
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
