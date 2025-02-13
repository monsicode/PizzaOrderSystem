package com.deliciouspizza.api.data;

public record Delivery(double distance, double duration) {

    private static final int TIME_TO_MAKE_PIZZA = 15;

    @Override
    public String toString() {
        return String.format("Distance is: %.2f km\nEstimated time for delivery is: %d min", distance,
            Math.round(duration) + TIME_TO_MAKE_PIZZA);
    }

}
