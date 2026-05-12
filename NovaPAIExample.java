// NovaPAI Java SDK Example
// Maven: add openai-java dependency
// Docs: https://novapai.ai

// pom.xml dependency:
// <dependency>
//   <groupId>com.openai</groupId>
//   <artifactId>openai-java</artifactId>
//   <version>2.4.0</version>
// </dependency>

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.*;

import java.util.List;
import java.util.Map;

public class NovaPAIExample {

    public static void main(String[] args) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey("your-api-key")
                .baseUrl("https://api.novapai.ai/router/v1")
                .build();

        // ── Basic Chat ──────────────────────────────────────
        basicChat(client);

        // ── Streaming ───────────────────────────────────────
        streamChat(client);

        // ── Function Calling ────────────────────────────────
        functionCalling(client);

        // ── JSON Mode ───────────────────────────────────────
        jsonMode(client);
    }

    static void basicChat(OpenAIClient client) {
        ChatCompletion response = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model("deepseek-v4-pro")
                        .messages(List.of(
                                ChatCompletionMessageParam.ofSystem(
                                        ChatCompletionSystemMessageParam.builder()
                                                .content("You are a helpful assistant.")
                                                .build()
                                ),
                                ChatCompletionMessageParam.ofUser(
                                        ChatCompletionUserMessageParam.builder()
                                                .content("Hello!")
                                                .build()
                                )
                        ))
                        .build()
        );

        System.out.println(response.choices().get(0).message().content().orElse(""));
    }

    static void streamChat(OpenAIClient client) {
        client.chat().completions().createStreaming(
                ChatCompletionCreateParams.builder()
                        .model("deepseek-v4-pro")
                        .messages(List.of(
                                ChatCompletionMessageParam.ofUser(
                                        ChatCompletionUserMessageParam.builder()
                                                .content("Tell me a joke")
                                                .build()
                                )
                        ))
                        .build()
        ).stream().forEach(chunk ->
                chunk.choices().stream()
                        .map(c -> c.delta().content().orElse(""))
                        .forEach(System.out::print)
        );
        System.out.println();
    }

    static void functionCalling(OpenAIClient client) {
        ChatCompletion response = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model("deepseek-v4-pro")
                        .messages(List.of(
                                ChatCompletionMessageParam.ofUser(
                                        ChatCompletionUserMessageParam.builder()
                                                .content("What's the weather in Tokyo?")
                                                .build()
                                )
                        ))
                        .tools(List.of(
                                ChatCompletionTool.builder()
                                        .function(
                                                FunctionDefinition.builder()
                                                        .name("get_weather")
                                                        .description("Get current weather for a city")
                                                        .parameters(Map.of(
                                                                "type", "object",
                                                                "properties", Map.of(
                                                                        "city", Map.of("type", "string", "description", "City name")
                                                                ),
                                                                "required", List.of("city")
                                                        ))
                                                        .build()
                                        )
                                        .build()
                        ))
                        .build()
        );

        ChatCompletionMessageToolCall tc = response.choices().get(0).message().toolCalls().get().get(0);
        System.out.println("Function: " + tc.function().name());
        System.out.println("Args: " + tc.function().arguments());
    }

    static void jsonMode(OpenAIClient client) {
        ChatCompletion response = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model("deepseek-v4-pro")
                        .messages(List.of(
                                ChatCompletionMessageParam.ofSystem(
                                        ChatCompletionSystemMessageParam.builder()
                                                .content("Extract company info as JSON.")
                                                .build()
                                ),
                                ChatCompletionMessageParam.ofUser(
                                        ChatCompletionUserMessageParam.builder()
                                                .content("Apple Inc. is based in Cupertino, founded in 1976.")
                                                .build()
                                )
                        ))
                        .responseFormat(ChatCompletionResponseFormat.ofJsonObject())
                        .build()
        );

        System.out.println(response.choices().get(0).message().content().orElse(""));
    }
}
