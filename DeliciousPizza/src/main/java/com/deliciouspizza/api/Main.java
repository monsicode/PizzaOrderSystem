package com.deliciouspizza.api;

import com.deliciouspizza.api.data.Delivery;
import com.deliciouspizza.api.exceptions.ApiException;

public class Main {

    public static void main(String[] args) throws ApiException {
        DistanceClient client = new DistanceClient();
      //  Delivery delivery = client.getDistanceAndDuration(42.645649, 23.337200, 42.674439, 23.330766);
        Delivery delivery = client.getDistanceAndDuration("НДК София");

        System.out.println(delivery.toString());

//        double distance = delivery.distance();
//        double time = delivery.duration();
//
//        System.out.printf("Distance is: %2f km\n", distance);
//        System.out.printf("Time is: %2f min", time);

//        Coordinates coordinates = client.getCoordinatesFromAddress("БЛ. 60 СТУДЕНТСКИ ГРАД");
//
////        double lat = coordinates[0];
////        double lon = coordinates[1];
//
//        double lat = coordinates.latitude();
//        double lon = coordinates.longitude();
//
//        System.out.printf("Distance is: %2f km\n", lat);
//        System.out.printf("Time is: %2f min", lon);

    }

}
