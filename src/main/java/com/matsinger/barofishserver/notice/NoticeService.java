package com.matsinger.barofishserver.notice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;

    List<Notice> selectNoticeList(NoticeType type) {
        return noticeRepository.findAllByType(type);
    }

    Notice selectNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> {
            throw new Error("공지사항 정보를 찾을 수 없습니다.");
        });
    }

    Notice addNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    Notice updateNotice(Notice notice) {
        return noticeRepository.save(notice);
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
