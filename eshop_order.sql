/*
Navicat MySQL Data Transfer

Source Server         : order写库
Source Server Version : 50720
Source Host           : 192.168.1.123:3306
Source Database       : eshop_order

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2017-12-27 22:09:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `consumer_transaction_message`
-- ----------------------------
DROP TABLE IF EXISTS `consumer_transaction_message`;
CREATE TABLE `consumer_transaction_message` (
  `msgId` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `topic` varchar(50) DEFAULT NULL,
  `message_status` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`msgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of consumer_transaction_message
-- ----------------------------
INSERT INTO `consumer_transaction_message` VALUES ('39', '2017-12-27 21:42:16', 'account', '3');
INSERT INTO `consumer_transaction_message` VALUES ('40', '2017-12-27 20:53:18', 'account', '1');
INSERT INTO `consumer_transaction_message` VALUES ('41', '2017-12-27 20:40:56', 'account', '1');
INSERT INTO `consumer_transaction_message` VALUES ('42', '2017-12-27 21:53:02', 'account', '1');
INSERT INTO `consumer_transaction_message` VALUES ('43', '2017-12-27 22:05:54', 'account', '1');
INSERT INTO `consumer_transaction_message` VALUES ('44', '2017-12-27 22:08:22', 'account', '1');

-- ----------------------------
-- Table structure for `enterprise`
-- ----------------------------
DROP TABLE IF EXISTS `enterprise`;
CREATE TABLE `enterprise` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of enterprise
-- ----------------------------
INSERT INTO `enterprise` VALUES ('1', '2000');

-- ----------------------------
-- Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `user` bigint(20) DEFAULT NULL,
  `place_time` datetime DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `order_status` smallint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('1', '1', '1', '1', '2017-10-06 21:27:34', '300', '3');
INSERT INTO `order` VALUES ('2', '2', '2', '1', '2017-10-06 21:27:50', '800', '1');
INSERT INTO `order` VALUES ('3', '3', '1', '1', '2017-10-06 21:28:27', '500', '2');
INSERT INTO `order` VALUES ('4', '4', '1', '1', '2017-10-06 21:29:12', '300', '2');
INSERT INTO `order` VALUES ('5', '5', '5', '1', '2017-10-06 21:30:03', '2000', '1');
INSERT INTO `order` VALUES ('16', '24', '1', '1', '2017-10-14 17:17:45', '500', '3');
INSERT INTO `order` VALUES ('17', '24', '1', '1', '2017-10-14 17:26:09', '500', '3');
INSERT INTO `order` VALUES ('18', '24', '1', '1', '2017-10-14 17:36:26', '500', '2');
INSERT INTO `order` VALUES ('20', '24', '1', '1', '2017-10-14 19:25:04', '500', '1');
INSERT INTO `order` VALUES ('21', '25', '1', '1', '2017-10-14 21:02:23', '500', '1');
INSERT INTO `order` VALUES ('22', '2', '1', '1', '2017-10-15 12:04:25', '400', '2');
INSERT INTO `order` VALUES ('23', '24', '1', '1', '2017-10-15 17:32:03', '500', '2');
INSERT INTO `order` VALUES ('24', '5', '1', '1', '2017-10-15 17:32:21', '400', '2');
INSERT INTO `order` VALUES ('25', '24', '1', '1', '2017-10-16 08:26:04', '500', '2');
INSERT INTO `order` VALUES ('26', '25', '1', '1', '2017-10-18 13:59:02', '500', '3');
INSERT INTO `order` VALUES ('27', '25', '1', '1', '2017-10-18 13:59:19', '500', '1');
INSERT INTO `order` VALUES ('28', '4', '1', '1', '2017-12-22 15:56:25', '300', '2');
INSERT INTO `order` VALUES ('29', '4', '1', '1', '2017-12-22 15:57:50', '300', '2');
INSERT INTO `order` VALUES ('30', '17', '1', '1', '2017-12-22 16:05:34', '300', '2');
INSERT INTO `order` VALUES ('31', '17', '1', '1', '2017-12-22 16:14:16', '300', '2');
INSERT INTO `order` VALUES ('32', '17', '1', '1', '2017-12-22 16:22:07', '300', '1');
INSERT INTO `order` VALUES ('33', '24', '1', '1', '2017-12-22 16:22:50', '500', '2');
INSERT INTO `order` VALUES ('34', '23', '1', '1', '2017-12-22 16:23:14', '300', '2');
INSERT INTO `order` VALUES ('35', '4', '1', '1', '2017-12-22 16:23:57', '300', '2');
INSERT INTO `order` VALUES ('36', '4', '1', '1', '2017-12-22 16:29:06', '300', '2');
INSERT INTO `order` VALUES ('37', '27', '1', '1', '2017-12-22 16:29:55', '500', '1');
INSERT INTO `order` VALUES ('38', '18', '1', '1', '2017-12-22 16:32:43', '300', '1');
INSERT INTO `order` VALUES ('39', '24', '1', '1', '2017-12-23 13:34:55', '500', '0');
INSERT INTO `order` VALUES ('40', '24', '1', '1', '2017-12-23 22:22:27', '500', '1');
INSERT INTO `order` VALUES ('41', '21', '1', '1', '2017-12-23 22:22:36', '300', '1');
INSERT INTO `order` VALUES ('42', '18', '1', '1', '2017-12-27 21:48:51', '300', '0');
INSERT INTO `order` VALUES ('43', '18', '1', '1', '2017-12-27 22:05:42', '300', '1');
INSERT INTO `order` VALUES ('44', '18', '1', '1', '2017-12-27 22:08:02', '300', '1');
