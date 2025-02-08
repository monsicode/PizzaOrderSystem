package com.deliciouspizza.api.data.distance;

import java.util.List;
import java.util.Objects;

public class DistanceResponse {

    private final List<Route> routes;

    public DistanceResponse(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DistanceResponse that = (DistanceResponse) object;
        return Objects.equals(routes, that.routes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(routes);
    }

}
