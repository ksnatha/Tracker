package com.tracker.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapToJsonConverterTest {

    private MapToJsonConverter converter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        converter = new MapToJsonConverter(objectMapper);
    }

    @Test
    void convertToDatabaseColumn_withNullMap_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumn_withEmptyMap_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(new HashMap<>()));
    }

    @Test
    void convertToDatabaseColumn_withValidMap_returnsJsonString() {
        // Given
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        map.put("key3", true);

        // When
        String result = converter.convertToDatabaseColumn(map);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("\"key1\":\"value1\""));
        assertTrue(result.contains("\"key2\":123"));
        assertTrue(result.contains("\"key3\":true"));
    }

    @Test
    void convertToEntityAttribute_withNullString_returnsEmptyMap() {
        Map<String, Object> result = converter.convertToEntityAttribute(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_withEmptyString_returnsEmptyMap() {
        Map<String, Object> result = converter.convertToEntityAttribute("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertToEntityAttribute_withValidJsonString_returnsMap() {
        // Given
        String json = "{\"key1\":\"value1\",\"key2\":123,\"key3\":true}";

        // When
        Map<String, Object> result = converter.convertToEntityAttribute(json);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals(123, result.get("key2"));
        assertEquals(true, result.get("key3"));
    }
}