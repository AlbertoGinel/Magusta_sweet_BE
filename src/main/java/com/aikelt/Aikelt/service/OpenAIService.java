package com.aikelt.Aikelt.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;

    // Inject API key from application.properties
    @Value("${openai.api.key}")
    private String apiKey;

    @Autowired
    public OpenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public JSONObject callOpenAI(List<Map<String, Object>> messages) {
        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messages);
        requestBody.put("function_call", null);
        requestBody.put("temperature", 1.0);


        // Make the API call and get the response
        try {
            String response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Block until the response is received

            // Return the response as a JSONObject

            return new JSONObject(response);

        } catch (WebClientResponseException e) {
            // Handle specific WebClient exceptions
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle any other exceptions
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public JSONObject randomSentence(String language, int wordLength, String meaningsToUse) {
        // Create the list to hold the messages
        List<Map<String, Object>> messages = new ArrayList<>();

        // Create the system message
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an assistant that generates random sentences in various languages, with constraints on length and potential meanings, no dot at the end.");

        // Add the system message to the list
        messages.add(systemMessage);

        // Create the user message
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        // Create the content for the user message dynamically based on input parameters
        String userContent = String.format(
                "I want a random sentence in %s, with approximately %d words. As far as possible, the sentence should match one or more of these meanings: %s.",
                language,
                wordLength,
                meaningsToUse);

        userMessage.put("content", userContent);

        // Add the user message to the list
        messages.add(userMessage);

        // Call the OpenAI function with the messages
        return callOpenAI(messages);
    }

    public JSONObject translateSentence(String language, String sentence) {
        // Create a list to hold the messages for translation
        List<Map<String, Object>> messages = new ArrayList<>();

        // Determine the direction of translation based on the language input
        String direction = "";
        if (language.equalsIgnoreCase("ENGLISH")) {
            direction = "English to Estonian";
        } else if (language.equalsIgnoreCase("ESTONIAN")) {
            direction = "Estonian to English";
        }

        // Create the system message for the translation task
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", String.format("You are a translator. Just give back the translation, no final dot. Translate the following sentence from %s.", direction));
        messages.add(systemMessage); // Add system message to list

        // Create the user message with the sentence to translate
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Translate this: '" + sentence + "'");
        messages.add(userMessage); // Add user message to list

        // Call the OpenAI function with the messages
        return callOpenAI(messages);
    }

    public JSONObject wordAnalysis(String word) {
        // Create a list to hold the messages for the dictionary entry
        List<Map<String, Object>> messages = new ArrayList<>();

        // Create the system message for the dictionary entry task
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a language assistant. Provide detailed information about the given word.  Give back a plain JSON format without any code block formatting or triple backticks");
        messages.add(systemMessage); // Add system message to list

        // Create the user message with the word to analyze
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", String.format(
                "For a given word in estonian, provide the following information only in json format, all strings in lower case. in parts, include each component of the given word, meaning, every suffix and prefix in a string. Level from 1 to 10 how basic is to learn, from colours 1, uncommon words 10, case could be nominative, genitive.. in nouns and adjectives: {\"type\": \"\", \"case\": \"\", \"number\": \"\", \"basic_form\": \"\", \"tense\": \"\", \"person\": \"\", \"degree\": \"\", \"parts\": \"component (meaning)\", \"level\": \"\", \"english_translation\": \"\"}. the word is '%s', basic_form field for the verbs will be the infinitive form of the word, for nouns nominative singular form.",
                word));
        messages.add(userMessage); // Add user message to list

        // Call the OpenAI function with the messages
        return callOpenAI(messages);
    }

    public JSONObject scoreResponse(String language, String solution, String humanResponse) {
        // List to hold messages
        List<Map<String, Object>> messages = new ArrayList<>();

        // User message
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        String userContent = String.format(
                "Calculate the percentage of letters that match in order between the human response (%s) and the original text (%s). " +
                        "If there are no letters that match in order, return 0. Only consider matching letters that appear in the same sequence. " +
                        "Provide just the percentage as a number with no additional explanation.",
                humanResponse, solution);



        userMessage.put("content", userContent);
        messages.add(userMessage);

        // Call OpenAI function and return response
        return callOpenAI(messages);
    }
}