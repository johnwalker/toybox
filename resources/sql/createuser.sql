create table useraccount (
       userid serial,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       PRIMARY KEY (userid)
);
