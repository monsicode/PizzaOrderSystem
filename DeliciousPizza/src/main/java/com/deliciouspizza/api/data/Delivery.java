package com.deliciouspizza.api.data;

public record Delivery(double distance, double duration) {

    @Override
    public String toString() {
        return String.format("Distance is: %.2f km\nTime is: %.2f min", distance, duration);
    }

}
