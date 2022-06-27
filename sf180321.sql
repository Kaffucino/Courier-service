DROP TABLE [Administrator]
go

DROP TABLE [Zahtev]
go

DROP TABLE [Vozio]
go

DROP TABLE [Ponuda]
go

DROP TABLE [Parkirana]
go

DROP TABLE [Magacin]
go

DROP TABLE [PrevoziSe]
go

DROP TABLE [Vozi]
go

DROP TABLE [Kurir]
go

DROP TABLE [TrenutnoUVozilu]
go

DROP TABLE [Isporuka]
go

DROP TABLE [Korisnik]
go

DROP TABLE [Adresa]
go

DROP TABLE [Grad]
go

DROP TABLE [Vozilo]
go

CREATE TABLE [Administrator]
( 
	[IdK]                integer  NOT NULL 
)
go

CREATE TABLE [Adresa]
( 
	[IdA]                integer  IDENTITY  NOT NULL ,
	[Ulica]              varchar(100)  NOT NULL ,
	[Broj]               integer  NOT NULL ,
	[X]                  integer  NOT NULL ,
	[Y]                  integer  NOT NULL ,
	[IdG]                integer  NOT NULL 
)
go

CREATE TABLE [Grad]
( 
	[IdG]                integer  IDENTITY  NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL ,
	[PostBr]             varchar(100)  NOT NULL 
)
go

CREATE TABLE [Isporuka]
( 
	[IdI]                integer  IDENTITY  NOT NULL ,
	[IdAOd]              integer  NOT NULL ,
	[IdADo]              integer  NOT NULL ,
	[IdK]                integer  NOT NULL ,
	[TipPaketa]          integer  NULL ,
	[Tezina]             decimal(10,3)  NULL ,
	[StatusPaketa]       integer  NULL ,
	[Cena]               decimal(10,3)  NULL ,
	[VremeKreiranja]     datetime  NULL ,
	[VremePrihvatanja]   datetime  NULL ,
	[Lokacija]           integer  NULL 
)
go

CREATE TABLE [Korisnik]
( 
	[IdK]                integer  IDENTITY  NOT NULL ,
	[Ime]                varchar(100)  NOT NULL ,
	[Prezime]            varchar(100)  NOT NULL ,
	[Sifra]              varchar(100)  NOT NULL ,
	[IdA]                integer  NOT NULL ,
	[KorIme]             varchar(100)  NULL 
)
go

CREATE TABLE [Kurir]
( 
	[IdK]                integer  NOT NULL ,
	[BrIsporPaketa]      integer  NULL ,
	[Status]             integer  NULL ,
	[Profit]             decimal(10,3)  NULL ,
	[VozackaDozvola]     varchar(9)  NULL 
)
go

CREATE TABLE [Magacin]
( 
	[IdM]                integer  IDENTITY  NOT NULL ,
	[IdA]                integer  NOT NULL 
)
go

CREATE TABLE [Parkirana]
( 
	[IdM]                integer  NOT NULL ,
	[IdVoz]              integer  NOT NULL 
)
go

CREATE TABLE [Ponuda]
( 
	[Status]             integer  NULL ,
	[IdI]                integer  NOT NULL ,
	[Cena]               decimal(10,3)  NULL 
)
go

CREATE TABLE [PrevoziSe]
( 
	[IdPr]               integer  IDENTITY  NOT NULL ,
	[StatusPak]          integer  NOT NULL ,
	[IdI]                integer  NOT NULL ,
	[IdK]                integer  NOT NULL ,
	[IdVoz]              integer  NOT NULL 
)
go

CREATE TABLE [TrenutnoUVozilu]
( 
	[IdVoz]              integer  NOT NULL ,
	[IdI]                integer  NOT NULL 
)
go

CREATE TABLE [Vozi]
( 
	[IdK]                integer  NOT NULL ,
	[IdVoz]              integer  NOT NULL ,
	[PredjeniPut]        decimal(10,3)  NULL ,
	[TrenutnaLokacija]   integer  NULL 
)
go

CREATE TABLE [Vozilo]
( 
	[IdVoz]              integer  IDENTITY  NOT NULL ,
	[TipGoriva]          integer  NULL ,
	[Potrosnja]          decimal(10,2)  NOT NULL ,
	[Nosivost]           decimal(10,2)  NOT NULL ,
	[RegBR]              varchar(10)  NULL 
)
go

