package com.deliciouspizza.api.data.distance;

import java.util.Objects;

public class Route {

    private final Summary summary;

    public Route(Summary summary) {
        this.summary = summary;
    }

    public Summary getSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Route route = (Route) object;
        return Objects.equals(summary, route.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(summary);
    }

}