package com.aikelt.Aikelt.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import com.aikelt.Aikelt.model.DictionaryWord;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

    @Component
    public class DictionaryRowMapper implements RowMapper<DictionaryWord> {

        @Override
        public DictionaryWord mapRow(ResultSet rs, int rowNum) throws SQLException {
            DictionaryWord dictionaryWord = new DictionaryWord();

            // Map fields from ResultSet to DictionaryWord object
            dictionaryWord.setId((UUID) rs.getObject("id")); // Use getObject for UUID
            dictionaryWord.setEstonian(rs.getString("estonian"));
            dictionaryWord.setType(rs.getString("type"));
            dictionaryWord.setCase_D(rs.getString("case_D"));
            dictionaryWord.setNumber(rs.getString("number"));
            dictionaryWord.setBasicForm(rs.getString("basic_form"));
            dictionaryWord.setTense(rs.getString("tense"));
            dictionaryWord.setPerson(rs.getString("person"));
            dictionaryWord.setDegree(rs.getString("degree"));
            dictionaryWord.setParts(rs.getString("parts")); // Assuming parts are stored as TEXT
            dictionaryWord.setLevel_D(rs.getInt("level_D"));
            dictionaryWord.setEnglishTranslation(rs.getString("english_translation"));
            dictionaryWord.setBasicWordId((UUID) rs.getObject("basicWordId"));
            dictionaryWord.setState(rs.getString("state"));
            dictionaryWord.setErrorText(rs.getString("errorText"));
            dictionaryWord.setCreated(rs.getTimestamp("created"));

            return dictionaryWord;
        }



}
