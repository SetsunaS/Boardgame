1. Login into postgreSQL in the shell (for example : `psql -U postgre`)
2. `CREATE DATABASE boargame`
3.
```SQL
CREATE TABLE player(
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(60) NOT NULL,
    salted_password VARCHAR(29) NOT NULL
);
```
4.
```SQL
CREATE TABLE score(
    id SERIAL PRIMARY KEY,
    score INT NOT NULL,
    player_id INT NOT NULL REFERENCES player(id) ON DELETE CASCADE
);
```