package com.bracits.surveyengine.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

/**
 * Accepts ISO-8601 date-time values and date-only values (yyyy-MM-dd).
 * Date-only values are normalized to end-of-day UTC.
 */
public class FlexibleInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        try {
            return Instant.parse(normalized);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate date = LocalDate.parse(normalized);
                return date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
            } catch (DateTimeParseException ex) {
                throw InvalidFormatException.from(
                        parser,
                        "Invalid date value. Use ISO-8601 date-time (e.g. 2026-03-31T23:59:59Z) or date (yyyy-MM-dd).",
                        normalized,
                        Instant.class);
            }
        }
    }
}
