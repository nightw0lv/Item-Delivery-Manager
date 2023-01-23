/*
 Navicat Premium Data Transfer

 Source Server         : DenArt-Designs.com
 Source Server Type    : MySQL
 Source Server Version : 100515
 Source Host           : 192.168.100.6:3306
 Source Schema         : donate

 Target Server Type    : MySQL
 Target Server Version : 100515
 File Encoding         : 65001

 Date: 21/05/2022 08:01:52


	1) You need to create a new database user/password for more security (dont use root)
	2) Access restricted to your host IP Address (ask your host provider for the IP)
	3) Give the user this specific permissions
	SELECT on characters (obj_Id and char_name)
	INSERT on user_item_delivery
	UPDATE on user_item_delivery
	INSERT on donations

	NOTE: if you have 2 databases (login, game) you must install the sql into game database, otherwise it wont work.
	NOTE: for extra security do not use the wildcard (%) for this user IP address, instead use your website host IP so it will be accessed only from there.

*/

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for donations
-- ----------------------------
DROP TABLE IF EXISTS `donations`;
CREATE TABLE `donations`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `character_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `date_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
