package com.matsinger.barofishserver.domain.compare.filter.domain;

import com.matsinger.barofishserver.domain.compare.filter.dto.CompareFilterDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compare_filter", schema = "barofish_dev", catalog = "")
public class CompareFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public CompareFilterDto convert2Dto() {
        return CompareFilterDto.builder().id(this.id).name(this.name).build();
    }
}
