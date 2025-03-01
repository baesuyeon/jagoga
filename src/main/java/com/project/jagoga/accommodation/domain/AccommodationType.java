package com.project.jagoga.accommodation.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.project.jagoga.exception.accommodation.UnknownAccommodationTypeException;
import java.util.stream.Stream;

public enum AccommodationType {
    PENSION, HOTEL;

    @JsonCreator
    public static AccommodationType forValue(String accommodationValue) {
        return Stream.of(AccommodationType.values())
            .filter(value -> value.name().equals(accommodationValue.toUpperCase()))
            .findFirst()
            .orElseThrow(UnknownAccommodationTypeException::new);
    }
}
