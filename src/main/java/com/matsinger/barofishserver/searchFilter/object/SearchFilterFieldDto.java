package com.matsinger.barofishserver.searchFilter.object;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchFilterFieldDto {
    private int id;
    private int searchFilterId;
    private String field;
}
