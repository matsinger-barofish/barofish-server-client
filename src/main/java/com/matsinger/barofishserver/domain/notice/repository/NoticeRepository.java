package com.matsinger.barofishserver.domain.notice.repository;

import com.matsinger.barofishserver.domain.notice.domain.Notice;
import com.matsinger.barofishserver.domain.notice.domain.NoticeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer>, JpaSpecificationExecutor<Notice> {
    List<Notice> findAllByType(NoticeType type, Sort sort);

    Page<Notice> findAllByType(NoticeType type, PageRequest pageRequest);
}
