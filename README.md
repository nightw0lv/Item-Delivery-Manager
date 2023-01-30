# Item Delivery Manager
_L2Java Delivery Manager for [DenArt Designs](https://shop.denart-designs.com/?utm_source=github "DenArt-Designs Shop") donate, vote, referral and acp panels._



#
### Requirements
* **L2JAVA:** Pack sources (Except Lucera and L2jEternity)
* **L2OFF:** Supported Advext and Vanganth packs
#
### Documentation
* https://shop.denart-designs.com/documentation/

#
### Installation Guide
##### Lucera
* You need to place the ```Delivery.ext.jar``` file inside your libs folder
* You can download this project and open with intellij to build new ext

##### Other Packs Eclipse
* Copy ItemDeliveryManager.java inside eclipse in some root folder like net.sf.l2j.gameserver
* Add in gameserver.java the following (or where you placed ItemDeliveryManager.java):
        
        import extensions.ItemDeliveryManager;

* Add in gameserver.java add after task managers:

        ItemDeliveryManager.getInstance();
        
##### MySQL Installation
* Execute Query:

        SET NAMES utf8;
        SET FOREIGN_KEY_CHECKS = 0;
    
        -- ----------------------------
        -- Table structure for user_item_delivery
        -- ----------------------------
        DROP TABLE IF EXISTS `user_item_delivery`;
        CREATE TABLE `user_item_delivery`  (
          `id` int NOT NULL AUTO_INCREMENT,
          `item_id` int NOT NULL,
          `item_count` int NOT NULL,
          `char_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
          `status` int NOT NULL DEFAULT 0,
          `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
          PRIMARY KEY (`id`) USING BTREE
        ) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
        
        -- ----------------------------
        -- Table structure for donations
        -- ----------------------------
        DROP TABLE IF EXISTS `donations`;
        CREATE TABLE `donations`  (
          `id` int(11) NOT NULL AUTO_INCREMENT,
          `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
          `transaction_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
          `amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
          `character_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
          `date_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
          PRIMARY KEY (`id`)
        ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
        
        SET FOREIGN_KEY_CHECKS = 1;
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

##### Database User Creation

* You need to **create a new database user** and password for more security (**don't use root**)
* Access restricted to your **web host** IP Address (ask your host provider for the IP)
* Give this user the specific permissions (for test grant \*.* and access from %)

        SELECT on characters (obj_Id and char_name)
        INSERT on user_item_delivery
        UPDATE on user_item_delivery
        INSERT on donations
        
##### Notes
* Check your port **3306** is open https://canyouseeme.org
* In case you have 2 databases (login, game) you must install the SQL into game Database, otherwise it won't work.
* For security **do not use** the **wildcard** (%) for this user IP address, instead use your website host IP, so it will be accessed only from your web host.

#

###Extras (This step is optional)
####

##### Extras for SGUARD, SMARTGUARD or any client URL open method for auto login
*  To open your donation website with this method you have to add OpenDonateWebsite.java in your project
*  Your npc find the lines like this:

    	public void onBypassFeedback(Player player, String command)
    	{
            if (player == null)
                return;

            // add this line
            if (command.equals("buydc"))
            {
                // if you use smart guard add this
                player.sendPacket(new OpenDonateWebsite("https://web.com/shop/payment/?game_login=" + player.getName()));
                // if you use sguard
                player.sendPacket(new OpenURLPacket("https://web.com/shop/payment/?game_login=" + player.getName()));
            }
		    
            ... your code
		    
* Replace buydc (buy donate coins in sort) html of that npc that should contain a button like this:
        
        <button value="Buy Donate Coins" action="bypass -h npc_%objectId%_buydc" width=134 height=21 back="L2UI_ch3.BigButton3_over" fore="L2UI_ch3.BigButton3">

#### Player will experience the following:
* Click on some npc button ie: "Buy Donate Coins" or voiced command .donate (check installation examples)
* Player's browser automatically opens the donation url for your server
* ~~Player required to login with character name~~ ```we bypass this step with this function```
* Player is already in payment selection and automatically logged in
* Player pays you.
* Player gets reward in game instantly.



#### Code:
* if you use smart guard add this

        player.sendPacket(new OpenDonateWebsite("https://web.com/shop/payment/?game_login=" + player.getName()));


* if you use sguard

        player.sendPacket(new OpenURLPacket("https://web.com/shop/payment/?game_login=" + player.getName()));


* Note H5+ packs have url open methods
