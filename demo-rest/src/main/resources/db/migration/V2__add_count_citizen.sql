ALTER TABLE cities
ADD COLUMN count_citizen INTEGER NULL
    CHECK (count_citizen > 0);