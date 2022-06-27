USE [KurirskaSluzba]
GO
/****** Object:  Trigger [dbo].[IsporukaTriger]    Script Date: 6/2/2022 2:25:57 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER TRIGGER [dbo].[IsporukaTriger]
   ON  [dbo].[Isporuka]
   for Insert,Update
AS 
BEGIN
	declare @kursor cursor

	declare @IdI integer,@IdAOd integer , @IdADo integer, @TipPaketa integer, @Tezina decimal(10,3)

	declare @OsnovnaCena decimal(10,3), @CenaPoKg decimal(10,3), @CenaIsporuke decimal(10,3)

	declare @EuklidskaDistanca decimal(10,3)

	declare @Xod integer, @Yod integer, @Xdo integer, @Ydo integer




	set @kursor= cursor for
	select IdI,IdAOd,IdADo,TipPaketa,Tezina
	from inserted


	open @kursor

	fetch from @kursor
	into
	@IdI,@IdAOd,@IdADo,@TipPaketa,@Tezina


	while @@FETCH_STATUS=0
	begin

	if(@TipPaketa=0) --mali paket
	begin
	set @OsnovnaCena=115

	set @CenaPoKg=0

	end

	if(@TipPaketa=1) --standardni paket
	begin
	set @OsnovnaCena=175

	set @CenaPoKg=100

	end

	if(@TipPaketa=2) --nestandardni paket
	begin
	set @OsnovnaCena=250

	set @CenaPoKg=100

	end

	if(@TipPaketa=3) --lomljiv paket
	begin
	set @OsnovnaCena=350

	set @CenaPoKg=500

	end

	select @Xod=X,@Yod=Y from Adresa
	where IdA=@IdAOd

	select @Xdo=X,@Ydo=Y from Adresa
	where IdA=@IdADo

	set @EuklidskaDistanca=SQRT(POWER(@Xod-@Xdo,2) + POWER(@Yod-@Ydo,2))

	set @CenaIsporuke=(@OsnovnaCena + @Tezina*@CenaPoKg) *  @EuklidskaDistanca

	if exists(select IdI from Ponuda where IdI=@IdI)
	begin
	update Ponuda
	set Cena=@CenaIsporuke
	where IdI=@IdI
	end
	else
	begin
	insert into Ponuda
	(IdI,Status,Cena)
	values(@IdI,0,@CenaIsporuke)
	end


	fetch from @kursor
	into
	@IdI,@IdAOd,@IdADo,@TipPaketa,@Tezina

	end

	close @kursor
	deallocate @kursor


END
