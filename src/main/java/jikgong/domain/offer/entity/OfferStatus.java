package jikgong.domain.offer.entity;

import lombok.Getter;

@Getter
public enum OfferStatus {
    OFFER("제안됨"),
    OFFER_CANCEL("제안 취소"),
    OFFER_ACCEPTED("제안 됨");

    private final String description;

    OfferStatus(String description) {
        this.description = description;
    }
}
