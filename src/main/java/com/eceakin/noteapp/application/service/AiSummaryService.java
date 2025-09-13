package com.eceakin.noteapp.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSummaryService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.token}")
    private String apiToken;

    public String summarizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "No content to summarize";
        }

        // Özetleme için daha uzun metinleri hedefleyerek bu eşiği 200'e çıkaralım.
        // Daha kısa notlar için özetleme yapmak anlamsız olabilir.
        if (text.length() < 200) {
            return "Note content is too short for a meaningful summary.";
        }

        try {
            log.info("Hugging Face API called with URL: {}", apiUrl);
            log.info("Text to summarize (first 100 chars): {}", text.substring(0, Math.min(100, text.length())));

            WebClient webClient = webClientBuilder
                    .defaultHeader("Authorization", "Bearer " + apiToken)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            // Yeni model (BART) için parametreleri optimize edelim.
            // max_length ve min_length değerlerini notlarının tipine göre ayarlayabilirsin.
            // Bu değerler, genelde 50-200 aralığında iyi sonuçlar verir.
            // "do_sample" parametresini "true" yaparak daha yaratıcı özetler elde edebilirsin.
            // Hugging Face Inference API'sinin parametreleri modelden modele değişebilir,
            // bu yüzden kullandığın modelin dokümantasyonunu kontrol etmek faydalı olur.
            String requestBody = String.format("""
                    {
                        "inputs": "%s",
                        "parameters": {
                            "max_length": 150,
                            "min_length": 50,
                            "do_sample": true
                        }
                    }
                    """, escapeJson(text));

            log.info("Request payload: {}", requestBody);

            String response = webClient.post()
                    .uri(apiUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60)) // Daha büyük model için timeout süresini artırmak iyi olur
                    .block();

            log.info("Hugging Face API response: {}", response);

            return extractSummaryFromResponse(response);

        } catch (WebClientResponseException e) {
            log.error("Error calling Hugging Face API. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 503) {
                return "AI service is currently loading. Please try again in a moment.";
            } else if (e.getStatusCode().value() == 401) {
                return "Authentication failed. Please check API token.";
            } else if (e.getStatusCode().value() == 404) {
                return "Model not found or inaccessible. Please check your model URL in application.properties.";
            }
            return "Unable to generate summary. API error: " + e.getStatusCode();

        } catch (Exception e) {
            log.error("Unexpected error during summarization: {}", e.getMessage(), e);
            return "Error occurred while generating summary: " + e.getMessage();
        }
    }

    private String extractSummaryFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("error")) {
                String error = rootNode.get("error").asText();
                log.error("API returned error: {}", error);
                return "Unable to generate summary: " + error;
            }

            if (rootNode.isArray() && rootNode.size() > 0) {
                JsonNode firstResult = rootNode.get(0);
                if (firstResult.has("summary_text")) {
                    return firstResult.get("summary_text").asText();
                }
            }
            // Yanıt formatında bir hata varsa, daha spesifik bir hata döndürmek için
            // bu kısım daha detaylı yazılabilir.
            return "Unable to extract summary from response. Unexpected format.";

        } catch (Exception e) {
            log.error("Error parsing summary response: {}", e.getMessage());
            return "Error parsing summary response.";
        }
    }

    private String escapeJson(String text) {
        // Unicode karakterleri de doğru bir şekilde işlemek için daha kapsamlı bir yöntem
        // kullanılabilir, ancak bu yöntem çoğu durumda yeterlidir.
        return text.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                // Backslash karakterini de escape etmeliyiz
                .replace("\\", "\\\\");
    }
}