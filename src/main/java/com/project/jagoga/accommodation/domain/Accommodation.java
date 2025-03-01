package com.project.jagoga.accommodation.domain;

import com.project.jagoga.utils.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id")
    private Long id;
    private String accommodationName;
    private long ownerId;
    private String phoneNumber;
    private long cityId;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;
    private String description;
    private String information;
    private int lowPrice;

    public Accommodation(Long id, String accommodationName, long ownerId, String phoneNumber, long cityId,
                         AccommodationType accommodationType, String description, String information, int lowPrice) {
        this.id = id;
        this.accommodationName = accommodationName;
        this.ownerId = ownerId;
        this.phoneNumber = phoneNumber;
        this.cityId = cityId;
        this.accommodationType = accommodationType;
        this.description = description;
        this.information = information;
        this.lowPrice = lowPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void update(String phoneNumber, long cityId, AccommodationType accommodationType,
                                String description, String information, int lowPrice) {
        this.phoneNumber = phoneNumber;
        this.cityId = cityId;
        this.accommodationType = accommodationType;
        this.description = description;
        this.information = information;
        this.lowPrice = lowPrice;
    }
}
