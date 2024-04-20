DROP DATABASE IF EXISTS picasso;

CREATE DATABASE picasso;

USE picasso;

CREATE TABLE player
(
    player_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'user ID, unique identifer',
    uname varchar(255) NOT NULL comment 'user name, should be unique as well ',
    password varchar(128) NOT NULL comment 'encrypted password',
    is_deleted tinyINT(1) DEFAULT 0 NOT NULL comment 'logic delete bit'
);

ALTER TABLE player ADD CONSTRAINT player_uk UNIQUE (uname);

CREATE TABLE word
(
	word_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'unique identifier for a scene',
    word varchar(1024) NOT NULL comment 'a short scentence (100 words or less) describing a scene to draw'
-- time_limit INT DEFAULT 180 NOT NULL comment 'the time limit to draw that word(second)',  -- move to game
-- is_deleted tinyINT(1) DEFAULT 0 NOT NULL comment 'logic deletion bit'                   -- probably not needed initially
)
    comment 'the word bank to draw';

CREATE TABLE game
(
game_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'unique identifier for a game',
word_id INT NOT NULL comment 'the scene for this game',
mode CHAR(1) NOT NULL DEFAULT 'S' comment 'game mode: S: Single, M: Multi-player via waiting room, C: 2 players via challenge/invite',
status CHAR(1) NOT NULL DEFAULT 'N' comment 'game status: N: New, A: Active, D: Done',
time_limit INT NOT NULL DEFAULT 180 comment 'time limit in seconds',
creator_id INT NOT NULL comment 'the player id that created this game',
capacity INT NOT NULL DEFAULT 1 comment 'number of players participated in this game',
created TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment 'datetime this game was created',
started TIMESTAMP comment 'datetime this game started (when moved to Active)',
ended TIMESTAMP  comment 'datetime this game ended'
);

ALTER TABLE game ADD CONSTRAINT game_word_fk FOREIGN KEY (word_id) REFERENCES word (word_id);
ALTER TABLE game ADD CONSTRAINT game_player_fk FOREIGN KEY (creator_id) REFERENCES player (player_id);
-- add check constrain on mode and status values to limit choices
ALTER TABLE game ADD CONSTRAINT game_mode_check CHECK ( mode IN ('S', 'M'));

ALTER TABLE game ADD CONSTRAINT game_status_check CHECK (status IN ('N', 'A', 'D'));
-- the game table setup allows a player to attempt the same word multiple times
-- mode and status are CHAR(1) instead of a bit to add more flexibility


CREATE TABLE picture
(
picture_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'unique identifier for a picture',
game_id INT NOT NULL comment 'a FK reference to game_id',
player_id INT NOT NULL comment 'a FK reference to player_id',
score INT NULL comment 'a score between 0 and 100 given by GPT judge',
remark VARCHAR(4096) NULL comment 'GPT remark on the paintaing, capped to 4096 chars',
title VARCHAR(1024) NULL comment 'an optional title for the art piece'
-- changed comment to remark to distinguish it form SQL comment keyword
-- and capped it to 4096
);

ALTER TABLE picture ADD CONSTRAINT picture_uk UNIQUE (game_id, player_id);
ALTER TABLE picture ADD CONSTRAINT picture_player_fk FOREIGN KEY (player_id) REFERENCES player (player_id);

ALTER TABLE picture ADD CONSTRAINT picture_game_fk FOREIGN KEY (game_id) REFERENCES game (game_id);
-- add check constraint on score to ensure it's within limit
-- note that check constraints do not fail when the value is null
ALTER TABLE picture ADD CONSTRAINT score_check CHECK ( score >= 0 AND score <= 100);
-- game player setup allows for any number of players to join the same game


CREATE TABLE line
(
    line_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'unique identifier of that line. a collection of lines make up a picture',
    picture_id INT NOT NULL comment 'FK reference to piture_id',
    pen_size INT NOT NULL comment 'pen size ',
    color_r INT NOT NULL comment 'red',
    color_g INT NOT NULL comment 'green',
    color_b INT NOT NULL comment 'blue',
    timestamp timestamp NOT NULL comment 'timestamp when drawing that line',
    is_eraser tinyINT(1) NOT NULL comment 'whether that line is in eraser mode',
    pre_x INT NOT NULL comment 'previous coordinate of X',
    pre_y INT NOT NULL comment 'previous coordinate of y',
    x INT NOT NULL comment 'current coordinate of x',
    y INT NOT NULL comment 'current coordinate of Y',
    is_deleted tinyINT(1) DEFAULT 0 NOT NULL comment 'logical deletion flag of that record, when delete the record, instead of delete that record, we set that bit to true'
);

ALTER TABLE line ADD CONSTRAINT line_picture_fk FOREIGN KEY (picture_id) REFERENCES picture (picture_id);
