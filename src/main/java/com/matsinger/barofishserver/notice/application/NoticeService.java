package com.matsinger.barofishserver.notice.application;

import com.matsinger.barofishserver.notice.domain.NoticeType;
import com.matsinger.barofishserver.notice.domain.Notice;
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
public class NoticeService {
    private final NoticeRepository noticeRepository;




}
