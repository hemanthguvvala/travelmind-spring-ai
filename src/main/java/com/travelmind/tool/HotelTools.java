package com.travelmind.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Hotel tools (mock data for demo). In production these would call a hotel booking API.
 */
@Component
public class HotelTools {

    @Tool(description = "Find hotels in a city or near an airport for a number of nights")
    public String findHotel(@ToolParam(description = "city or airport area, e.g. New York or JFK") String city,
            @ToolParam(description = "number of nights") int nights) {
        String c = city.toUpperCase();
        if (c.contains("JFK") || c.contains("NEW YORK")) {
            return "Hotels near JFK for " + nights + " night(s): JFK Airport Inn (150 GBP/night); TWA Hotel (220 GBP/night).";
        } else if (c.contains("DEL") || c.contains("DELHI")) {
            return "Hotels in Delhi for " + nights + " night(s): Aerocity Holiday Inn (6500 INR/night); IBIS Delhi (4200 INR/night).";
        }
        return "Hotels in " + city + " for " + nights + " night(s): City Center Hotel (120 GBP/night); Budget Stay Inn (80 GBP/night).";
    }
}
