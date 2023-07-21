package com.matsinger.barofishserver.search.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Integer getPrevRank() {
        return prevRank;
    }

    public void setPrevRank(Integer prevRank) {
        this.prevRank = prevRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchKeyword that = (SearchKeyword) o;
        return amount == that.amount &&
                Objects.equals(keyword, that.keyword) &&
                Objects.equals(prevRank, that.prevRank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, amount, prevRank);
    }
}
