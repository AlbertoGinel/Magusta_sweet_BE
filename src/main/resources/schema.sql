DROP TABLE IF EXISTS DictionaryWord;

CREATE TABLE DictionaryWord (
    id UUID PRIMARY KEY,
    estonian VARCHAR(255) UNIQUE,
    type VARCHAR(255) CHECK (type IN ('noun', 'verb', 'adjective', 'adverb', 'pronoun', 'preposition', 'conjunction', 'interjection')),
    case_D VARCHAR(255) CHECK (case_D IN ('nominative', 'genitive', 'partitive', 'accusative', 'dative', 'ablative', 'allative', 'elative', 'inessive', 'adessive', 'translative', 'essive', 'abessive', 'comitative') OR case_D IS NULL),
    number VARCHAR(255),
    basic_form VARCHAR(255),
    tense VARCHAR(255),
    person VARCHAR(255),
    degree VARCHAR(255),
    parts TEXT,
    level_D INTEGER CHECK (level_D >= 1 AND level_D <= 10),
    english_translation VARCHAR(255),
    basicWordId UUID,
    state VARCHAR(255) CHECK (state IN ('empty', 'error', 'ok')),
    errorText VARCHAR(255),
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE DictionaryWord
    ADD CONSTRAINT fk_basicWordId FOREIGN KEY (basicWordId) REFERENCES DictionaryWord(id) ON DELETE SET NULL;

DROP TABLE IF EXISTS PersonalWord;

CREATE TABLE PersonalWord (
    id UUID PRIMARY KEY,
    userId UUID NOT NULL,
    dictionaryWordId UUID NOT NULL,
    lastAppearance TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    estonian VARCHAR(255) NOT NULL,
    halo NUMERIC NOT NULL,
    level_D INT NOT NULL,
    state VARCHAR(255) CHECK (state IN ('MASTERED', 'RESERVED', 'LIVE')),

    -- Composite unique constraint on (userId, estonian)
    UNIQUE (userId, estonian),

    -- Foreign key with cascade delete option
    CONSTRAINT fk_dictionaryWord FOREIGN KEY (dictionaryWordId) REFERENCES DictionaryWord(id) ON DELETE CASCADE
);


