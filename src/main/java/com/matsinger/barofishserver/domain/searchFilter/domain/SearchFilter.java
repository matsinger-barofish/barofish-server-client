package com.matsinger.barofishserver.domain.searchFilter.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_filter", schema = "barofish_dev", catalog = "")
public class SearchFilter {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    public void setName(String name) {
        this.name = name;
    }
}
