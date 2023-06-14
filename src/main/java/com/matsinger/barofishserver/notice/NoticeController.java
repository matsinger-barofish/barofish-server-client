package com.matsinger.barofishserver.notice;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice")
public class NoticeController {
    private final NoticeService noticeService;
    private final Common utils;
    private final S3Uploader s3;
    private final JwtService jwt;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Notice>>> selectNoticeList(@RequestParam(value = "type") NoticeType type) {
        CustomResponse<List<Notice>> res = new CustomResponse<>();
        try {
            List<Notice> notices = noticeService.selectNoticeList(type);
            res.setData(Optional.ofNullable(notices));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Notice>> selectNotice(@PathVariable("id") Integer noticeId) {
        CustomResponse<Notice> res = new CustomResponse<>();
        try {
            Notice notice = noticeService.selectNotice(noticeId);
            res.setData(Optional.ofNullable(notice));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class NoticeAddReq {
        NoticeType type;
        String title;
        String content;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<CustomResponse<Notice>> addNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "data") NoticeAddReq data) {
        CustomResponse<Notice> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.getType() == null) return res.throwError("타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String title = utils.validateString(data.getTitle(), 200L, "제목");
            String content = utils.validateString(data.getContent(), 500L, "내용");
            Notice
                    notice =
                    Notice.builder().content(content).title(title).createdAt(utils.now()).type(data.getType()).build();
            noticeService.addNotice(notice);
            res.setData(Optional.ofNullable(notice));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Notice>> updateNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("id") Integer noticeId,
                                                               @RequestPart(value = "data") NoticeAddReq data) {
        CustomResponse<Notice> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Notice notice = noticeService.selectNotice(noticeId);
            if (data.getTitle() != null) {
                String title = utils.validateString(data.getTitle(), 200L, "제목");
                notice.setTitle(title);
            }
            if (data.getContent() != null) {
                String content = utils.validateString(data.getContent(), 500L, "내용");
                notice.setContent(content);
            }
            notice.setUpdateAt(utils.now());
            noticeService.updateNotice(notice);
            res.setData(Optional.ofNullable(notice));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> updateNotice(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer noticeId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Notice notice = noticeService.selectNotice(noticeId);
            Boolean result = noticeService.deleteNotice(noticeId);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
