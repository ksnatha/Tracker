# Devin Instructions: Spring AI MCP Server Module

## Objective

Create a standalone Gradle module called `mcp-module` that implements an MCP (Model Context Protocol)
server using Spring AI. The server exposes Tools, Resources, and Prompts following the MCP standard,
testable via MCP Inspector at `http://localhost:8080`.

---

## Tech Stack

- Java 21
- Spring Boot 3.4.1
- Spring AI 1.0.0-M6
- Gradle (Groovy DSL)
- Transport: HTTP + SSE (for MCP Inspector compatibility)

---

## Step 1: Create the Project Structure

Create the following directory layout:

```
mcp-module/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── java/
        │   └── com/example/mcp/
        │       ├── McpServerApplication.java
        │       ├── config/
        │       │   └── McpServerConfig.java
        │       ├── tools/
        │       │   └── WeatherTools.java
        │       ├── resources/
        │       │   └── WeatherResources.java
        │       └── prompts/
        │           └── WeatherPrompts.java
        └── resources/
            └── application.yml
```

---

## Step 2: Create `settings.gradle`

```groovy
rootProject.name = 'mcp-module'
```

---

## Step 3: Create `build.gradle`

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.1'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.example'
version = '1.0.0-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url 'https://repo.spring.io/milestone' }
    maven { url 'https://repo.spring.io/snapshot' }
}

ext {
    set('springAiVersion', '1.0.0-M6')
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.ai:spring-ai-bom:${springAiVersion}"
    }
}

