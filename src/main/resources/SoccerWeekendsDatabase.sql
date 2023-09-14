USE [master]
GO
/****** Object:  Database [SoccerWeekends]    Script Date: 14.09.2023 15:29:54 ******/
CREATE DATABASE [SoccerWeekends]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'SoccerWeekends', FILENAME = N'D:\Programs\MSSQL15.SQLEXPRESS\MSSQL\DATA\SoccerWeekends.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'SoccerWeekends_log', FILENAME = N'D:\Programs\MSSQL15.SQLEXPRESS\MSSQL\DATA\SoccerWeekends_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO
ALTER DATABASE [SoccerWeekends] SET COMPATIBILITY_LEVEL = 150
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SoccerWeekends].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SoccerWeekends] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [SoccerWeekends] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [SoccerWeekends] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [SoccerWeekends] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [SoccerWeekends] SET ARITHABORT OFF 
GO
ALTER DATABASE [SoccerWeekends] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [SoccerWeekends] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [SoccerWeekends] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [SoccerWeekends] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [SoccerWeekends] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [SoccerWeekends] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [SoccerWeekends] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [SoccerWeekends] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [SoccerWeekends] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [SoccerWeekends] SET  DISABLE_BROKER 
GO
ALTER DATABASE [SoccerWeekends] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [SoccerWeekends] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [SoccerWeekends] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [SoccerWeekends] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [SoccerWeekends] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [SoccerWeekends] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [SoccerWeekends] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [SoccerWeekends] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [SoccerWeekends] SET  MULTI_USER 
GO
ALTER DATABASE [SoccerWeekends] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [SoccerWeekends] SET DB_CHAINING OFF 
GO
ALTER DATABASE [SoccerWeekends] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [SoccerWeekends] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [SoccerWeekends] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [SoccerWeekends] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [SoccerWeekends] SET QUERY_STORE = OFF
GO
USE [SoccerWeekends]
GO
/****** Object:  User [TestUser]    Script Date: 14.09.2023 15:29:54 ******/
CREATE USER [TestUser] FOR LOGIN [Test] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [TestUser]
GO
/****** Object:  UserDefinedTableType [dbo].[ID_TABLE]    Script Date: 14.09.2023 15:29:54 ******/
CREATE TYPE [dbo].[ID_TABLE] AS TABLE(
	[id] [int] NOT NULL
)
GO
/****** Object:  UserDefinedFunction [dbo].[GetNumberOfOpponentGames]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetNumberOfOpponentGames] (@opponent_id INT)
	RETURNS INT
BEGIN
	DECLARE @number_of_opponent_games INT

	SELECT @number_of_opponent_games = COUNT(query_in.id_game)
	FROM (SELECT id_game
			FROM game_result gr
			WHERE id_opponent = @opponent_id
			GROUP BY gr.id_game) AS query_in

	RETURN @number_of_opponent_games
END;
GO
/****** Object:  UserDefinedFunction [dbo].[GetNumberOfOpponentWins]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetNumberOfOpponentWins] (@id_opponent INT)
	RETURNS INT
	BEGIN
		DECLARE @number_of_opponent_wins INT = 0 -- Количество побед

		-- Курсор для поиска игр игрока
		DECLARE @id_game INT -- Код игры
		DECLARE opponent_games_cursor CURSOR LOCAL FOR
			SELECT id_game
			FROM game_result
			WHERE id_opponent = @id_opponent
			GROUP BY id_game;

		
		-- Открываем курсор и достаём коды игр
		OPEN opponent_games_cursor
		FETCH NEXT FROM opponent_games_cursor INTO
			@id_game
		WHILE @@FETCH_STATUS = 0
		BEGIN
			DECLARE @id_opponent_winner INT; -- Код победителя
			-- Курсор для определения, кто победитель в игре
			DECLARE opponent_winner_cursor CURSOR LOCAL FOR
				SELECT id_opponent
				FROM game_result
				WHERE id_game = @id_game
					AND score = (SELECT MAX(score)
								 FROM game_result
								 WHERE id_game = @id_game);

			OPEN opponent_winner_cursor
			FETCH NEXT FROM opponent_winner_cursor INTO
				@id_opponent_winner
			WHILE @@FETCH_STATUS = 0
			BEGIN
				IF @id_opponent_winner = @id_opponent -- Если код победителя совпадает с входящим кодом, прибавляем победу
				BEGIN
					SET @number_of_opponent_wins = @number_of_opponent_wins + 1
					BREAK
				END

				FETCH NEXT FROM opponent_winner_cursor INTO
					@id_opponent_winner
			END
			CLOSE opponent_winner_cursor
			DEALLOCATE opponent_winner_cursor

			FETCH NEXT FROM opponent_games_cursor INTO
				@id_game
		END
		CLOSE opponent_games_cursor
		DEALLOCATE opponent_games_cursor

		RETURN @number_of_opponent_wins
	END;
GO
/****** Object:  UserDefinedFunction [dbo].[GetNumberOfPlayersPlayingTheGame]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GetNumberOfPlayersPlayingTheGame](@id_game INT)
	RETURNS INT
BEGIN
		DECLARE @numberOfPlayers INT

		SELECT @numberOfPlayers = COUNT(*)
		FROM game_result gr
		WHERE gr.id_game = @id_game

		RETURN @numberOfPlayers
END;
GO
/****** Object:  Table [dbo].[game]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[game](
	[id_game] [int] IDENTITY(1,1) NOT NULL,
	[id_game_type] [int] NOT NULL,
	[game_date] [date] NOT NULL,
 CONSTRAINT [PK_game] PRIMARY KEY CLUSTERED 
(
	[id_game] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[game_result]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[game_result](
	[id_game_result] [int] IDENTITY(1,1) NOT NULL,
	[id_game] [int] NOT NULL,
	[id_opponent] [int] NOT NULL,
	[score] [int] NOT NULL,
 CONSTRAINT [PK_game_result] PRIMARY KEY CLUSTERED 
(
	[id_game_result] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[game_type]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[game_type](
	[id_game_type] [int] IDENTITY(1,1) NOT NULL,
	[title] [nvarchar](max) NOT NULL,
	[image_name] [nvarchar](max) NULL,
	[info] [nvarchar](max) NULL,
 CONSTRAINT [PK_game_type] PRIMARY KEY CLUSTERED 
(
	[id_game_type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[opponent]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[opponent](
	[id_opponent] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](max) NOT NULL,
	[image_name] [nvarchar](max) NULL,
 CONSTRAINT [PK_opponent] PRIMARY KEY CLUSTERED 
(
	[id_opponent] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[game]  WITH CHECK ADD  CONSTRAINT [FK_game_game_type] FOREIGN KEY([id_game_type])
REFERENCES [dbo].[game_type] ([id_game_type])
GO
ALTER TABLE [dbo].[game] CHECK CONSTRAINT [FK_game_game_type]
GO
ALTER TABLE [dbo].[game_result]  WITH CHECK ADD  CONSTRAINT [FK_game_result_game] FOREIGN KEY([id_game])
REFERENCES [dbo].[game] ([id_game])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[game_result] CHECK CONSTRAINT [FK_game_result_game]
GO
ALTER TABLE [dbo].[game_result]  WITH CHECK ADD  CONSTRAINT [FK_game_result_opponent4] FOREIGN KEY([id_opponent])
REFERENCES [dbo].[opponent] ([id_opponent])
GO
ALTER TABLE [dbo].[game_result] CHECK CONSTRAINT [FK_game_result_opponent4]
GO
/****** Object:  StoredProcedure [dbo].[GetGameRating]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetGameRating]
	@id_game INT
AS
BEGIN
	SELECT * 
	FROM game_result
	WHERE id_game = @id_game
	ORDER BY score DESC
END;
GO
/****** Object:  StoredProcedure [dbo].[GetGamesBetweenOpponents]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetGamesBetweenOpponents]
	@id_opponents_table ID_TABLE readonly
AS
BEGIN

	-- Ищем все игры, в которых участвовал любой из участников
	DECLARE @id_game INT -- Код игры
	DECLARE id_games_with_opponents_cursor CURSOR LOCAL FOR
		SELECT g.id_game
		FROM game g
		WHERE g.id_game IN (SELECT gr.id_game
							FROM game_result gr
							WHERE gr.id_opponent IN (SELECT id FROM @id_opponents_table)
							GROUP BY gr.id_game)

	-- Сохраняем количество игроков, которых ищем
	DECLARE @number_of_opponents_find INT
	SET @number_of_opponents_find = (SELECT COUNT(*) FROM (SELECT id -- Убираем возможные дубликаты
														  FROM @id_opponents_table 
														  GROUP BY id) as query_in)
	-- Создаем результирующую таблицу
	DECLARE @id_games_result ID_TABLE

	-- Запускаем курсор
	OPEN id_games_with_opponents_cursor
	FETCH NEXT FROM id_games_with_opponents_cursor INTO
		@id_game
	WHILE @@FETCH_STATUS = 0
	BEGIN
		-- Ищем общее количество участников в игре
		DECLARE @number_of_opponents_in_current_game INT
		SET @number_of_opponents_in_current_game = (SELECT COUNT(*) 
										 FROM (SELECT id_opponent
												FROM game_result
												WHERE id_game = @id_game
												AND id_opponent IN (SELECT id 
																	FROM @id_opponents_table)
												GROUP BY id_opponent) as query_in)

		-- Если общее количество участников в игре равно количеству, переданному в процедуру - добавляем
		IF (@number_of_opponents_find = @number_of_opponents_in_current_game)
		BEGIN
			INSERT INTO @id_games_result
				VALUES (@id_game)
		END

		FETCH NEXT FROM id_games_with_opponents_cursor INTO
			@id_game
	END
	CLOSE id_games_with_opponents_cursor
	DEALLOCATE id_games_with_opponents_cursor

	SELECT *
	FROM game
	WHERE id_game IN (SELECT id
					  FROM @id_games_result)
END;
GO
/****** Object:  StoredProcedure [dbo].[GetOpponentStats]    Script Date: 14.09.2023 15:29:54 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetOpponentStats]
	@id_opponent INT
AS
BEGIN
	DECLARE @number_of_games INT = 0
	DECLARE @number_of_wins INT = 0
	DECLARE @number_of_draws INT = 0
	DECLARE @number_of_loses INT = 0

	DECLARE @number_of_opponent_wins INT = 0 -- Количество побед

		-- Курсор для поиска игр игрока
		DECLARE @id_game INT -- Код игры
		DECLARE opponent_games_cursor CURSOR LOCAL FOR
			SELECT id_game
			FROM game_result
			WHERE id_opponent = @id_opponent
			GROUP BY id_game;
		
		-- Открываем курсор и достаём коды игр
		OPEN opponent_games_cursor
		FETCH NEXT FROM opponent_games_cursor INTO
			@id_game
		WHILE @@FETCH_STATUS = 0
		BEGIN
			-- Добавляем игру
			SET @number_of_games = @number_of_games + 1;

			-- Сохраняем всех победителей в таблицу
			DECLARE @game_winners_table TABLE (
				id_opponent INT
				);

			INSERT INTO @game_winners_table
			SELECT id_opponent
			FROM game_result
			WHERE id_game = @id_game
				AND score = (SELECT MAX(score)
								FROM game_result
								WHERE id_game = @id_game);

			-- Проверяем, есть ли в победителях искомый оппонент
			IF ((SELECT COUNT(*)
				FROM @game_winners_table
				WHERE id_opponent = @id_opponent) > 0)
				BEGIN
					-- Проверяем, сколько победителей в игре
					IF ((SELECT COUNT(*)
						FROM @game_winners_table) = 1)
						-- Если только искомый оппонент - это победа
						SET @number_of_wins = @number_of_wins + 1;
					ELSE
						-- Если несколько оппонентов - это ничья
						SET @number_of_draws = @number_of_draws + 1;
				END
			ELSE
				-- Если среди победителей нет искомого оппонента - это поражение
				SET @number_of_loses = @number_of_loses + 1;

			DELETE FROM @game_winners_table;
			FETCH NEXT FROM opponent_games_cursor INTO
				@id_game
		END
		CLOSE opponent_games_cursor
		DEALLOCATE opponent_games_cursor

		-- Возвращаем результат единой таблицей
		SELECT @number_of_games AS "Количество игр", 
			@number_of_wins AS "Количество побед",
			@number_of_draws AS "Количество ничьих",
			@number_of_loses AS "Количество поражений"
END;
GO
USE [master]
GO
ALTER DATABASE [SoccerWeekends] SET  READ_WRITE 
GO