CREATE TABLE [Vozio]
( 
	[IdVoz]              integer  NOT NULL ,
	[IdK]                integer  NOT NULL 
)
go

CREATE TABLE [Zahtev]
( 
	[IdZ]                integer  NOT NULL ,
	[VozackaDozvola]     varchar(9)  NOT NULL 
)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([IdK] ASC)
go

ALTER TABLE [Adresa]
	ADD CONSTRAINT [XPKAdresa] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([IdG] ASC)
go

ALTER TABLE [Isporuka]
	ADD CONSTRAINT [XPKIsporuka] PRIMARY KEY  CLUSTERED ([IdI] ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([IdK] ASC)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([IdK] ASC)
go

ALTER TABLE [Magacin]
	ADD CONSTRAINT [XPKMagacin] PRIMARY KEY  CLUSTERED ([IdM] ASC)
go

ALTER TABLE [Parkirana]
	ADD CONSTRAINT [XPKParkirana] PRIMARY KEY  CLUSTERED ([IdM] ASC,[IdVoz] ASC)
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [XPKPonuda] PRIMARY KEY  CLUSTERED ([IdI] ASC)
go

ALTER TABLE [PrevoziSe]
	ADD CONSTRAINT [XPKPrevoziSe] PRIMARY KEY  CLUSTERED ([IdPr] ASC,[StatusPak] ASC)
go

ALTER TABLE [TrenutnoUVozilu]
	ADD CONSTRAINT [XPKTrenutniUVozilu] PRIMARY KEY  CLUSTERED ([IdVoz] ASC,[IdI] ASC)
go

ALTER TABLE [Vozi]
	ADD CONSTRAINT [XPKVozi] PRIMARY KEY  CLUSTERED ([IdK] ASC,[IdVoz] ASC)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([IdVoz] ASC)
go

ALTER TABLE [Vozio]
	ADD CONSTRAINT [XPKVozio] PRIMARY KEY  CLUSTERED ([IdVoz] ASC,[IdK] ASC)
go

ALTER TABLE [Zahtev]
	ADD CONSTRAINT [XPKZahtev] PRIMARY KEY  CLUSTERED ([IdZ] ASC)
go


ALTER TABLE [Administrator]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdK]) REFERENCES [Korisnik]([IdK])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Adresa]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdG]) REFERENCES [Grad]([IdG])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Isporuka]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IdAOd]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Isporuka]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([IdADo]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Isporuka]
	ADD CONSTRAINT [R_21] FOREIGN KEY ([IdK]) REFERENCES [Korisnik]([IdK])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Isporuka]
	ADD CONSTRAINT [R_24] FOREIGN KEY ([Lokacija]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Korisnik]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdA]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdK]) REFERENCES [Korisnik]([IdK])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Magacin]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdA]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Parkirana]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([IdM]) REFERENCES [Magacin]([IdM])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Parkirana]
	ADD CONSTRAINT [R_26] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_23] FOREIGN KEY ([IdI]) REFERENCES [Isporuka]([IdI])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [PrevoziSe]
	ADD CONSTRAINT [R_36] FOREIGN KEY ([IdI]) REFERENCES [Isporuka]([IdI])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [PrevoziSe]
	ADD CONSTRAINT [R_37] FOREIGN KEY ([IdK],[IdVoz]) REFERENCES [Vozi]([IdK],[IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TrenutnoUVozilu]
	ADD CONSTRAINT [R_38] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [TrenutnoUVozilu]
	ADD CONSTRAINT [R_39] FOREIGN KEY ([IdI]) REFERENCES [Isporuka]([IdI])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Vozi]
	ADD CONSTRAINT [R_32] FOREIGN KEY ([IdK]) REFERENCES [Kurir]([IdK])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Vozi]
	ADD CONSTRAINT [R_33] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Vozi]
	ADD CONSTRAINT [R_35] FOREIGN KEY ([TrenutnaLokacija]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Vozio]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Vozio]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([IdK]) REFERENCES [Kurir]([IdK])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Zahtev]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdZ]) REFERENCES [Korisnik]([IdK])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
