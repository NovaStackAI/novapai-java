// NovaPAI Java SDK Example
// Maven: add openai-java dependency
// Docs: https://api.novapai.ai

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
}
