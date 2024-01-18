package com.matsinger.barofishserver.jwt;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum TokenAuthType {
    USER,
    PARTNER,
    ADMIN,
    ALLOW
}
