package com.aikelt.Aikelt.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.aikelt.Aikelt.model.DictionaryWord;

import java.util.Random;
import com.aikelt.Aikelt.model.PersonalWord;
import com.aikelt.Aikelt.repository.mappers.DictionaryRowMapper;
import com.aikelt.Aikelt.repository.mappers.PersonalWordRowMapper;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcWordRepository {

    private final JdbcClient jdbcClient;
    private final DictionaryRowMapper dictionaryRowMapper;
    private final PersonalWordRowMapper personalWordRowMapper;
    private Random random;

    public JdbcWordRepository(JdbcClient jdbcClient, DictionaryRowMapper dictionaryRowMapper, PersonalWordRowMapper personalWordRowMapper) {
        this.jdbcClient = jdbcClient;
        this.dictionaryRowMapper = dictionaryRowMapper;
        this.personalWordRowMapper = personalWordRowMapper;
        this.random = new Random();
    }



    //DictionaryWord

    public List<DictionaryWord> findAllDictionaryWords() {
        return jdbcClient.sql("select * from dictionaryWord")
                .query(dictionaryRowMapper)
                .list();
    }

    public Optional<String> findByString(String word) {
        String sql = "SELECT id FROM Words WHERE idString = ?";

        List<String> results = jdbcClient.sql(sql)
                .param(word)
                .query((rs, rowNum) -> rs.getString("id"))
                .list();

        return results.stream().findFirst();
    }

    //PersonalWord


    public List<PersonalWord> findAllPersonalWords() {
        return jdbcClient.sql("select * from personalWord")
                .query(personalWordRowMapper)
                .list();
    }

    public List<PersonalWord> findAllPersonalWordsByUser(UUID id) {
        return jdbcClient.sql("select * from personalWord where userId = :id")
                .param("id", id) // Bind the UUID parameter to the query
                .query(personalWordRowMapper)
                .list();
    }

    public String findSample(Integer sampleLength, Integer firstGroup, UUID usedID) {
        // Null checks and proper range condition
        if (sampleLength == null || firstGroup == null || sampleLength <= 0 || firstGroup < sampleLength) {
            return "";
        }

        // Use parameterized queries to prevent SQL injection
        String sql = "SELECT * FROM personalWord WHERE userId = ? AND state = 'LIVE' ORDER BY halo ASC LIMIT ?";

        // Execute query with parameters for usedID and firstGroup
        List<PersonalWord> list = jdbcClient.sql(sql)
                .param(usedID)         // Use prepared statement to safely pass the UUID
                .param(firstGroup)    // Safely pass the group limit
                .query(personalWordRowMapper) // Adjust the mapper to `personalWordRowMapper`
                .list();

        // If the result list is empty, return an empty string
        if (list.isEmpty()) {
            return "";
        }

        // Shuffle the list to randomize order
        Collections.shuffle(list);

        // Limit the result to the sampleLength after shuffling
        List<String> words = list.stream()
                .limit(sampleLength)
                .map(personalWord -> personalWord.getEstonian())  // Assuming PersonalWord has getIdString method
                .collect(Collectors.toList());

        // Join the words into a single string separated by commas and trim any trailing spaces
        return String.join(", ", words).trim();
    }

    public DictionaryWord searchDictionaryWord(String targetWord) {
        String sql = "SELECT * FROM dictionaryWord WHERE estonian = ?";

        return jdbcClient.sql(sql)
                .param(1, targetWord)
                .query(DictionaryWord.class) // Assuming DictionaryWord is the class representing the table structure
                .optional()
                .orElse(null);
    }

    public UUID createDictionaryWord(DictionaryWord dictionaryWord) {
        // SQL query to check if the Estonian word already exists
        String checkSql = "SELECT id FROM DictionaryWord WHERE estonian = ?";

        // SQL query to insert a new word
        String insertSql = "INSERT INTO DictionaryWord (id, estonian, type, case_D, number, basic_form, tense, person, degree, parts, level_D, english_translation, basicWordId, state, errorText, created) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Generate a random UUID for the new word's id
        UUID id = UUID.randomUUID();

        // Get current time and format it in ISO-8601
        LocalDateTime now = LocalDateTime.now();

        Timestamp formattedTimestamp = Timestamp.valueOf(now);


        try {
            // Check if the Estonian word already exists, fetch a single result
            UUID existingId = jdbcClient.sql(checkSql)
                    .param(1, dictionaryWord.getEstonian())
                    .query(UUID.class)  // Specify the type of result expected (UUID)
                    .optional()  // Since the UUID may or may not exist, use 'optional' to handle null results
                    .orElse(null);

            // If the word exists, return the existing ID
            if (existingId != null) {
                return existingId;
            }

            // If the word doesn't exist, proceed with the insert
            jdbcClient.sql(insertSql)
                    .param(1, id) // UUID
                    .param(2, dictionaryWord.getEstonian()) // String
                    .param(3, dictionaryWord.getType()) // String
                    .param(4, dictionaryWord.getCase_D() != null && !dictionaryWord.getCase_D().isEmpty() ? dictionaryWord.getCase_D() : null) // Set to null if empty
                    .param(5, dictionaryWord.getNumber() != null && !dictionaryWord.getNumber().isEmpty() ? dictionaryWord.getNumber() : null) // Handle number similarly
                    .param(6, dictionaryWord.getBasicForm()) // String (nullable)
                    .param(7, dictionaryWord.getTense() != null && !dictionaryWord.getTense().isEmpty() ? dictionaryWord.getTense() : null) // Handle tense similarly
                    .param(8, dictionaryWord.getPerson() != null && !dictionaryWord.getPerson().isEmpty() ? dictionaryWord.getPerson() : null) // Handle person similarly
                    .param(9, dictionaryWord.getDegree() != null && !dictionaryWord.getDegree().isEmpty() ? dictionaryWord.getDegree() : null) // Handle degree similarly
                    .param(10, dictionaryWord.getParts()) // String (nullable)
                    .param(11, dictionaryWord.getLevel_D()) // Integer
                    .param(12, dictionaryWord.getEnglishTranslation()) // String (nullable)
                    .param(13, dictionaryWord.getBasicWordId() != null ? dictionaryWord.getBasicWordId() : null) // Handle basicWordId
                    .param(14, "ok") // State is defaulted to "ok"
                    .param(15, null) // Error text is defaulted to null
                    .param(16, formattedTimestamp) // Created is set to formatted timestamp
                    .update(); // Execute the update

            // Return the newly generated UUID
            return id;

        } catch (DuplicateKeyException e) {
            // Handle the duplicate key exception, log it, and continue
            System.out.println("Duplicate entry for Estonian word: " + dictionaryWord.getEstonian());
            throw new RuntimeException("Duplicate Estonian word found: " + dictionaryWord.getEstonian(), e);

        } catch (DataIntegrityViolationException e) {
            // Handle other potential data violations
            System.out.println("Data integrity violation: " + e.getMessage());
            throw new RuntimeException("Data integrity issue: " + e.getMessage(), e);
        }
    }

    public UUID createPersonalWord(PersonalWord personalWord) {
        // SQL query to check if the PersonalWord with specified attributes already exists
        String checkSql = "SELECT id FROM PersonalWord WHERE userId = ? AND dictionaryWordId = ?";

        // SQL query to insert a new PersonalWord
        String insertSql = "INSERT INTO PersonalWord (id, userId, dictionaryWordId, lastAppearance, estonian, halo, level_D, state) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Check if the word already exists
            UUID existingId = jdbcClient.sql(checkSql)
                    .param(1, personalWord.getUserId())
                    .param(2, personalWord.getDictionaryWordId())
                    .query(UUID.class)
                    .optional()
                    .orElse(null);

            // If the word exists, return the existing ID
            if (existingId != null) {
                return existingId;
            }

            // Generate a random UUID for the new word's id
            UUID id = UUID.randomUUID();

            // Get current time and format it in ISO-8601
            LocalDateTime now = LocalDateTime.now();

            Timestamp formattedTimestamp = Timestamp.valueOf(now);

            // Insert a new PersonalWord with generated ID and current timestamp
            jdbcClient.sql(insertSql)
                    .param(1, id)                                    // Generated UUID
                    .param(2, personalWord.getUserId())              // User ID
                    .param(3, personalWord.getDictionaryWordId()) // Dictionary Word ID
                    .param(4, formattedTimestamp)                    // Current timestamp
                    .param(5, personalWord.getEstonian())            // Estonian word
                    .param(6, personalWord.getHalo())                // Halo value
                    .param(7, 1)               // Level
                    .param(8, "LIVE")    // State as String
                    .update();

            // Return the generated UUID of the new PersonalWord
            return id;

        } catch (Exception e) {
            // Log the error and handle appropriately
            System.err.println("Error creating PersonalWord: " + e.getMessage());
            throw new RuntimeException("Failed to create PersonalWord", e);
        }
    }


    public int[] getHaloRanks(UUID userId) {
        int[] result = new int[4];
        int[] points = {49, 99, 499, 999};

        // Get the total row count for this user with state 'live'
        int rowCount = jdbcClient.sql("SELECT COUNT(*) FROM PersonalWord WHERE userId = :userId AND state = 'LIVE'")
                .param("userId", userId)
                .query(Integer.class)
                .single();

        // Get the highest halo value for fallback
        int highestHalo = jdbcClient.sql("SELECT halo FROM PersonalWord WHERE userId = :userId AND state = 'LIVE' ORDER BY halo DESC LIMIT 1")
                .param("userId", userId)
                .query(Integer.class)
                .single();

        // Loop through the 4 specified points
        for (int i = 0; i < points.length; i++) {
            if (rowCount > points[i]) {  // Check if the row at the desired offset exists
                String sqlQuery = "SELECT halo FROM PersonalWord WHERE userId = :userId AND state = 'LIVE' ORDER BY halo ASC LIMIT 1 OFFSET " + points[i];
                int halo = jdbcClient.sql(sqlQuery)
                        .param("userId", userId)
                        .query(Integer.class)
                        .single();
                result[i] = halo;
            } else {
                // If the row does not exist, assign the highest halo value
                result[i] = highestHalo;
            }
        }

        return result; // Return the array of 4 halo values
    }

    public boolean showableWord(UUID userId, String word) {
        // SQL query to check for the presence and state of the word for the user
        String sql = """
            SELECT CASE 
                       WHEN p.id IS NULL THEN TRUE    -- Word not found in the DB, showable
                       WHEN p.state = 'LIVE' THEN TRUE  -- Word found in 'LIVE' state, showable
                       ELSE FALSE                     -- Word found but not in 'LIVE' state, not showable
                   END AS is_showable
            FROM (SELECT 1) AS dummy      -- A dummy table to allow the LEFT JOIN when no row exists for the word
            LEFT JOIN PersonalWord p
            ON p.userId = :userId AND p.estonian = :word
        """;

        try {
            // Execute the query and return the result as a boolean
            return jdbcClient.sql(sql)
                    .param("userId", userId)
                    .param("word", word)
                    .query(Boolean.class)
                    .single(); // Returns true if showable, false otherwise
        } catch (EmptyResultDataAccessException e) {
            // Handle case where no result is found; return a default value
            return false; // or true if that aligns better with your logic
        }
    }


    private int calculateNewHalo(int level, int[] ranks) {
        int newHalo = 0;
        switch (level) {
            case 1:
                newHalo = random.nextInt(ranks[0]);
                break;
            case 2:
                newHalo = random.nextInt(ranks[1] - ranks[0]) + ranks[0];
                break;
            case 3:
                newHalo = random.nextInt(ranks[2] - ranks[1]) + ranks[1];
                break;
            case 4:
                newHalo = random.nextInt(ranks[3] - ranks[2]) + ranks[2];
                break;
            case 5:
                newHalo = ranks[3] + random.nextInt(2000);
                break;
            case 6:
                newHalo = ranks[3] + random.nextInt(2000);
                break;
        }
        return newHalo;
    }

    public void updatePersonalWord(UUID userID, UUID word, int level, int[] ranks, String estonian) {

        try {

            int count = jdbcClient.sql("SELECT COUNT(*) FROM PERSONALWORD WHERE userID = :userId AND dictionarywordid = :word")
                    .param("userId", userID)
                    .param("word", word)
                    .query(Integer.class)
                    .single();

            boolean wordExists = count > 0;

            int newHalo = calculateNewHalo(level, ranks);
            if (wordExists) {

                String newState = (level == 6) ? "MASTERED" : "LIVE";

                LocalDateTime now = LocalDateTime.now();

                Timestamp formattedTimestamp = Timestamp.valueOf(now);

                String updateQuery = "UPDATE PERSONALWORD SET lastAppearance = :lastAppearance, halo = :newHalo, state = :newState " +
                        "WHERE userID = :userID AND dictionarywordid = :word";

                jdbcClient.sql(updateQuery)
                        .param("lastAppearance", formattedTimestamp)  // Use the formatted timestamp
                        .param("newHalo", newHalo)
                        .param("newState", newState.toString())       // Ensure newState is correctly formatted
                        .param("userID", userID)
                        .param("word", word)
                        .update();
            } else {
                // Word doesn't exist, create a new entry
                PersonalWord newWord = new PersonalWord( userID, word, estonian,newHalo,1, PersonalWord.State.LIVE);
                createPersonalWord(newWord);
            }
        } catch (Exception e) {
            System.out.println("Error updating personal word: " + e.getMessage());
        }
    }
}