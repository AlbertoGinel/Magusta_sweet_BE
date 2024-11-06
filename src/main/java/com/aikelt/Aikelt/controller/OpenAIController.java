package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.service.OpenAIService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService){
        this.openAIService = openAIService;
    }

    @PostMapping("/wordAnalysis")
    public Mono<String> wordAnalysis(@RequestBody Map<String, String> body) {
        // Extract the "word" from the request body
        String word = body.get("word");

        // This is how you open choices
        return Mono.fromCallable(() -> {
            JSONObject analysis = openAIService.wordAnalysis(word);

            String content = analysis.getJSONArray("choices")
                    .getJSONObject(0) // Get the first choice
                    .getJSONObject("message") // Access the "message" object
                    .getString("content"); // Get the content as a string

            JSONObject contentJson = new JSONObject(content);



            return analysis.toString();
        });
    }


    @PostMapping("/scoreResponse")
    public Mono<String> scoreResponse(@RequestBody Map<String, String> body) {
        String language = body.get("language");
        String translation = body.get("translation");
        String humanResponse = body.get("humanResponse");

        // Assuming scoreResponse returns a JSONObject, you can convert it to a string for the response.
        JSONObject response = openAIService.scoreResponse(language, translation, humanResponse);

        return Mono.just(response.toString());
    }

    @PostMapping("/randomSentence")
    public Mono<String> randomSentence(@RequestBody Map<String, Object> body) {
        // Extract parameters from the request body
        String language = (String) body.get("language");
        int length = (Integer) body.get("length");
        String meaningsToUse = (String) body.get("meaningsToUse");

        // Call the service to generate the sentence
        return Mono.fromCallable(() -> {
            // Assuming the service method 'randomSentence' takes language, length, and meaningsToUse as parameters
            JSONObject sentence = openAIService.randomSentence(language, length, meaningsToUse);

            // Return the generated sentence as a string (or any other required transformation)
            return sentence.toString();
        });
    }
}

