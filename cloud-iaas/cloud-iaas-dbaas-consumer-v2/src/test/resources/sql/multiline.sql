CREATE TABLE public.agedepart (
    id integer NOT NULL,
    ageretr varchar(8) NOT NULL,
    display varchar(16) NOT NULL,
    debut date NOT NULL,
    fin date NOT NULL,
    anticipe boolean NOT NULL,
    lag62 varchar(8) NOT NULL,
    lag64 varchar(8) NOT NULL,
    lag67 varchar(8) NOT NULL
) WITHOUT OIDS;