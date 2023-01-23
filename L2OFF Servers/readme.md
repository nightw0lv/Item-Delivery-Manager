Enable item delivery

- Open port 1433
- that's it.

##### MsSQL Installation
* Execute Query:

        USE [lin2world]
            GO

        IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[user_item_delivery]') AND type IN ('U'))
        	DROP TABLE [dbo].[user_item_delivery]
        	GO
        SET ANSI_NULLS ON
        	GO
        SET QUOTED_IDENTIFIER ON
        	GO
        SET ANSI_PADDING ON
        	GO
        
        CREATE TABLE [dbo].[user_item_delivery]
        (
        	[id] [int]  IDENTITY(1,1) NOT NULL,
        	[item_id] [int]  NOT NULL,
        	[item_count] [int]  NOT NULL,
        	[char_name] [nvarchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
        	[status] [int] DEFAULT 0 NOT NULL,
        	[reason] [nvarchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL
        	PRIMARY KEY CLUSTERED
        	(
        		[id] ASC
        	)	WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
        )	ON [PRIMARY]
        	GO
        
        SET ANSI_PADDING OFF
        	GO
        ALTER TABLE [dbo].[user_item_delivery] SET (LOCK_ESCALATION = TABLE)
        	GO
        	
        	
        IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[donations]') AND type IN ('U'))
        	DROP TABLE [dbo].[donations]
        	GO
        SET ANSI_NULLS ON
        	GO
        SET QUOTED_IDENTIFIER ON
        	GO
        SET ANSI_PADDING ON
        	GO
        
        CREATE TABLE [dbo].[donations]
        (
            [id] [int] IDENTITY(1,1) NOT NULL,
            [method] [varchar](50) NOT NULL,
            [transaction_id] [nvarchar](50) NOT NULL,
            [amount] [nvarchar](50) NOT NULL,
            [character_name] [nvarchar](50) NOT NULL,
            [date_time] [datetime] NULL,
        	PRIMARY KEY CLUSTERED
        	(
        		[id] ASC
        	)	WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
        )	ON [PRIMARY]
        	GO
        
        SET ANSI_PADDING OFF
            GO
        ALTER TABLE [dbo].[donations] SET (LOCK_ESCALATION = TABLE)
        	GO
