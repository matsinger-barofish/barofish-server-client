package com.matsinger.barofishserver.domain.notice.application;

import com.matsinger.barofishserver.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;




}
