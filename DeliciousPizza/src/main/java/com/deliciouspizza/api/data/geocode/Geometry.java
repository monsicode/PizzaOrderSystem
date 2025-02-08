package com.deliciouspizza.api.data.geocode;

import java.util.List;
import java.util.Objects;

public class Geometry {

    private final List<Double> coordinates;

    public Geometry(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public double getLongitude() {
        return coordinates.getFirst();
    }

    public double getLatitude() {
        return coordinates.get(1);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Geometry geometry = (Geometry) object;
        return Objects.equals(coordinates, geometry.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coordinates);
    }

}