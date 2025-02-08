package com.deliciouspizza.api;

import com.google.gson.Gson;
import com.deliciouspizza.api.data.Coordinates;
import com.deliciouspizza.api.data.Delivery;
import com.deliciouspizza.api.data.geocode.GeocodeResponse;
import com.deliciouspizza.api.data.distance.DistanceResponse;
import com.deliciouspizza.api.exceptions.ApiException;
import com.deliciouspizza.api.exceptions.BadRequestException;
import com.deliciouspizza.api.exceptions.UnauthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import io.github.cdimascio.dotenv.Dotenv;

public class DistanceClient {

    private static final Dotenv DOTENV = Dotenv.load();
    private static final String API_KEY = DOTENV.get("API_KEY");
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.openrouteservice.org";
    private static final String API_ENDPOINT_PATH_DISTANCE = "/v2/directions/cycling-regular";

    private static final String API_ENDPOINT_PATH_COORDINATES = "/geocode/search";
    private static final String API_KEY_PARAM_QUERY = "api_key=%s";
    private static final String ADDRESS_PARAM_QUERY = "&text=%s";

    private static final String API_KEY_HEADER = "Authorization";

    private static final Coordinates COORDINATES_RESTAURANT = new Coordinates(42.643077, 23.340818);

    private final HttpClient client;

    public DistanceClient() {
        this.client = HttpClient.newHttpClient();
    }

    public Delivery getDistanceAndDuration(String address)
        throws ApiException {

        Coordinates coordinatesClient = getCoordinatesFromAddress(address);

        String coordinatesJson = String.format("{\"coordinates\": [[%f, %f], [%f, %f]]}",
            coordinatesClient.longitude(), coordinatesClient.latitude(), COORDINATES_RESTAURANT.longitude(),
            COORDINATES_RESTAURANT.latitude());

        URI uri = URI.create(API_ENDPOINT_SCHEME + "://" + API_ENDPOINT_HOST + API_ENDPOINT_PATH_DISTANCE);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .header(API_KEY_HEADER, API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(coordinatesJson))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleApiResponse(response);

            Gson gson = new Gson();
            DistanceResponse distanceResponse = gson.fromJson(response.body(), DistanceResponse.class);
            double distance = distanceResponse.getRoutes().getFirst().getSummary().getDistance();
            double duration = distanceResponse.getRoutes().getFirst().getSummary().getDuration();

            return new Delivery(distance / 1000, duration / 60);

        } catch (UnauthorizedException | BadRequestException e) {
            throw new ApiException(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Error fetching data from API: " + e.getMessage());
        }
    }

    public Coordinates getCoordinatesFromAddress(String address) throws ApiException {

        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String geocodingUrl = API_ENDPOINT_SCHEME + "://" + API_ENDPOINT_HOST + API_ENDPOINT_PATH_COORDINATES +
                "?" + String.format(API_KEY_PARAM_QUERY, API_KEY) + "&" +
                String.format(ADDRESS_PARAM_QUERY, encodedAddress);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geocodingUrl))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleApiResponse(response);

            Gson gson = new Gson();
            GeocodeResponse geocodeResponse = gson.fromJson(response.body(), GeocodeResponse.class);

            double lat = geocodeResponse.getFeatures().getFirst().getGeometry().getLatitude();
            double lon = geocodeResponse.getFeatures().getFirst().getGeometry().getLongitude();

            return new Coordinates(lat, lon);

        } catch (IOException | InterruptedException | BadRequestException | UnauthorizedException e) {
            throw new ApiException("Error sending geocoding request: " + e.getMessage());
        }
    }

    private void handleApiResponse(HttpResponse<String> response)
        throws BadRequestException, UnauthorizedException, ApiException {
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            switch (response.statusCode()) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    throw new BadRequestException("Bad request: " + response.body());
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException("Unauthorized access: " + response.body());
                default:
                    throw new ApiException("API Error: " + response.statusCode() + " - " + response.body());
            }
        }
    }


}
