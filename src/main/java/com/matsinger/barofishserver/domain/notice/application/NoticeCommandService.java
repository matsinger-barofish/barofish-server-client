package com.matsinger.barofishserver.domain.notice.application;

import com.matsinger.barofishserver.domain.notice.domain.Notice;
import com.matsinger.barofishserver.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeCommandService {
    private final NoticeRepository noticeRepository;

    public Notice addNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public void updateNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    public Boolean deleteNotice(Integer noticeId) {
        try {
            noticeRepository.deleteById(noticeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
