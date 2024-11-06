package com.aikelt.Aikelt.service;

import com.aikelt.Aikelt.model.DictionaryWord;
import com.aikelt.Aikelt.model.PersonalWord;
import com.aikelt.Aikelt.repository.JdbcWordRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
public class WordService {

    private final JdbcWordRepository wordRepository;
    private final OpenAIService openAIService;

    @Autowired
    public WordService(JdbcWordRepository wordRepository, OpenAIService openAIService) {
        this.wordRepository = wordRepository;
        this.openAIService = openAIService;
    }

    public List<DictionaryWord> findAllDictionaryWords() {
        return wordRepository.findAllDictionaryWords();
    }

    public List<PersonalWord> findAllPersonalWords() {
        return wordRepository.findAllPersonalWords();
    }

    public List<PersonalWord> findAllPersonalWordsByUser(UUID id) {
        return wordRepository.findAllPersonalWords();
    }

    public String findSample(Integer sampleLength, Integer firstGroup, UUID usedID){
        return wordRepository.findSample(sampleLength,firstGroup,usedID);
    }

    public DictionaryWord searchDictionaryWord(String targetWord) {

        return wordRepository.searchDictionaryWord(targetWord);
    }

    private DictionaryWord createUpdateWord(String word){

        JSONObject analysis = openAIService.wordAnalysis(word);

        // Parse the response
        String content = analysis.getJSONArray("choices")
                .getJSONObject(0) // Get the first choice
                .getJSONObject("message") // Access the "message" object
                .getString("content");

        JSONObject contentJson = new JSONObject(content);

        int level;
        try {
            level = Integer.parseInt(contentJson.getString("level"));
        } catch (NumberFormatException | NullPointerException e) {
            level = 0; // Or any default value you'd like to use if parsing fails
        }


        // Create the dictionary entry
        DictionaryWord newWord = new DictionaryWord(
                word,
                contentJson.getString("number"),
                level,
                contentJson.getString("person"),
                contentJson.getString("degree"),
                contentJson.getString("parts"), // Convert JSONArray to String if necessary
                contentJson.getString("english_translation"),
                contentJson.getString("type"),
                contentJson.getString("tense"),
                contentJson.getString("case"),
                contentJson.getString("basic_form")
        );

        return newWord;
    }


    public DictionaryWord updateDictionaryByWord(String word) {

        String targetWord = word.toLowerCase().trim();

        DictionaryWord foundWord1 = searchDictionaryWord(targetWord);

        if (foundWord1 == null) {

            UUID newID;

            DictionaryWord newWord1 = this.createUpdateWord(targetWord);

            if(newWord1.getEstonian() != newWord1.getBasicForm()){
                DictionaryWord foundWord2 = searchDictionaryWord(newWord1.getBasicForm());

                if (foundWord2 == null) {
                    DictionaryWord newWord2 = this.createUpdateWord(newWord1.getBasicForm());
                    newID = wordRepository.createDictionaryWord(newWord2);
                }else{
                    newID = foundWord2.getId();
                }

                newWord1.setBasicWordId(newID);
                System.out.println(newWord1);
            }

            UUID uuid = wordRepository.createDictionaryWord(newWord1);

            newWord1.setId(uuid);

            return newWord1;

        }else{
            return foundWord1;
        }


    }

    //Add a perfect word into the dictionary
    public UUID createDictionaryWord(DictionaryWord dictionaryWord) {

        //System.out.println("Create Dictionary Word: "+ dictionaryWord.getEstonian());

        return wordRepository.createDictionaryWord(dictionaryWord);
    }

    public int[] getHaloRanks(UUID userId) {
        return wordRepository.getHaloRanks(userId);
    }

    public boolean showableWord(UUID userId, String word) {
        return wordRepository.showableWord(userId, word);
    }

    public void updatePersonalWords(JSONArray jsonArray, UUID user) {
        // Retrieve ranks for the given user
        int[] ranks = wordRepository.getHaloRanks(user);

        // Loop through each element in the JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = jsonArray.getJSONObject(i);

            // Extract fields from each JSON object
            UUID word = UUID.fromString(element.getString("id"));
            int level = element.getInt("level");
            String estonian = element.getString("estonian");

            // Call updatePersonalWord for each object in the array
            wordRepository.updatePersonalWord(user, word, level, ranks, estonian);
        }
    }
}