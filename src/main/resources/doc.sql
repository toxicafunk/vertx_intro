CREATE TABLE docs
(
    id     INT GENERATED ALWAYS AS IDENTITY,
    mdname varchar(40) NOT NULL,
    typed  varchar(250)
);
