package com.matsinger.barofishserver.searchFilter.domain;

import com.matsinger.barofishserver.searchFilter.dto.SearchFilterFieldDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_filter_field", schema = "barofish_dev", catalog = "")
public class SearchFilterField {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "search_filter_id")
    private int searchFilterId;
    @ManyToOne
    @JoinColumn(name = "search_filter_id", updatable = false, insertable = false)
    private SearchFilter searchFilter;
    @Basic
    @Column(name = "field")
    private String field;

    public SearchFilterFieldDto convert2Dto() {
        return SearchFilterFieldDto.builder()
                .id(this.id)
                .searchFilterId(this.searchFilterId)
                .field(this.field).build();
    }
}
