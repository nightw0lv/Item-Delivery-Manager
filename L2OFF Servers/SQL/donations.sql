/*
 Navicat Premium Data Transfer

 Source Server         : Titan
 Source Server Type    : SQL Server
 Source Server Version : 10501600
 Source Host           : Titan:1433
 Source Catalog        : lin2world
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 10501600
 File Encoding         : 65001

 Date: 23/01/2023 03:13:40
*/

USE [lin2world]
    GO

-- ----------------------------
-- Table structure for user_item_delivery
-- ----------------------------
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