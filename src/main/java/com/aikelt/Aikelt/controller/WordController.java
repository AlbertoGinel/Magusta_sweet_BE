package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.DictionaryWord;
import com.aikelt.Aikelt.model.PersonalWord;
import com.aikelt.Aikelt.service.CustomUserDetailsService;
import com.aikelt.Aikelt.service.WordService;
import org.json.JSONArray;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;
    private final CustomUserDetailsService customUserDetailsService;

    public WordController(WordService wordService, CustomUserDetailsService customUserDetailsService) {
        this.wordService = wordService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/allDictionaryWords")
    List<DictionaryWord> findAllDictionaryWords() {
        return wordService.findAllDictionaryWords();
    }

    @GetMapping("/allPersonalWords")
    List<PersonalWord> findAllPersonalWords() {
        return wordService.findAllPersonalWords();
    }

    @GetMapping("/allPersonalWordsbyID")
    public List<PersonalWord> findAllPersonalWordsbyID() {
        // Get the current authenticated user from the SecurityContext (i.e., token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // or use custom user details if necessary

        UUID id = customUserDetailsService.findIDByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        // Call the service with the extracted user and id
        return wordService.findAllPersonalWordsByUser(id);
    }

    @PostMapping("/findSample")
    public String findSample(@RequestBody Map<String, Object> requestBody) {
        // Extract values from the request body
        Integer sampleLength = (Integer) requestBody.get("sampleLength");
        Integer firstGroup = (Integer) requestBody.get("firstGroup");
        UUID usedID = UUID.fromString((String) requestBody.get("usedID")); // Convert string to UUID

        // Call the service method with extracted parameters
        return wordService.findSample(sampleLength, firstGroup, usedID);
    }


    @PostMapping("/searchDictionaryWord")
    public DictionaryWord searchDictionaryWord(@RequestBody Map<String, Object> requestBody){

        String targetWord = (String) requestBody.get("targetWord");

        return wordService.searchDictionaryWord(targetWord);
    }

    @PostMapping("/updateDictionaryByWord")
    public DictionaryWord updateDictionaryByWord(@RequestBody Map<String, Object> requestBody){

        String targetWord = (String) requestBody.get("targetWord");

        return wordService.updateDictionaryByWord(targetWord);
    }

    @PostMapping("/createDictionaryWord")
    public String createDictionaryWord(@RequestBody DictionaryWord dictionaryWord) {
        // Call the service to create a new dictionary word
        UUID id = wordService.createDictionaryWord(dictionaryWord);

        // Return success message
        return "Dictionary word created successfully! " + id;
    }


    @PostMapping("/getHaloRanks")
    public int[] getHaloRanks(@RequestBody Map<String, Object> requestBody) {
        // Extract the UUID from the request body
        UUID id = UUID.fromString((String) requestBody.get("usedID"));

        // Call the service with the extracted id
        return wordService.getHaloRanks(id);
    }

    @PostMapping("/showableWord")
    public boolean showableWord(@RequestBody Map<String, Object> requestBody) {
        // Extract the UUID from the request body
        UUID id = UUID.fromString((String) requestBody.get("usedID"));
        String word = (String) requestBody.get("word");

        // Call the service with the extracted id
        return wordService.showableWord(id,word);
    }

    @PostMapping("/updatePersonalWords")
    public void updatePersonalWords(@RequestBody Map<String, Object> requestBody) {
        // Extract userID from requestBody
        UUID userID = UUID.fromString((String) requestBody.get("userID"));

        // Extract the words array
        JSONArray wordsList = new JSONArray((List<?>) requestBody.get("wordsList"));

        // Call the service with the extracted userID and wordsList
        wordService.updatePersonalWords(wordsList, userID);
    }
}