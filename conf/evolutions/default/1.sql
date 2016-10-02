# --- !Ups

CREATE TABLE USERS (
  NAME varchar(255) NOT NULL,
  TEAM_NAME varchar(255) NOT NULL,
  ROLE varchar(10) NOT NULL,
  BUDGET integer NOT NULL,
  PRIMARY KEY (NAME)
);

CREATE TABLE PLAYERS (
  NAME varchar(255) NOT NULL,
  ROLE char(1) NOT NULL,
  VALUE integer NOT NULL,
  TEAM varchar(255) NOT NULL,
  USER varchar(255),
  USER_TEAM varchar(255),
  PRIMARY KEY (NAME)
);

CREATE TABLE EVENTS (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  PAYLOAD VARCHAR(4096) NOT NULL,
  SAVE_POINT smallint NOT NULL,
  PRIMARY KEY (ID)
);


INSERT INTO USERS VALUES ('max', 'Atl. Tricheco', 'admin', 100);
INSERT INTO USERS VALUES ('gino', 'Seagallo F.C.', 'bidder', 100);


INSERT INTO PLAYERS VALUES ('Abate Ignazio','D', 0, 'Milan', null, null);
INSERT INTO PLAYERS VALUES ('Acatullo Marco', 'A', 0, 'Pescara', null, null);

# --- !Downs

DROP TABLE USERS;
DROP TABLE PLAYERS;
DROP TABLE EVENTS;
