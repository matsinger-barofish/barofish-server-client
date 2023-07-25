package com.matsinger.barofishserver.admin.log.api;

import com.matsinger.barofishserver.admin.domain.Admin;
import com.matsinger.barofishserver.admin.domain.AdminState;
import com.matsinger.barofishserver.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.admin.log.dto.AdminLogDto;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/log")
public class AdminLogController {
    private final AdminLogQueryService adminLogQueryService;
    private final JwtService jwt;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Page<AdminLogDto>>> selectAdminLogList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                @RequestParam(value = "type") AdminLogType type,
                                                                                @RequestParam(value = "targetId") String targetId) {
        CustomResponse<Page<AdminLogDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (type == null) return res.throwError("타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (targetId == null) return res.throwError("대상 아이디를 입력해주세요.", " INPUT_CHECK_REQUIRED");
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(Sort.Direction.DESC, "createdAt"));
            Specification<AdminLog> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.equal(root.get("type"), type));
                predicates.add(builder.equal(root.get("targetId"), targetId));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            Page<AdminLogDto>
                    adminLogDtos =
                    adminLogQueryService.selectAdminLogList(spec, pageRequest).map(AdminLog::convert2Dto);
            res.setData(Optional.of(adminLogDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

}
