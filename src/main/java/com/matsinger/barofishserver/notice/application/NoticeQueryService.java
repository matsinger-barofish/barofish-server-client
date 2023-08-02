package com.matsinger.barofishserver.notice.application;

import com.matsinger.barofishserver.notice.domain.Notice;
import com.matsinger.barofishserver.notice.domain.NoticeType;
import com.matsinger.barofishserver.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeQueryService {
    private final NoticeRepository noticeRepository;
    public Page<Notice> selectNoticeListByAdmin(PageRequest pageRequest) {
        return noticeRepository.findAll(pageRequest);
    }

    public List<Notice> selectNoticeList(NoticeType type) {
        return noticeRepository.findAllByType(type, Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    public Page<Notice> selectNoticeList(Specification<Notice> spec, PageRequest pageRequest) {
        return noticeRepository.findAll(spec, pageRequest);
    }

    public Notice selectNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> {
            throw new Error("공지사항 정보를 찾을 수 없습니다.");
        });
    }
}