dependencies {
    // Core MCP server with HTTP/SSE transport (required for MCP Inspector)
    implementation 'org.springframework.ai:spring-ai-mcp-server-webmvc-spring-boot-starter'

    // Spring Web for HTTP/SSE transport layer
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Optional: health check endpoint
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

**Dependency notes for Devin:**
- `spring-ai-mcp-server-webmvc-spring-boot-starter` is the single most important dependency — it pulls in the MCP server engine, MCP SDK, SSE support, and all Spring AI MCP autoconfiguration.
- The `spring-ai-bom` in `dependencyManagement` controls all Spring AI artifact versions — do NOT add version numbers to individual `spring-ai-*` dependencies.
- The Spring Milestone repo (`repo.spring.io/milestone`) is mandatory because Spring AI 1.0.0-M6 is a milestone release not published to Maven Central.

---

## Step 4: Create `application.yml`

Location: `src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  ai:
    mcp:
      server:
        name: weather-mcp-server
        version: 1.0.0
        sse-message-endpoint: /mcp/messages

management:
  endpoints:
    web:
      exposure:
        include: health, info
```

---

## Step 5: Create `McpServerApplication.java`

Location: `src/main/java/com/example/mcp/McpServerApplication.java`

```java
package com.example.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}
```

---

## Step 6: Create `WeatherTools.java`

Location: `src/main/java/com/example/mcp/tools/WeatherTools.java`

This class exposes MCP **Tools** — callable functions that MCP clients can invoke.
Use `@Tool` on each method and `@ToolParam` on each parameter. No other annotations needed.
Return type must be `String`.

```java
package com.example.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherTools {

    @Tool(name = "get_current_weather",
          description = "Get the current weather conditions for a given city. Returns temperature, condition, humidity and wind speed.")
    public String getCurrentWeather(
            @ToolParam(description = "The name of the city, e.g. 'London' or 'New York'") String city,
            @ToolParam(description = "Temperature unit: 'celsius' or 'fahrenheit'. Defaults to celsius.") String unit) {

        String tempUnit = "fahrenheit".equalsIgnoreCase(unit) ? "°F" : "°C";
        double temp     = "fahrenheit".equalsIgnoreCase(unit) ? 72.5 : 22.5;

        return String.format("""
                Current weather in %s:
                  Temperature : %.1f %s
                  Condition   : Partly Cloudy
                  Humidity    : 65%%
                  Wind Speed  : 15 km/h
                """, city, temp, tempUnit);
    }

    @Tool(name = "get_weather_forecast",
          description = "Get a 3-day weather forecast for a given city.")
    public String getWeatherForecast(
            @ToolParam(description = "The name of the city to get the forecast for") String city) {

        return String.format("""
                3-Day Forecast for %s:
                  Day 1 (Today)    : Partly Cloudy, High 24°C / Low 18°C
                  Day 2 (Tomorrow) : Sunny,         High 27°C / Low 19°C
                  Day 3            : Rain,           High 20°C / Low 15°C
                """, city);
    }
}
```

---

## Step 7: Create `WeatherResources.java`

Location: `src/main/java/com/example/mcp/resources/WeatherResources.java`

MCP **Resources** are readable data (like files/documents) exposed to MCP clients via a URI.
Each resource is a `McpServerFeatures.SyncResourceRegistration` — a record pairing a resource
descriptor with a handler that returns its content.

```java
package com.example.mcp.resources;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherResources {

    public McpServerFeatures.SyncResourceRegistration weatherStationsResource() {
        var resource = new McpSchema.Resource(
                "weather://stations/all",
                "Weather Stations",
                "A list of all available weather monitoring stations with their IDs and locations.",
                "application/json",
                null
        );

        return new McpServerFeatures.SyncResourceRegistration(resource, request -> {
            String stationsJson = """
                    {
                      "stations": [
                        { "id": "STN001", "name": "London Heathrow", "lat": 51.47,  "lon": -0.45,   "country": "GB" },
                        { "id": "STN002", "name": "New York JFK",    "lat": 40.63,  "lon": -73.78,  "country": "US" },
                        { "id": "STN003", "name": "Tokyo Haneda",    "lat": 35.55,  "lon": 139.78,  "country": "JP" },
                        { "id": "STN004", "name": "Sydney Airport",  "lat": -33.94, "lon": 151.18,  "country": "AU" }
                      ]
                    }
                    """;

            return new McpSchema.ReadResourceResult(
                    List.of(new McpSchema.TextResourceContents(
                            "weather://stations/all", "application/json", stationsJson))
            );
        });
    }

    public McpServerFeatures.SyncResourceRegistration weatherApiDocsResource() {
        var resource = new McpSchema.Resource(
                "weather://docs/api",
                "Weather API Documentation",
                "Documentation for the Weather MCP tools and available parameters.",
                "text/markdown",
                null
        );

        return new McpServerFeatures.SyncResourceRegistration(resource, request -> {
            String docs = """
                    # Weather MCP API Documentation

                    ## Tools
                    - **get_current_weather** — city (required), unit (optional: celsius/fahrenheit)
                    - **get_weather_forecast** — city (required)

                    ## Resources
                    - weather://stations/all — All monitoring stations (JSON)
                    - weather://docs/api     — This documentation (Markdown)

                    ## Prompts
                    - weather_summary    — Weather briefing for a city
                    - weather_comparison — Compare weather between two cities
                    """;

            return new McpSchema.ReadResourceResult(
                    List.of(new McpSchema.TextResourceContents(
                            "weather://docs/api", "text/markdown", docs))
            );
        });
    }
}
```

---

## Step 8: Create `WeatherPrompts.java`

Location: `src/main/java/com/example/mcp/prompts/WeatherPrompts.java`

MCP **Prompts** are reusable message templates that MCP clients fetch and pass to an LLM.
They accept arguments and return a list of `PromptMessage` objects.
The LLM is NOT called here — only the message template is returned.

```java
package com.example.mcp.prompts;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WeatherPrompts {

    public McpServerFeatures.SyncPromptRegistration weatherSummaryPrompt() {
        var prompt = new McpSchema.Prompt(
                "weather_summary",
                "Generate a detailed weather briefing for a specific city",
                List.of(new McpSchema.PromptArgument("city", "The city to generate a weather briefing for", true))
        );

        return new McpServerFeatures.SyncPromptRegistration(prompt, request -> {
            Map<String, Object> args = request.arguments();
            String city = args != null && args.containsKey("city") ? args.get("city").toString() : "Unknown City";

            String promptText = String.format("""
                    You are a professional meteorologist. Provide a comprehensive weather briefing for %s.
                    Include: current conditions, today's forecast, 3-day outlook, any warnings, and activity recommendations.
                    Use a professional but easy-to-understand tone.
                    """, city);

            return new McpSchema.GetPromptResult(
                    "Weather briefing prompt for " + city,
                    List.of(new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent(promptText)))
            );
        });
    }

    public McpServerFeatures.SyncPromptRegistration weatherComparisonPrompt() {
        var prompt = new McpSchema.Prompt(
                "weather_comparison",
                "Compare current weather and climate between two cities",
                List.of(
                        new McpSchema.PromptArgument("city1", "First city for comparison", true),
                        new McpSchema.PromptArgument("city2", "Second city for comparison", true)
                )
        );

        return new McpServerFeatures.SyncPromptRegistration(prompt, request -> {
            Map<String, Object> args = request.arguments();
            String city1 = args != null && args.containsKey("city1") ? args.get("city1").toString() : "City 1";
            String city2 = args != null && args.containsKey("city2") ? args.get("city2").toString() : "City 2";

            String promptText = String.format("""
                    Compare the weather and climate of %s and %s.
                    Include: side-by-side current conditions table, climate differences, best time to visit each, and which has better weather right now.
                    Be concise and use a comparison table where appropriate.
                    """, city1, city2);

            return new McpSchema.GetPromptResult(
                    String.format("Weather comparison: %s vs %s", city1, city2),
                    List.of(new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent(promptText)))
            );
        });
    }
}
```

---

## Step 9: Create `McpServerConfig.java`

Location: `src/main/java/com/example/mcp/config/McpServerConfig.java`

This is the central wiring class. Spring AI's MCP autoconfiguration detects these specific bean types:
- `ToolCallbackProvider` → registers MCP Tools
- `List<SyncResourceRegistration>` → registers MCP Resources
- `List<SyncPromptRegistration>` → registers MCP Prompts

```java
package com.example.mcp.config;

import com.example.mcp.prompts.WeatherPrompts;
import com.example.mcp.resources.WeatherResources;
import com.example.mcp.tools.WeatherTools;

import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider weatherToolCallbackProvider(WeatherTools weatherTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherTools)
                .build();
    }

    @Bean
    public List<McpServerFeatures.SyncResourceRegistration> mcpResources(WeatherResources weatherResources) {
        return List.of(
                weatherResources.weatherStationsResource(),
                weatherResources.weatherApiDocsResource()
        );
    }

    @Bean
    public List<McpServerFeatures.SyncPromptRegistration> mcpPrompts(WeatherPrompts weatherPrompts) {
        return List.of(
                weatherPrompts.weatherSummaryPrompt(),
                weatherPrompts.weatherComparisonPrompt()
        );
    }
}
```

---

## Step 10: Build and Run

```bash
cd mcp-module
./gradlew bootRun
```

Expected console output on startup:
```
Started McpServerApplication in X.XXX seconds
Tomcat started on port 8080
```

---

## Step 11: Test with MCP Inspector

1. Open MCP Inspector: https://inspector.modelcontextprotocol.io
2. Set transport type to **SSE**
3. Enter URL: `http://localhost:8080/sse`
4. Click **Connect**
5. You should see the following registered:
   - **Tools tab**: `get_current_weather`, `get_weather_forecast`
   - **Resources tab**: `weather://stations/all`, `weather://docs/api`
   - **Prompts tab**: `weather_summary`, `weather_comparison`

### Test a Tool
- Select `get_current_weather`
- Input: `{ "city": "London", "unit": "celsius" }`
- Click Run → should return weather data

### Test a Resource
- Select `weather://stations/all`
- Click Read → should return JSON list of stations

### Test a Prompt
- Select `weather_summary`
- Input: `{ "city": "Tokyo" }`
- Click Get Prompt → should return the prompt message template

---

## Key Design Rules (Important for Devin)

1. **No custom annotations** — use only `@Tool` and `@ToolParam` from `org.springframework.ai.tool.annotation`
2. **Tool return type must be `String`** — MCP tool results are always text
3. **`McpServerConfig` is the only wiring point** — all tools/resources/prompts are registered here
4. **Do NOT add Spring AI version numbers** to individual dependencies — the BOM manages them
5. **Milestone repo is required** — Spring AI 1.0.0-M6 is not on Maven Central
6. **`MethodToolCallbackProvider`** scans for `@Tool` methods — just pass your `@Service` beans to `.toolObjects()`
7. **Resources and Prompts are NOT Spring beans themselves** — they return registration objects collected into `List<>` beans in `McpServerConfig`

---

## Summary of What Gets Created

| File | Purpose |
|------|---------|
| `build.gradle` | Gradle build with Spring AI BOM and MCP starter |
| `settings.gradle` | Project name |
| `application.yml` | Port, server name, SSE endpoint config |
| `McpServerApplication.java` | Spring Boot entry point |
| `WeatherTools.java` | 2 MCP Tools using `@Tool` / `@ToolParam` |
| `WeatherResources.java` | 2 MCP Resources (stations list + API docs) |
| `WeatherPrompts.java` | 2 MCP Prompts (summary + city comparison) |
| `McpServerConfig.java` | Wires all tools, resources, prompts as Spring beans |
