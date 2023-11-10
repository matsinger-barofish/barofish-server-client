package com.matsinger.barofishserver.domain.notice.api;

import com.matsinger.barofishserver.domain.notice.application.NoticeCommandService;
import com.matsinger.barofishserver.domain.notice.application.NoticeQueryService;
import com.matsinger.barofishserver.domain.notice.domain.Notice;
import com.matsinger.barofishserver.domain.notice.domain.NoticeOrderBy;
import com.matsinger.barofishserver.domain.notice.domain.NoticeType;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.notice.dto.NoticeAddReq;
import com.matsinger.barofishserver.jwt.exception.JwtExceptionMessage;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
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
@RequestMapping("/api/v1/notice")
public class NoticeController {
    private final NoticeQueryService noticeQueryService;
    private final NoticeCommandService noticeCommandService;
    private final Common utils;
    private final S3Uploader s3;
    private final JwtService jwt;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Notice>>> selectNoticeList(@RequestParam(value = "type") NoticeType type) {
        CustomResponse<List<Notice>> res = new CustomResponse<>();

        List<Notice> notices = noticeQueryService.selectNoticeList(type);
        res.setData(Optional.ofNullable(notices));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<Notice>>> selectNoticeListByAdmin(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                                @RequestParam(value = "type") NoticeType type,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                @RequestParam(value = "orderby", defaultValue = "createdAt") NoticeOrderBy orderBy,
                                                                                @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                                @RequestParam(value = "title", required = false) String title,
                                                                                @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                                @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE) {
        CustomResponse<Page<Notice>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        Specification<Notice> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null) predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
            if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
            if (tokenInfo.getType().equals(TokenAuthType.PARTNER))
                predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.getId()));
            predicates.add(builder.equal(root.get("type"), type));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
        Page<Notice> notices = noticeQueryService.selectNoticeList(spec, pageRequest);
        res.setData(Optional.ofNullable(notices));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Notice>> selectNotice(@PathVariable("id") Integer noticeId) {
        CustomResponse<Notice> res = new CustomResponse<>();

        Notice notice = noticeQueryService.selectNotice(noticeId);
        res.setData(Optional.ofNullable(notice));
        return ResponseEntity.ok(res);
    }



    @PostMapping(value = "/add")
    public ResponseEntity<CustomResponse<Notice>> addNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "data") NoticeAddReq data) throws Exception {
        CustomResponse<Notice> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        if (data.getType() == null) return res.throwError("타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
        if (data.getContent() == null) return res.throwError("내용을 입력해주세요.", "INPUT_CHECK_REQUIRED");
        String title = utils.validateString(data.getTitle(), 200L, "제목");
        String content = "";
        Notice
                notice =
                Notice.builder().content(content).title(title).createdAt(utils.now()).type(data.getType()).build();
        notice.setContent(s3.uploadEditorStringToS3(data.getContent(),
                new ArrayList<>(Arrays.asList("notice", String.valueOf(notice.getId())))));
        notice = noticeCommandService.addNotice(notice);
        res.setData(Optional.ofNullable(notice));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Notice>> updateNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("id") Integer noticeId,
                                                               @RequestPart(value = "data") NoticeAddReq data) throws Exception {
        CustomResponse<Notice> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        Notice notice = noticeQueryService.selectNotice(noticeId);
        if (data.getTitle() != null) {
            String title = utils.validateString(data.getTitle(), 200L, "제목");
            notice.setTitle(title);
        }
        if (data.getContent() != null) {
            String
                    content =
                    s3.uploadEditorStringToS3(data.getContent(),
                            new ArrayList<>(Arrays.asList("notice", String.valueOf(notice.getId()))));
            notice.setContent(content);
        }
        notice.setUpdateAt(utils.now());
        noticeCommandService.updateNotice(notice);
        res.setData(Optional.of(notice));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> updateNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer noticeId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        Notice notice = noticeQueryService.selectNotice(noticeId);
        Boolean result = noticeCommandService.deleteNotice(noticeId);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }
}
