package com.deliciouspizza.api;

import com.deliciouspizza.api.cache.CacheDistance;
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
    //  private static final String API_ENDPOINT_PATH_DISTANCE = "/v2/directions/driving-car";
    private static final String API_ENDPOINT_PATH_COORDINATES = "/geocode/search";
    private static final String API_KEY_PARAM_QUERY = "api_key=%s";
    private static final String ADDRESS_PARAM_QUERY = "&text=%s";
    private static final String API_KEY_HEADER = "Authorization";

    private static final double METERS_TO_KILOMETERS = 1000.0;
    private static final double SECONDS_TO_MINUTES = 60.0;

    private static final Coordinates COORDINATES_RESTAURANT = new Coordinates(42.643077, 23.340818);

    private final CacheDistance<String, Delivery> cache;

    private final HttpClient client;

    public DistanceClient() {
        this.client = HttpClient.newHttpClient();
        this.cache = new CacheDistance<>("new_cache.json", String.class, Delivery.class);
    }

    public Delivery getDistanceAndDuration(String address) throws ApiException {
        String trimmedAddress = address.trim();
        if (cache.containsKey(trimmedAddress)) {
            return cache.get(trimmedAddress);
        }

        Coordinates coordinatesClient = getCoordinatesFromAddress(trimmedAddress);
        String coordinatesJson = buildCoordinatesJson(coordinatesClient);
        HttpRequest request = buildHttpRequest(coordinatesJson);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            handleApiResponse(response);
            Delivery result = parseResponse(response.body());
            cache.put(trimmedAddress, result);
            return result;
        } catch (UnauthorizedException | BadRequestException e) {
            throw new ApiException(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Error fetching data from API: " + e.getMessage());
        }
    }

    private String buildCoordinatesJson(Coordinates coordinatesClient) {
        return String.format("{\"coordinates\": [[%f, %f], [%f, %f]]}",
            coordinatesClient.longitude(), coordinatesClient.latitude(),
            COORDINATES_RESTAURANT.longitude(), COORDINATES_RESTAURANT.latitude());
    }

    private HttpRequest buildHttpRequest(String coordinatesJson) {
        URI uri = URI.create(API_ENDPOINT_SCHEME + "://" + API_ENDPOINT_HOST + API_ENDPOINT_PATH_DISTANCE);
        return HttpRequest.newBuilder()
            .uri(uri)
            .header(API_KEY_HEADER, API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(coordinatesJson))
            .build();
    }

    private Delivery parseResponse(String responseBody) {
        Gson gson = new Gson();
        DistanceResponse distanceResponse = gson.fromJson(responseBody, DistanceResponse.class);
        double distance = distanceResponse.getRoutes().getFirst().getSummary().getDistance() / METERS_TO_KILOMETERS;
        double duration = distanceResponse.getRoutes().getFirst().getSummary().getDuration() / SECONDS_TO_MINUTES;
        return new Delivery(distance, duration);
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
