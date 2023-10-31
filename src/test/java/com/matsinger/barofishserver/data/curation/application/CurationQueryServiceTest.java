package com.matsinger.barofishserver.data.curation.application;

import com.matsinger.barofishserver.domain.data.curation.application.CurationQueryService;
import com.matsinger.barofishserver.domain.data.curation.domain.Curation;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationState;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationType;
import com.matsinger.barofishserver.domain.data.curation.repository.CurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
@Transactional
class CurationQueryServiceTest {

    @Autowired private CurationRepository curationRepository;
    @Autowired private CurationQueryService curationQueryService;

    @BeforeEach
    public void setCurations() {
        curationRepository.save(Curation.builder()
                .image("")
                .shortName("curation1")
                .title("curation1")
                .description("curation1")
                .type(CurationType.L_SLIDER)
                .sortNo(1)
                .curationProductMaps(new ArrayList<>())
                .state(CurationState.ACTIVE)
                .build());

        curationRepository.save(Curation.builder()
                .image("")
                .shortName("curation2")
                .title("curation2")
                .description("curation2")
                .type(CurationType.L_SLIDER)
                .sortNo(2)
                .curationProductMaps(new ArrayList<>())
                .state(CurationState.INACTIVE)
                .build());
    }

    @DisplayName("메인 화면에서 큐레이션을 불러올 때 상태가 true인 것만 불러와야 한다.")
    @Test
    void getCurationOnlyStateTrue() {
        // given
        // when
        List<Curation> curations = curationQueryService.selectCurationState(CurationState.ACTIVE);
        // then
        assertThat(curations.size()).isEqualTo(1);
        assertThat(curations.get(0).getState()).isEqualTo(CurationState.ACTIVE);
        assertThat(curations.get(0).getTitle()).isEqualTo("curation1");
    }
}