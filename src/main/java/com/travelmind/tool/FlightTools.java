package com.travelmind.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Flight tools (mock data for demo). In production these would call real flight APIs.
 */
@Component
public class FlightTools {

    @Tool(description = "Get the current status of a flight by its flight number")
    public String flightStatus(@ToolParam(description = "flight number, e.g. BA117") String flightNumber) {
        return switch (flightNumber.toUpperCase()) {
            case "BA117" -> "BA117 (LHR -> JFK) is cancelled, crew shortage, EU261 band 600 EUR";
            case "AI501" -> "AI501 (BLR -> DEL) is delayed by 90 min";
            default -> flightNumber.toUpperCase() + " is on time";
        };
    }

    @Tool(description = "Find alternative flights between two airports")
    public String findAltFlight(@ToolParam(description = "origin airport code, e.g. LHR") String origin,
            @ToolParam(description = "destination airport code, e.g. JFK") String dest) {
        String route = origin.toUpperCase() + "-" + dest.toUpperCase();
        return switch (route) {
            case "LHR-JFK" -> "Alternatives for " + route + ": BA179 departs in 4h (420 GBP); VS003 departs in 6h (385 GBP).";
            case "BLR-DEL" -> "Alternatives for " + route + ": AI777 departs in 3h (7200 INR); 6E355 departs in 5h (6100 INR).";
            default -> "Alternatives for " + origin.toUpperCase() + " -> " + dest.toUpperCase()
                    + ": nearest option departs tomorrow morning (500 GBP).";
        };
    }
}
