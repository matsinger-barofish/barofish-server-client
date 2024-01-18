package com.matsinger.barofishserver.utils.fcm;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fcm_token", schema = "barofish_dev", catalog = "")
public class FcmToken {

    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
}
