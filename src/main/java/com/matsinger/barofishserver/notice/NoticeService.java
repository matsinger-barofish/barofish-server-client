package com.matsinger.barofishserver.notice;

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
public class NoticeService {
    private final NoticeRepository noticeRepository;

    Page<Notice> selectNoticeListByAdmin(PageRequest pageRequest) {
        return noticeRepository.findAll(pageRequest);
    }

    List<Notice> selectNoticeList(NoticeType type) {
        return noticeRepository.findAllByType(type, Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    Page<Notice> selectNoticeList(Specification<Notice> spec, PageRequest pageRequest) {
        return noticeRepository.findAll(spec, pageRequest);
    }

    Notice selectNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> {
            throw new Error("공지사항 정보를 찾을 수 없습니다.");
        });
    }

    Notice addNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    void updateNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    Boolean deleteNotice(Integer noticeId) {
        try {
            noticeRepository.deleteById(noticeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
