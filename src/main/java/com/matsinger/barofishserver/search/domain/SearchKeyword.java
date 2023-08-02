package com.matsinger.barofishserver.search.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_keyword", schema = "barofish_dev", catalog = "")
public class SearchKeyword {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;
    @Basic
    @Column(name = "amount", nullable = false)
    private int amount;
    @Basic
    @Column(name = "prev_rank", nullable = true)
    private Integer prevRank;
}
