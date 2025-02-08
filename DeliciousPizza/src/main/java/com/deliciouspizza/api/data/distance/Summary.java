package com.deliciouspizza.api.data.distance;

import java.util.Objects;

public class Summary {

    private final double distance;
    private final double duration;

    public Summary(double distance, double duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Summary summary = (Summary) object;
        return Double.compare(distance, summary.distance) == 0 &&
            Double.compare(duration, summary.duration) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, duration);
    }

}
