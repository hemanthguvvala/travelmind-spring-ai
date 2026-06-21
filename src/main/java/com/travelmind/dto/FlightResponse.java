package com.travelmind.dto;

import java.util.List;

public record FlightResponse(String flightNumber, FlightStatus status, int compensationEur,
		List<Alternative> alternatives) {
}
