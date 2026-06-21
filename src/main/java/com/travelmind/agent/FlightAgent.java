package com.travelmind.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.travelmind.tool.FlightTools;

/**
 * Flight specialist agent: its own ChatClient + flight tools, exposed to the
 * orchestrator as the askFlightTeam tool (agents-as-tools pattern).
 */
@Component
public class FlightAgent {

    private final ChatClient flightClient;

    public FlightAgent(ChatClient.Builder builder, FlightTools flightTools) {
        this.flightClient = builder
                .defaultSystem("""
                        You are the flight team. Handle flight status and alternative flights only.
                        When asked for alternative flights for a specific flight:
                          1. First call flightStatus with the flight number to find its ORIGIN and DESTINATION.
                          2. Then call findAltFlight using EXACTLY that origin and destination from step 1 -
                             never guess, swap, or substitute other airports.
                        Use ONLY tool data, never invent. Be concise.
                        """)
                .defaultTools(flightTools)
                .build();
    }

    @Tool(description = "Ask the flight team about a flight's status or alternative flights")
    public String askFlightTeam(
            @ToolParam(description = "the flight question, including any flight number or route") String query) {
        return flightClient.prompt().user(query).call().content();
    }
}
