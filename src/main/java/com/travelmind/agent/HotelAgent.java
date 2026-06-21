package com.travelmind.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.travelmind.tool.HotelTools;

/**
 * Hotel specialist agent, exposed to the orchestrator as the askHotelTeam tool.
 */
@Component
public class HotelAgent {

    private final ChatClient hotelClient;

    public HotelAgent(ChatClient.Builder builder, HotelTools hotelTools) {
        this.hotelClient = builder
                .defaultSystem("""
                        You are the hotel team. You find hotels in a city or near an airport
                        for a given number of nights. Use ONLY tool data, never invent. Be concise.
                        """)
                .defaultTools(hotelTools)
                .build();
    }

    @Tool(description = "Ask the hotel team to find hotels in a city or near an airport for a number of nights")
    public String askHotelTeam(
            @ToolParam(description = "the hotel request, including the city or airport area and the number of nights") String query) {
        return hotelClient.prompt().user(query).call().content();
    }
}
