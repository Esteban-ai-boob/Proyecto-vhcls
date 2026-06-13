package com.PPOOII.Laboratorio.APIs.GoogleMaps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class Geocoder {

    private static final String GEOCODING_RESOURCE =
        "https://maps.googleapis.com/maps/api/geocode/json?key=";
    private static final String API_KEY = resolveApiKey();

    public boolean isConfigured() {
        return API_KEY != null && !API_KEY.isBlank() && !"TU_API_KEY".equals(API_KEY);
    }

    public String GeocodeSync(String query) throws IOException, InterruptedException {
        if (!isConfigured()) {
            return null;
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GEOCODING_RESOURCE + API_KEY + "&address=" + encodedQuery))
            .timeout(Duration.ofMillis(2000))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getLating(String query) throws IOException, InterruptedException {
        String body = GeocodeSync(query);
        if (body == null || body.isBlank()) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        JsonNode status = root.get("status");
        String statusValue = status != null ? status.asText() : null;
        if (statusValue == null) {
            return null;
        }
        if ("REQUEST_DENIED".equals(statusValue) || "OVER_DAILY_LIMIT".equals(statusValue)) {
            JsonNode errorMessage = root.get("error_message");
            throw new IllegalStateException(
                errorMessage != null && !errorMessage.asText().isBlank()
                    ? errorMessage.asText()
                    : "Google Geocoding rechazo la solicitud"
            );
        }
        if (!"OK".equals(statusValue)) {
            return null;
        }

        JsonNode results = root.get("results");
        if (results == null || !results.isArray() || results.isEmpty()) {
            return null;
        }

        JsonNode location = root.get("results").get(0).get("geometry").get("location");
        if (location == null) {
            return null;
        }

        JsonNode lat = location.get("lat");
        JsonNode lng = location.get("lng");
        if (lat == null || lng == null) {
            return null;
        }

        return lat.asText() + "," + lng.asText();
    }

    private static String resolveApiKey() {
        String propertyValue = System.getProperty("google.maps.api-key");
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        String envValue = System.getenv("GOOGLE_MAPS_API_KEY");
        return envValue == null ? "" : envValue;
    }
}
