package com.deliciouspizza.api.data.geocode;

import java.util.Objects;

public class Feature {

    private final Geometry geometry;

    public Feature(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Feature feature = (Feature) object;
        return Objects.equals(geometry, feature.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(geometry);
    }

}