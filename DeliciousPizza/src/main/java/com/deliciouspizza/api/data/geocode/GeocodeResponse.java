package com.deliciouspizza.api.data.geocode;

import java.util.List;
import java.util.Objects;

public class GeocodeResponse {

    private final List<Feature> features;

    public GeocodeResponse(List<Feature> features) {
        this.features = features;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GeocodeResponse that = (GeocodeResponse) object;
        return Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(features);
    }

}
