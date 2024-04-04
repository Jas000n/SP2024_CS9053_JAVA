create table `Lines`
(
    LID        int                  not null comment 'unique identifier of that line'
        primary key,
    PID        int                  not null comment 'which picture this line belong',
    pen_size   int                  not null comment 'pen size ',
    color_R    int                  not null comment 'rgb of R',
    color_G    int                  not null comment 'rgb of G',
    color_B    int                  not null comment 'RGB_B',
    timestamp  timestamp            not null comment 'timestamp when drawing that line',
    is_eraser  tinyint(1)           not null comment 'whether that line is in eraser mode',
    preX       int                  not null comment 'previous coordinate of X',
    preY       int                  not null comment 'previous coordinate of y',
    X          int                  not null comment 'current coordinate of x',
    Y          int                  not null comment 'current coordinate of Y',
    is_deleted tinyint(1) default 0 not null comment 'logical deletion flag of that record, when delete the record, instead of delete that record, we set that bit to true'
);

create table PIcture
(
    PID        int                  not null comment 'unique identifier'
        primary key,
    score      int                  null comment 'score given by judge, can be ChatGPT or other player',
    comment    text                 null comment 'comment given by ChatGPT or other players',
    authorID   int                  not null comment 'id of the author drawing this picture',
    word       text                 not null comment 'the word that the author is drawing',
    is_deleted tinyint(1) default 0 not null comment 'logic delete bit'
);

create table Player
(
    UID        int                  not null comment 'user ID, unique identifer',
    Uname      varchar(255)         not null comment 'user name, should be unique as well ',
    password   char(32)             not null comment 'encrypted password',
    is_deleted tinyint(1) default 0 not null comment 'logic delete bit',
    constraint Player_pk
        unique (UID),
    constraint Player_unique
        unique (Uname)
);

create table Word
(
    word        varchar(255)           not null comment 'the word to draw',
    description varchar(1024)          not null comment 'the description of the word',
    time_limit  int        default 180 not null comment 'the time limit to draw that word(second)',
    is_deleted  tinyint(1) default 0   not null comment 'logic deletion bit',
    constraint Word_pk
        unique (word)
)
    comment 'the word bank to draw';

