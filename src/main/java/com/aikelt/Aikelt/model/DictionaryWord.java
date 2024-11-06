package com.aikelt.Aikelt.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "dictionaryword")
public class DictionaryWord {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "estonian", unique = true, nullable = false)
    private String estonian;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "case_D")
    private String case_D;

    @Column(name = "number")
    private String number;

    @Column(name = "basic_form")
    private String basicForm;

    @Column(name = "tense")
    private String tense;

    @Column(name = "person")
    private String person;

    @Column(name = "degree")
    private String degree;

    @Column(name = "parts")
    private String parts;  // Storing parts as a comma-separated string (TEXT)

    @Column(name = "level_D", nullable = false)
    private Integer level_D;

    @Column(name = "english_translation")
    private String englishTranslation;

    @Column(name = "basicWordId")
    private UUID  basicWordId;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "errorText")
    private String errorText;

    @Column(name = "created", nullable = false)
    private Timestamp created;

    private static final Set<String> ALLOWED_CASES = Set.of("nominative", "genitive", "partitive", "accusative", "dative", "ablative", "allative", "elative", "inessive", "adessive", "translative", "essive", "abessive", "comitative");

    // Constructors, Getters, and Setters
    public DictionaryWord() {
        this.created = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters for each field
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEstonian() {
        return estonian;
    }

    public void setEstonian(String estonian) {
        this.estonian = estonian;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCase_D() {
        return case_D;
    }

    public void setCase_D(String case_D) {
        if (case_D != null && ALLOWED_CASES.contains(case_D.toLowerCase())) {
            this.case_D = case_D;
        } else {
            this.case_D = null; // Set to null if not allowed
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBasicForm() {
        return basicForm;
    }

    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public String getTense() {
        return tense;
    }

    public void setTense(String tense) {
        this.tense = tense;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public Integer getLevel_D() {
        return level_D;
    }

    public void setLevel_D(Integer level_D) {
        this.level_D = level_D;
    }

    public String getEnglishTranslation() {
        return englishTranslation;
    }

    public void setEnglishTranslation(String englishTranslation) {
        this.englishTranslation = englishTranslation;
    }

    public UUID getBasicWordId() {
        return basicWordId;
    }

    public void setBasicWordId(UUID basicWordId) {
        this.basicWordId = basicWordId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public DictionaryWord(String estonian, String number, Integer level_D, String person,
                          String degree, String parts, String englishTranslation,
                          String type, String tense, String case_D, String basicForm) {

        this.estonian = estonian;
        this.number = number;
        this.level_D = level_D;
        this.person = person;
        this.degree = degree;
        this.parts = parts;  // Convert list to a comma-separated string
        this.englishTranslation = englishTranslation;
        this.type = type;
        this.tense = tense;
        this.case_D = case_D;
        this.basicForm = basicForm;
        this.created = new Timestamp(System.currentTimeMillis());
        this.state = "ok";  // Assuming a default state, can be changed as needed
        this.errorText = null;  // Default to null, can be modified later
    }

    public String toString() {
        return "ToString DictionaryWord{" +
                "id=" + id +
                ", estonian='" + estonian + '\'' +
                ", type='" + type + '\'' +
                ", case_D='" + case_D + '\'' +
                ", number='" + number + '\'' +
                ", basicForm='" + basicForm + '\'' +
                ", tense='" + tense + '\'' +
                ", person='" + person + '\'' +
                ", degree='" + degree + '\'' +
                ", parts='" + parts + '\'' +
                ", level_D=" + level_D +
                ", englishTranslation='" + englishTranslation + '\'' +
                ", basicWordId=" + basicWordId +
                ", state='" + state + '\'' +
                ", errorText='" + errorText + '\'' +
                ", created=" + created +
                '}';
    }
}