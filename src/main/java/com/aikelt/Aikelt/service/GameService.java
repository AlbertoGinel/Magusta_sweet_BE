package com.aikelt.Aikelt.service;

import com.aikelt.Aikelt.model.DictionaryWord;
import com.aikelt.Aikelt.model.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GameService {

        private final WordService wordService;
        private final OpenAIService openAIService;

        @Autowired
        public GameService(WordService wordService, OpenAIService openAIService) {
                this.wordService = wordService;
                this.openAIService = openAIService;
        }

        private final ConcurrentHashMap<UUID, Game> gameMap = new ConcurrentHashMap<>();

        public void createGame(Game.GameType type, int length, UUID user) {

                this.deleteGame(user);

                if (length < 3 || length > 10) {
                        throw new IllegalArgumentException("Length must be between 3 and 10 characters.");
                }
                String seeds = wordService.findSample(5, 10, user);


                JSONObject randomSentence = openAIService.randomSentence(type.toString(), length, seeds);
                Integer tokens = randomSentence.getJSONObject("usage").getInt("total_tokens");
                String sentence = randomSentence.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                JSONObject translateSentence = openAIService.translateSentence(type.toString(), sentence);
                Integer tokens2 = translateSentence.getJSONObject("usage").getInt("total_tokens");

                String translate = translateSentence.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                Game game = new Game(type, sentence, tokens + tokens2, length, seeds, translate, user);

                gameMap.put(user, game);
                this.updateGameEEWords(user);
        }

        public void updateGameEEWords(UUID user) {
                // Protect against the absence of the game:
                Game game = getGame(user);

                // Ensure the game object is not null to avoid NullPointerException
                if (game == null) {
                        throw new IllegalStateException("Game not found for user: " + user);
                }

                String estonianSentence = null;

                // Check the game type and assign the appropriate sentence
                if (game.getType() == Game.GameType.ESTONIAN) {
                        estonianSentence = game.getInitialSentence();
                } else if (game.getType() == Game.GameType.ENGLISH) {
                        estonianSentence = game.getTranslation();
                }

                System.out.println("\u001B[33mThe Estonian sentence is: " + estonianSentence + "\u001B[0m");

                // Check if the estonianSentence is not null or empty
                if (estonianSentence != null && !estonianSentence.isEmpty()) {
                        // Split the sentence into words and remove any commas or periods
                        List<String> estonianWords = Arrays.stream(estonianSentence.split("\\s+"))
                                .map(word -> word.replaceAll("[.,]", "")) // Remove periods and commas
                                .collect(Collectors.toList());

                        List<DictionaryWord> outputWords = new ArrayList<>();

                        // Loop through each word in estonianWords
                        for (String word : estonianWords) {


                                // Update the dictionary for the word and get the DictionaryWord object
                                DictionaryWord dictionaryWord = wordService.updateDictionaryByWord(word);

                                //filter showable
                                if (wordService.showableWord(user,dictionaryWord.getEstonian())){
                                        outputWords.add(dictionaryWord);
                                }
                        }

                        game.setEstonianWords(outputWords);
                } else {
                        throw new IllegalStateException("Sentence is empty or null.");
                }
        }

        public void finishGame(JSONArray jsonArray, UUID user){
                Game game = getGame(user);
                wordService.updatePersonalWords(jsonArray, user);
                this.deleteGame(user);
        }




        public Game getGame(UUID playerId) {
                return gameMap.get(playerId);
        }

        public void deleteGame(UUID playerId) {
                gameMap.remove(playerId);
        }



        public boolean hasGame(UUID playerId) {
                return gameMap.containsKey(playerId);
        }

        public void scoreResponse(UUID user, String humanResponse){

                Game game = getGame(user);

                // Ensure the game object is not null to avoid NullPointerException
                if (game == null) {
                        throw new IllegalStateException("Game not found for user: " + user);
                }

                JSONObject scoreResponse = openAIService.scoreResponse(Game.oppositeType(game.getType()), game.getTranslation(), humanResponse );

                Integer tokens = scoreResponse.getJSONObject("usage").getInt("total_tokens");
                String translate = scoreResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                game.addTokens(tokens);
                game.setHumanResponse(humanResponse);
                try {
                        double totalPointsDouble = Double.parseDouble(translate.trim());
                        int totalPoints = (int) Math.round(totalPointsDouble);
                        game.setTotalPoints(totalPoints);
                } catch (NumberFormatException e) {
                        // Handle the case where the translation cannot be parsed to an integer
                        System.err.println("Error parsing total points from translation: " + e.getMessage());
                }

                game.setStep(2);

        }
}




