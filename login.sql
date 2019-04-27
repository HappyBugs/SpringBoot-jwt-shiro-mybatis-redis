/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : login

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-04-27 16:12:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(16) NOT NULL,
  `passWord` varchar(16) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `role` varchar(16) NOT NULL,
  `permission` varchar(225) NOT NULL,
  PRIMARY KEY (`id`,`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '你好', '123456', '12345678974', 'admin', 'view,edit');
INSERT INTO `user` VALUES ('2', '李四', '123456', '15847596547', 'user', 'view');
