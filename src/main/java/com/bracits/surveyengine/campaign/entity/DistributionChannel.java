package com.bracits.surveyengine.campaign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * A distribution channel for a campaign.
 * <p>
 * SRS §4.8: "System shall generate public link, private link, HTML embed
 * snippet,
 * WordPress embed, JS embed code, and email distribution."
 */
@Entity
@Table(name = "distribution_channel")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DistributionChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 30)
    private DistributionChannelType channelType;

    @Column(name = "channel_value", nullable = false, columnDefinition = "TEXT")
    private String channelValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
