package com.matsinger.barofishserver.compare.filter;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Getter
@Setter
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

    public CompareFilterDto convert2Dto() {
        return CompareFilterDto.builder().id(this.id).name(this.name).build();
    }
}
