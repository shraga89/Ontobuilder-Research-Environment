/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50146
Source Host           : localhost:3306
Source Database       : schemamatching

Target Server Type    : MYSQL
Target Server Version : 50146
File Encoding         : 65001

Date: 2010-06-19 23:39:50
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `datasets`
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets` (
  `DSID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DSName` varchar(50) NOT NULL,
  PRIMARY KEY (`DSID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of datasets
-- ----------------------------
INSERT INTO `datasets` VALUES ('1', 'OntoBuilder Webforms');
INSERT INTO `datasets` VALUES ('2', 'Tel-8');

-- ----------------------------
-- Table structure for `exactmatches`
-- ----------------------------
DROP TABLE IF EXISTS `exactmatches`;
CREATE TABLE `exactmatches` (
  `SPID` bigint(20) NOT NULL,
  `TargetTermID` int(11) NOT NULL,
  `CandidateTermID` int(11) NOT NULL,
  `ismatch` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SPID`,`TargetTermID`,`CandidateTermID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of exactmatches
-- ----------------------------

-- ----------------------------
-- Table structure for `experiments`
-- ----------------------------
DROP TABLE IF EXISTS `experiments`;
CREATE TABLE `experiments` (
  `EID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ExperimentDesc` varchar(50) DEFAULT NULL,
  `RunDate` datetime NOT NULL,
  PRIMARY KEY (`EID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of experiments
-- ----------------------------

-- ----------------------------
-- Table structure for `experimentschemapairs`
-- ----------------------------
DROP TABLE IF EXISTS `experimentschemapairs`;
CREATE TABLE `experimentschemapairs` (
  `Eid` bigint(20) NOT NULL,
  `SPID` bigint(20) NOT NULL,
  `training` tinyint(4) NOT NULL,
  PRIMARY KEY (`Eid`,`SPID`,`training`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of experimentschemapairs
-- ----------------------------

-- ----------------------------
-- Table structure for `mapping`
-- ----------------------------
DROP TABLE IF EXISTS `mapping`;
CREATE TABLE `mapping` (
  `EID` bigint(20) NOT NULL,
  `SPID` bigint(20) NOT NULL,
  `training` tinyint(4) NOT NULL,
  `TargetTermID` int(11) NOT NULL,
  `CandidateTermID` int(11) NOT NULL,
  `MatcherID` bigint(20) NOT NULL,
  `SMID` bigint(20) NOT NULL,
  `IsMapped` tinyint(4) NOT NULL,
  `TTName` text,
  `CTName` text,
  PRIMARY KEY (`EID`,`SPID`,`training`,`TargetTermID`,`CandidateTermID`,`MatcherID`,`SMID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of mapping
-- ----------------------------

-- ----------------------------
-- Table structure for `matchers`
-- ----------------------------
DROP TABLE IF EXISTS `matchers`;
CREATE TABLE `matchers` (
  `MatcherID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MatcherName` varchar(50) NOT NULL,
  `MatcherDesc` text,
  `System` char(10) DEFAULT NULL,
  PRIMARY KEY (`MatcherID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of matchers
-- ----------------------------

-- ----------------------------
-- Table structure for `matcherweights`
-- ----------------------------
DROP TABLE IF EXISTS `matcherweights`;
CREATE TABLE `matcherweights` (
  `EID` bigint(20) NOT NULL,
  `mID` bigint(20) NOT NULL,
  `SMID` bigint(20) NOT NULL,
  `weight` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of matcherweights
-- ----------------------------

-- ----------------------------
-- Table structure for `outliers`
-- ----------------------------
DROP TABLE IF EXISTS `outliers`;
CREATE TABLE `outliers` (
  `EID` bigint(20) DEFAULT NULL,
  `CandSchema` varchar(125) DEFAULT NULL,
  `TargSchema` varchar(125) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of outliers
-- ----------------------------

-- ----------------------------
-- Table structure for `schemapairs`
-- ----------------------------
DROP TABLE IF EXISTS `schemapairs`;
CREATE TABLE `schemapairs` (
  `SPID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DSID` bigint(20) NOT NULL,
  `TargetSchema` bigint(20) NOT NULL,
  `CandidateSchema` bigint(20) NOT NULL,
  `path` longtext,
  PRIMARY KEY (`SPID`)
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of schemapairs
-- ----------------------------
INSERT INTO `schemapairs` VALUES ('1', '1', '1', '2', 'schema/1-time.xml_2-surfer.xml_EXACT/1-time.xml_2-surfer.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('2', '1', '3', '4', 'schema/6-people.xml_4-sportsillustrated.xml_EXACT/6-people.xml_4-sportsillustrated.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('3', '1', '5', '6', 'schema/6-people.xml_5-vogue.xml_EXACT/6-people.xml_5-vogue.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('4', '1', '7', '8', 'schema/7-powder.xml_8-windsurfing.xml_EXACT/7-powder.xml_8-windsurfing.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('5', '1', '9', '10', 'schema/absoluteagency.xml_www.dating.com.xml_EXACT/absoluteagency.xml_www.dating.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('6', '1', '11', '12', 'schema/accountservices.passport.net.xml_ldbreg.lycos.com.xml_EXACT/accountservices.passport.net.xml_ldbreg.lycos.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('7', '1', '13', '14', 'schema/auth.ivillage.co.uk.xml_www.777happyline.com.xml_EXACT/auth.ivillage.co.uk.xml_www.777happyline.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('8', '1', '15', '16', 'schema/betus.com.xml_linesmaker.com.xml_EXACT/betus.com.xml_linesmaker.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('9', '1', '17', '18', 'schema/betus.com.xml_secure.sportsinteraction.com.xml_EXACT/betus.com.xml_secure.sportsinteraction.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('10', '1', '19', '20', 'schema/betus.com.xml_wp.eurobet.com.xml_EXACT/betus.com.xml_wp.eurobet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('11', '1', '21', '22', 'schema/betus.com.xml_www.bet-at-home.com.xml_EXACT/betus.com.xml_www.bet-at-home.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('12', '1', '23', '24', 'schema/betus.com.xml_www.betdirect.net.xml_EXACT/betus.com.xml_www.betdirect.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('13', '1', '25', '26', 'schema/betus.com.xml_www.bettingexpress.com.xml_EXACT/betus.com.xml_www.bettingexpress.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('14', '1', '27', '28', 'schema/betus.com.xml_www.cybersportsbook.com.xml_EXACT/betus.com.xml_www.cybersportsbook.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('15', '1', '29', '30', 'schema/betus.com.xml_www.gonegambling.com.xml_EXACT/betus.com.xml_www.gonegambling.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('16', '1', '31', '32', 'schema/betus.com.xml_www.vegas-sportsbetting.com.xml_EXACT/betus.com.xml_www.vegas-sportsbetting.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('17', '1', '33', '34', 'schema/betus.com.xml_www.willhill.com.xml_EXACT/betus.com.xml_www.willhill.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('18', '1', '35', '36', 'schema/cms.lufthansa.com.xml_www.britishairways.com.xml_EXACT/cms.lufthansa.com.xml_www.britishairways.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('19', '1', '37', '38', 'schema/cms.lufthansa.com.xml_www.delta.com.xml_EXACT/cms.lufthansa.com.xml_www.delta.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('20', '1', '39', '40', 'schema/cms.lufthansa.com.xml_www.elal.co.il.xml_EXACT/cms.lufthansa.com.xml_www.elal.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('21', '1', '41', '42', 'schema/edit.travel.yahoo.com.xml_travel.kelkoo.co.uk.xml_EXACT/edit.travel.yahoo.com.xml_travel.kelkoo.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('22', '1', '43', '44', 'schema/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('23', '1', '45', '46', 'schema/edit.yahoo.com.xml_4842.runbox.com.xml_EXACT/edit.yahoo.com.xml_4842.runbox.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('24', '1', '47', '48', 'schema/edit2.yahoo.com.xml_ldbreg.lycos.com.xml_EXACT/edit2.yahoo.com.xml_ldbreg.lycos.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('25', '1', '49', '50', 'schema/fanclub.wd40.com.xml_www.am630.net.xml_EXACT/fanclub.wd40.com.xml_www.am630.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('26', '1', '51', '52', 'schema/flipfloptrunkshow.com.xml_www.finishline.com.xml_EXACT/flipfloptrunkshow.com.xml_www.finishline.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('27', '1', '53', '54', 'schema/forums.sijun.com.xml_www.eatpoo.com.xml_EXACT/forums.sijun.com.xml_www.eatpoo.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('28', '1', '55', '56', 'schema/Galei_Eilat.xml_www.danielhotel.com.xml_EXACT/Galei.Eilat.xml_www.danielhotel.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('29', '1', '57', '58', 'schema/hotel.de_res.findlocalhotels.com/hotel.de.xml_res.findlocalhotels.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('30', '1', '59', '60', 'schema/hotels.securebooking.org_www.ichotelsgroup.com/hotels.securebooking.org.xml_www.ichotelsgroup.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('31', '1', '61', '62', 'schema/hotmail.passport.net.xml_edit.yahoo.com.xml_EXACT/hotmail.passport.net.xml_edit.yahoo.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('32', '1', '63', '64', 'schema/info.hotelsvalued.com_frisco-colorado.where-to-stay.com/info.hotelsvalued.com.xml_frisco-colorado.where-to-stay.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('33', '1', '65', '66', 'schema/kaxy.xml_www.cashette.com.xml_EXACT/kaxy.xml_www.cashette.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('34', '1', '67', '68', 'schema/ldbreg.lycos.com.xml_hotmail.passport.net.xml_EXACT/ldbreg.lycos.com.xml_hotmail.passport.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('35', '1', '69', '70', 'schema/linesmaker.com.xml_secure.sportsinteraction.com.xml_EXACT/linesmaker.com.xml_secure.sportsinteraction.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('36', '1', '71', '72', 'schema/linesmaker.com.xml_wp.eurobet.com.xml_EXACT/linesmaker.com.xml_wp.eurobet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('37', '1', '73', '74', 'schema/linesmaker.com.xml_www.bet-at-home.com.xml_EXACT/linesmaker.com.xml_www.bet-at-home.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('38', '1', '75', '76', 'schema/linesmaker.com.xml_www.betdirect.net.xml_EXACT/linesmaker.com.xml_www.betdirect.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('39', '1', '77', '78', 'schema/linesmaker.com.xml_www.bettingexpress.com.xml_EXACT/linesmaker.com.xml_www.bettingexpress.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('40', '1', '79', '80', 'schema/linesmaker.com.xml_www.cybersportsbook.com.xml_EXACT/linesmaker.com.xml_www.cybersportsbook.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('41', '1', '81', '82', 'schema/linesmaker.com.xml_www.gonegambling.com.xml_EXACT/linesmaker.com.xml_www.gonegambling.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('42', '1', '83', '84', 'schema/linesmaker.com.xml_www.vegas-sportsbetting.com.xml_EXACT/linesmaker.com.xml_www.vegas-sportsbetting.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('43', '1', '85', '86', 'schema/linesmaker.com.xml_www.willhill.com.xml_EXACT/linesmaker.com.xml_www.willhill.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('44', '1', '87', '88', 'schema/linesmaker.com.xml_www.youbet.com.xml_EXACT/linesmaker.com.xml_www.youbet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('45', '1', '89', '90', 'schema/lonelyplanet.xml_www.klm.com.xml_EXACT/lonelyplanet.xml_www.klm.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('46', '1', '91', '92', 'schema/lonelyplanet.xml_www.ryanair.com.xml_EXACT/lonelyplanet.xml_www.ryanair.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('47', '1', '93', '94', 'schema/MevoJerusalem.xml_NeptuneEilat.xml_EXACT/MevoJerusalem.xml_NeptuneEilat.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('48', '1', '95', '96', 'schema/moneycentral.msn.com-screen.morningstar.com/moneycentral.msn.com.xml_screen.morningstar.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('49', '1', '97', '98', 'schema/OrchidEilat.xml_www.royal-plaza.co.il.xml_EXACT/OrchidEilat.xml_www.royal-plaza.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('50', '1', '99', '100', 'schema/passport.care2.net.xml_rapids.canoe.ca.xml_EXACT/passport.care2.net.xml_rapids.canoe.ca.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('51', '1', '101', '102', 'schema/passport.care2.net.xml_registration.excite.com.xml_EXACT/passport.care2.net.xml_registration.excite.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('52', '1', '103', '104', 'schema/prosearch.businessweek.com-moneycentral.msn.com/prosearch.businessweek.com.xml_moneycentral.msn.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('53', '1', '105', '106', 'schema/rapids.canoe.ca.xml_www.fusemail.com.xml_EXACT/rapids.canoe.ca.xml_www.fusemail.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('54', '1', '107', '108', 'schema/registration.excite.com.xml_www.cashette.com.xml_EXACT/registration.excite.com.xml_www.cashette.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('55', '1', '109', '110', 'schema/screen.finance.yahoo.com-finance.aol.com/screen.finance.yahoo.com.xml_finance.aol.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('56', '1', '111', '112', 'schema/search.abcnews.go.com.xml_www.washingtonpost.com.xml_EXACT/search.abcnews.go.com.xml_www.washingtonpost.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('57', '1', '113', '114', 'schema/search.scotsman.com.xml_search.sky.com.xml_EXACT/search.scotsman.com.xml_search.sky.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('58', '1', '115', '116', 'schema/secure.kalmbach.com.xml_www.kable.com.xml_EXACT/secure.kalmbach.com.xml_www.kable.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('59', '1', '117', '118', 'schema/secure.sportsinteraction.com.xml_wp.eurobet.com.xml_EXACT/secure.sportsinteraction.com.xml_wp.eurobet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('60', '1', '119', '120', 'schema/secure.sportsinteraction.com.xml_www.bet-at-home.com.xml_EXACT/secure.sportsinteraction.com.xml_www.bet-at-home.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('61', '1', '121', '122', 'schema/secure.sportsinteraction.com.xml_www.betdirect.net.xml_EXACT/secure.sportsinteraction.com.xml_www.betdirect.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('62', '1', '123', '124', 'schema/secure.sportsinteraction.com.xml_www.bettingexpress.com.xml_EXACT/secure.sportsinteraction.com.xml_www.bettingexpress.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('63', '1', '125', '126', 'schema/secure.sportsinteraction.com.xml_www.cybersportsbook.com.xml_EXACT/secure.sportsinteraction.com.xml_www.cybersportsbook.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('64', '1', '127', '128', 'schema/secure.sportsinteraction.com.xml_www.gonegambling.com.xml_EXACT/secure.sportsinteraction.com.xml_www.gonegambling.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('65', '1', '129', '130', 'schema/secure.sportsinteraction.com.xml_www.vegas-sportsbetting.com.xml_EXACT/secure.sportsinteraction.com.xml_www.vegas-sportsbetting.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('66', '1', '131', '132', 'schema/secure.sportsinteraction.com.xml_www.willhill.com.xml_EXACT/secure.sportsinteraction.com.xml_www.willhill.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('67', '1', '133', '134', 'schema/secure.sportsinteraction.com.xml_www.youbet.com.xml_EXACT/secure.sportsinteraction.com.xml_www.youbet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('68', '1', '135', '136', 'schema/securewn.com.xml_www.eldan.co.il.xml_EXACT/securewn.com.xml_www.eldan.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('69', '1', '137', '138', 'schema/shop.sae.org.xml_www.knag.nl.xml_EXACT/shop.sae.org.xml_www.knag.nl.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('70', '1', '139', '140', 'schema/subscribe.americancityandcounty.com.xml_www.nightclub.com.xml_EXACT/subscribe.americancityandcounty.com.xml_www.nightclub.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('71', '1', '141', '142', 'schema/taut.securesites.co.xml_www1522.boca15-verio.co1.xml_EXACT/taut.securesites.com.xml_www1522.boca15-verio.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('72', '1', '143', '144', 'schema/travelwww.aexp.be.xml_www.experienced-people.co.uk.xml_EXACT/travelwww.aexp.be.xml_www.experienced-people.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('73', '1', '145', '146', 'schema/twinkletoesbabyshoes.com.xml_www.hilohattie.com.xml_EXACT/twinkletoesbabyshoes.com.xml_www.hilohattie.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('74', '1', '147', '148', 'schema/wbln0018.worldbank.org.xml_www.gravesham.gov.uk.xml_EXACT/wbln0018.worldbank.org.xml_www.gravesham.gov.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('75', '1', '149', '150', 'schema/webcenter.love.match.netscape.com.xml_www.date.com.xml_EXACT/webcenter.love.match.netscape.com.xml_www.date.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('76', '1', '151', '152', 'schema/webcenter.love.match.netscape.com.xml_www.lovetomeetyou.com.xml_EXACT/webcenter.love.match.netscape.com.xml_www.lovetomeetyou.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('77', '1', '153', '154', 'schema/webmail.co.za.xml_www.marchmail.com.xml_EXACT/webmail.co.za.xml_www.marchmail.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('78', '1', '155', '156', 'schema/wp.eurobet.com.xml_www.bet-at-home.com.xml_EXACT/wp.eurobet.com.xml_www.bet-at-home.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('79', '1', '157', '158', 'schema/www.amazon.com.xml_www.audible.com.xml_EXACT/www.amazon.com.xml_www.audible.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('80', '1', '159', '160', 'schema/www.amazon.com.xml_www.powells.com.xml_EXACT/www.amazon.com.xml_www.powells.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('81', '1', '161', '162', 'schema/www.amerciansingles.com.xml_www.datemeister.com.xml_EXACT/www.amerciansingles.com.xml_www.datemeister.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('82', '1', '163', '164', 'schema/www.americanairlines.com.xml_www.continental.com.xml_EXACT/www.americanairlines.com.xml_www.continental.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('83', '1', '165', '166', 'schema/www.another.com.xml_www.topica.com.xml_EXACT/www.another.com.xml_www.topica.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('84', '1', '167', '168', 'schema/www.arabia.com.xml_www.dbzmail.com.xml_EXACT/www.arabia.com.xml_www.dbzmail.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('85', '1', '169', '170', 'schema/www.atlastwemeet.com.xml_www.ezboard.com.xml_EXACT/www.atlastwemeet.com.xml_www.ezboard.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('86', '1', '171', '172', 'schema/www.audible.com.xml_www.sagebrushcorp.com.xml_EXACT/www.audible.com.xml_www.sagebrushcorp.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('87', '1', '173', '174', 'schema/www.autoeurope.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.autoeurope.co.il.xml_www.rentdirect.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('88', '1', '175', '176', 'schema/www.bbltamex.com.xml_www.experienced-people.co.uk.xml_EXACT/www.bbltamex.com.xml_www.experienced-people.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('89', '1', '177', '178', 'schema/www.besthotel.com_www.secure-reservations.net/www.besthotel.com.xml_www.secure-reservations.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('90', '1', '179', '180', 'schema/www.bestindianhotels.com.xml_www.hotelclub.net.xml_EXACT/www.bestindianhotels.com.xml_www.hotelclub.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('91', '1', '181', '182', 'schema/www.bet-at-home.com.xml_www.youbet.com.xml_EXACT/www.bet-at-home.com.xml_www.youbet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('92', '1', '183', '184', 'schema/www.betdirect.net.xml_wp.eurobet.com.xml_EXACT/www.betdirect.net.xml_wp.eurobet.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('93', '1', '185', '186', 'schema/www.boardermail.com.xml_www.linuxmail.com.xml_EXACT/www.boardermail.com.xml_www.linuxmail.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('94', '1', '187', '188', 'schema/www.bravenet.com.xml_ldbreg.lycos.com.xml_EXACT/www.bravenet.com.xml_ldbreg.lycos.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('95', '1', '189', '190', 'schema/www.britishairways.com.xml_www.thy.com.xml_EXACT/www.britishairways.com.xml_www.thy.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('96', '1', '191', '192', 'schema/www.britishairways.com.xml_www.ual.com.xml_EXACT/www.britishairways.com.xml_www.ual.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('97', '1', '193', '194', 'schema/www.budget.co.il.xml_www.eldan.co.il.xml_EXACT/www.budget.co.il.xml_www.eldan.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('98', '1', '195', '196', 'schema/www.cashette.com.xml_accountservices.passport.net.xml_EXACT/www.cashette.com.xml_accountservices.passport.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('99', '1', '197', '198', 'schema/www.cashette.com.xml_login.myspace.com.xml_EXACT/www.cashette.com.xml_login.myspace.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('100', '1', '199', '200', 'schema/www.cashette.com.xml_www2.inmail24.com.xml_EXACT/www.cashette.com.xml_www2.inmail24.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('101', '1', '201', '202', 'schema/www.coupleme.com.xml_www.loveaccess.com.xml_EXACT/www.coupleme.com.xml_www.loveaccess.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('102', '1', '203', '204', 'schema/www.coupleme.com.xml_www.singleme.com.xml_EXACT/www.coupleme.com.xml_www.singleme.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('103', '1', '205', '206', 'schema/www.couponforum.com.xml_www.ghchealth.com.xml_EXACT/www.couponforum.com.xml_www.ghchealth.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('104', '1', '207', '208', 'schema/www.cupidusa.com.xml_www.jdate.com.xml_EXACT/www.cupidusa.com.xml_www.jdate.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('105', '1', '209', '210', 'schema/www.cybersportsbook.com.xml_www.bettingexpress.com.xml_EXACT/www.cybersportsbook.com.xml_www.bettingexpress.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('106', '1', '211', '212', 'schema/www.cybersuitors.com.xml_www.date.com.xml_EXACT.xml/www.cybersuitors.com.xml_www.date.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('107', '1', '213', '214', 'schema/www.debtbusterloans.com.xml_www.doubleclickloans.co.uk.xml_EXACT/www.debtbusterloans.com.xml_www.doubleclickloans.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('108', '1', '215', '216', 'schema/www.delta.com.xml_www.elal.co.il.xml_EXACT/www.delta.com.xml_www.elal.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('109', '1', '217', '218', 'schema/www.delta.com.xml_www.ual.com.xml_EXACT/www.delta.com.xml_www.ual.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('110', '1', '219', '220', 'schema/www.digitallook.com-screen.finance.yahoo.com/www.digitallook.com.xml_screen.finance.yahoo.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('111', '1', '221', '222', 'schema/www.dream-dating.org.xml_www.chemistry.com.xml_EXACT/www.dream-dating.org.xml_www.chemistry.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('112', '1', '223', '224', 'schema/www.dream-dating.org.xml_www.matchmaker.com.xml_EXACT/www.dream-dating.org.xml_www.matchmaker.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('113', '1', '225', '226', 'schema/www.dream-dating.org.xml_www.singleme.com.xml_EXACT/www.dream-dating.org.xml_www.singleme.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('114', '1', '227', '228', 'schema/www.elal.co.il.xml_www.thy.com.xml_EXACT/www.elal.co.il.xml_www.thy.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('115', '1', '229', '230', 'schema/www.enriqueiglesias.com.xml_www.yanni.com.xml_EXACT/www.enriqueiglesias.com.xml_www.yanni.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('116', '1', '231', '232', 'schema/www.eservus.com.xml_www.theatremania.org.xml_EXACT/www.eservus.com.xml_www.theatremania.org.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('117', '1', '233', '234', 'schema/www.firstmeritib.com.xml_www.financial-securesite.com.xml_EXACT/www.firstmeritib.com.xml_www.financial-securesite.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('118', '1', '235', '236', 'schema/www.firstmeritib.com.xml_www.moneymart.ca.xml_EXACT/www.firstmeritib.com.xml_www.moneymart.ca.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('119', '1', '237', '238', 'schema/www.frbatlanta.org.xml_www.state.il.us.xml_EXACT/www.frbatlanta.org.xml_www.state.il.us.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('120', '1', '239', '240', 'schema/www.fusemail.com.xml_login.myspace.com.xml_EXACT/www.fusemail.com.xml_login.myspace.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('121', '1', '241', '242', 'schema/www.globeinvestor.com-finance.aol.com/www.globeinvestor.com.xml_finance.aol.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('122', '1', '243', '244', 'schema/www.gonegambling.com.xml_www.willhill.com.xml_EXACT/www.gonegambling.com.xml_www.willhill.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('123', '1', '245', '246', 'schema/www.google.com.xml_www.yahoo.com.xml_EXACT/www.google.com.xml_www.yahoo.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('124', '1', '247', '248', 'schema/www.hertz.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.hertz.co.il.xml_www.rentdirect.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('125', '1', '249', '250', 'schema/www.italyhotelink.com.xml_www.venere.com.xml_EXACT/www.italyhotelink.com.xml_www.venere.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('126', '1', '251', '252', 'schema/www.kswiss.com.xml_www.mgear.com.xml_EXACT/www.kswiss.com.xml_www.mgear.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('127', '1', '253', '254', 'schema/www.lastminute.com.xml_www.klm.com.xml_EXACT/www.lastminute.com.xml_www.klm.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('128', '1', '255', '256', 'schema/www.maineadvantage.com.xml_www.financialaid.com.xml_EXACT/www.maineadvantage.com.xml_www.financialaid.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('129', '1', '257', '258', 'schema/www.marketwatch.com-www.globeinvestor.com/www.marketwatch.com.xml_www.globeinvestor.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('130', '1', '259', '260', 'schema/www.miss-janet.com.xml_www.myreg.net.xml_EXACT/www.miss-janet.com.xml_www.myreg.net.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('131', '1', '261', '262', 'schema/www.moneymart.ca.xml_www.no-fax-loan.com.xml_EXACT/www.moneymart.ca.xml_www.no-fax-loan.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('132', '1', '263', '264', 'schema/www.motels.com.xml_www.hotellocators.com.xml_EXACT/www.motels.com.xml_www.hotellocators.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('133', '1', '265', '266', 'schema/www.myfunnymail.co-www.postmaster.co.u1_EXACT/www.myfunnymail.com.xml_www.postmaster.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('134', '1', '267', '268', 'schema/www.nationaldefensemagazine.org.xml_magshop.co.nz.xml_EXACT/www.nationaldefensemagazine.org.xml_magshop.co.nz.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('135', '1', '269', '270', 'schema/www.nbwebexpress.com.xml_www.bootsunlimited.com.xml_EXACT/www.nbwebexpress.com.xml_www.bootsunlimited.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('136', '1', '271', '272', 'schema/www.netcheck.com.xml_www.omisan.com.xml_EXACT/www.netcheck.com.xml_www.omisan.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('137', '1', '273', '274', 'schema/www.no-fax-loan.com.xml_applications.us.com.xml_EXACT/www.no-fax-loan.com.xml_applications.us.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('138', '1', '275', '276', 'schema/www.ofran.com.xml_www.alamo.co.il.xml_EXACT/www.ofran.com.xml_www.alamo.co.il.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('139', '1', '277', '278', 'schema/www.onlinereservationz.com_www.besthotel.com/www.onlinereservationz.com.xml_www.besthotel.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('140', '1', '279', '280', 'schema/www.opensystems-publishing.com.xml_www.omeda.com.xml_EXACT/www.opensystems-publishing.com.xml_www.omeda.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('141', '1', '281', '282', 'schema/www.paragonsports.com.xml_www.filastore.com.xml_EXACT/www.paragonsports.com.xml_www.filastore.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('142', '1', '283', '284', 'schema/www.powells.com.xml_www.sagebrushcorp.com.xml_EXACT/www.powells.com.xml_www.sagebrushcorp.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('143', '1', '285', '286', 'schema/www.priceline.com.xml_www.orbitz.com.xml_EXACT/www.priceline.com.xml_www.orbitz.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('144', '1', '287', '288', 'schema/www.printsolutionsmag.com.xml_www.associationmanager.co.uk.xml_EXACT/www.printsolutionsmag.com.xml_www.associationmanager.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('145', '1', '289', '290', 'schema/www.psc.state.al.us.xml_www.clevelandfed.org.xml_EXACT/www.psc.state.al.us.xml_www.clevelandfed.org.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('146', '1', '291', '292', 'schema/www.safc.com.xml_www.mfc.premiumtv.co.uk.xml_EXACT/www.safc.com.xml_www.mfc.premiumtv.co.uk.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('147', '1', '293', '294', 'schema/www.sportsmansguide.com.xml_www.northlandmarine.com.xml_EXACT/www.sportsmansguide.com.xml_www.northlandmarine.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('148', '1', '295', '296', 'schema/www.stlouisrams.com.xml_secure2.steelers.com.xml_EXACT/www.stlouisrams.com.xml_secure2.steelers.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('149', '1', '297', '298', 'schema/www.tio.com.au.xml_svartifoss2.fcc.gov.xml_EXACT/www.tio.com.au.xml_svartifoss2.fcc.gov.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('150', '1', '299', '300', 'schema/www.ual.com.xml_www.thy.com.xml_EXACT/www.ual.com.xml_www.thy.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('151', '1', '301', '302', 'schema/www.webdate.com.xml_www.syl.com.xml_EXACT/www.webdate.com.xml_www.syl.com.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('152', '1', '303', '304', 'schema/www2.cityofseattle.net.xml_www.ci.glendale.az.us.xml_EXACT/www2.cityofseattle.net.xml_www.ci.glendale.az.us.xml_EXACT.xml');
INSERT INTO `schemapairs` VALUES ('153', '1', '305', '306', 'schema/www2.inmail24.com.xml_Gmail.xml_EXACT/www2.inmail24.com.xml_Gmail.xml_EXACT.xml');

-- ----------------------------
-- Table structure for `schemata`
-- ----------------------------
DROP TABLE IF EXISTS `schemata`;
CREATE TABLE `schemata` (
  `SchemaID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SchemaName` varchar(300) DEFAULT NULL,
  `DSID` bigint(20) DEFAULT NULL,
  `DS_SchemaID` bigint(20) DEFAULT NULL,
  `path` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`SchemaID`)
) ENGINE=InnoDB AUTO_INCREMENT=779 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of schemata
-- ----------------------------
INSERT INTO `schemata` VALUES ('1', '1-time', '1', null, 'schema/1-time.xml_2-surfer.xml_EXACT/1-time.xml');
INSERT INTO `schemata` VALUES ('2', '2-surfer', '1', null, 'schema/1-time.xml_2-surfer.xml_EXACT/2-surfer.xml');
INSERT INTO `schemata` VALUES ('3', '4-sportsillustrated', '1', null, 'schema/6-people.xml_4-sportsillustrated.xml_EXACT/4-sportsillustrated.xml');
INSERT INTO `schemata` VALUES ('4', '6-people', '1', null, 'schema/6-people.xml_4-sportsillustrated.xml_EXACT/6-people.xml');
INSERT INTO `schemata` VALUES ('5', '5-vogue', '1', null, 'schema/6-people.xml_5-vogue.xml_EXACT/5-vogue.xml');
INSERT INTO `schemata` VALUES ('6', '6-people', '1', null, 'schema/6-people.xml_5-vogue.xml_EXACT/6-people.xml');
INSERT INTO `schemata` VALUES ('7', '7-powder', '1', null, 'schema/7-powder.xml_8-windsurfing.xml_EXACT/7-powder.xml');
INSERT INTO `schemata` VALUES ('8', '8-windsurfing', '1', null, 'schema/7-powder.xml_8-windsurfing.xml_EXACT/8-windsurfing.xml');
INSERT INTO `schemata` VALUES ('9', 'absoluteagency', '1', null, 'schema/absoluteagency.xml_www.dating.com.xml_EXACT/absoluteagency.xml');
INSERT INTO `schemata` VALUES ('10', 'www.dating.com', '1', null, 'schema/absoluteagency.xml_www.dating.com.xml_EXACT/www.dating.com.xml');
INSERT INTO `schemata` VALUES ('11', 'accountservices.passport.net', '1', null, 'schema/accountservices.passport.net.xml_ldbreg.lycos.com.xml_EXACT/accountservices.passport.net.xml');
INSERT INTO `schemata` VALUES ('12', 'ldbreg.lycos.com', '1', null, 'schema/accountservices.passport.net.xml_ldbreg.lycos.com.xml_EXACT/ldbreg.lycos.com.xml');
INSERT INTO `schemata` VALUES ('13', 'auth.ivillage.co.uk', '1', null, 'schema/auth.ivillage.co.uk.xml_www.777happyline.com.xml_EXACT/auth.ivillage.co.uk.xml');
INSERT INTO `schemata` VALUES ('14', 'www.777happyline.com', '1', null, 'schema/auth.ivillage.co.uk.xml_www.777happyline.com.xml_EXACT/www.777happyline.com.xml');
INSERT INTO `schemata` VALUES ('15', 'betus.com', '1', null, 'schema/betus.com.xml_linesmaker.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('16', 'linesmaker.com', '1', null, 'schema/betus.com.xml_linesmaker.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('17', 'betus.com', '1', null, 'schema/betus.com.xml_secure.sportsinteraction.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('18', 'secure.sportsinteraction.com', '1', null, 'schema/betus.com.xml_secure.sportsinteraction.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('19', 'betus.com', '1', null, 'schema/betus.com.xml_wp.eurobet.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('20', 'wp.eurobet.com', '1', null, 'schema/betus.com.xml_wp.eurobet.com.xml_EXACT/wp.eurobet.com.xml');
INSERT INTO `schemata` VALUES ('21', 'betus.com', '1', null, 'schema/betus.com.xml_www.bet-at-home.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('22', 'www.bet-at-home.com', '1', null, 'schema/betus.com.xml_www.bet-at-home.com.xml_EXACT/www.bet-at-home.com.xml');
INSERT INTO `schemata` VALUES ('23', 'betus.com', '1', null, 'schema/betus.com.xml_www.betdirect.net.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('24', 'www.betdirect.net', '1', null, 'schema/betus.com.xml_www.betdirect.net.xml_EXACT/www.betdirect.net.xml');
INSERT INTO `schemata` VALUES ('25', 'betus.com', '1', null, 'schema/betus.com.xml_www.bettingexpress.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('26', 'www.bettingexpress.com', '1', null, 'schema/betus.com.xml_www.bettingexpress.com.xml_EXACT/www.bettingexpress.com.xml');
INSERT INTO `schemata` VALUES ('27', 'betus.com', '1', null, 'schema/betus.com.xml_www.cybersportsbook.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('28', 'www.cybersportsbook.com', '1', null, 'schema/betus.com.xml_www.cybersportsbook.com.xml_EXACT/www.cybersportsbook.com.xml');
INSERT INTO `schemata` VALUES ('29', 'betus.com', '1', null, 'schema/betus.com.xml_www.gonegambling.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('30', 'www.gonegambling.com', '1', null, 'schema/betus.com.xml_www.gonegambling.com.xml_EXACT/www.gonegambling.com.xml');
INSERT INTO `schemata` VALUES ('31', 'betus.com', '1', null, 'schema/betus.com.xml_www.vegas-sportsbetting.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('32', 'www.vegas-sportsbetting.com', '1', null, 'schema/betus.com.xml_www.vegas-sportsbetting.com.xml_EXACT/www.vegas-sportsbetting.com.xml');
INSERT INTO `schemata` VALUES ('33', 'betus.com', '1', null, 'schema/betus.com.xml_www.willhill.com.xml_EXACT/betus.com.xml');
INSERT INTO `schemata` VALUES ('34', 'www.willhill.com', '1', null, 'schema/betus.com.xml_www.willhill.com.xml_EXACT/www.willhill.com.xml');
INSERT INTO `schemata` VALUES ('35', 'cms.lufthansa.com', '1', null, 'schema/cms.lufthansa.com.xml_www.britishairways.com.xml_EXACT/cms.lufthansa.com.xml');
INSERT INTO `schemata` VALUES ('36', 'www.britishairways.com', '1', null, 'schema/cms.lufthansa.com.xml_www.britishairways.com.xml_EXACT/www.britishairways.com.xml');
INSERT INTO `schemata` VALUES ('37', 'cms.lufthansa.com', '1', null, 'schema/cms.lufthansa.com.xml_www.delta.com.xml_EXACT/cms.lufthansa.com.xml');
INSERT INTO `schemata` VALUES ('38', 'www.delta.com', '1', null, 'schema/cms.lufthansa.com.xml_www.delta.com.xml_EXACT/www.delta.com.xml');
INSERT INTO `schemata` VALUES ('39', 'cms.lufthansa.com', '1', null, 'schema/cms.lufthansa.com.xml_www.elal.co.il.xml_EXACT/cms.lufthansa.com.xml');
INSERT INTO `schemata` VALUES ('40', 'www.elal.co.il', '1', null, 'schema/cms.lufthansa.com.xml_www.elal.co.il.xml_EXACT/www.elal.co.il.xml');
INSERT INTO `schemata` VALUES ('41', 'edit.travel.yahoo.com', '1', null, 'schema/edit.travel.yahoo.com.xml_travel.kelkoo.co.uk.xml_EXACT/edit.travel.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('42', 'travel.kelkoo.co.uk', '1', null, 'schema/edit.travel.yahoo.com.xml_travel.kelkoo.co.uk.xml_EXACT/travel.kelkoo.co.uk.xml');
INSERT INTO `schemata` VALUES ('43', 'edit.travel.yahoo.com', '1', null, 'schema/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT/edit.travel.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('44', 'www.klm.com', '1', null, 'schema/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT/www.klm.com.xml');
INSERT INTO `schemata` VALUES ('45', '4842.runbox.com', '1', null, 'schema/edit.yahoo.com.xml_4842.runbox.com.xml_EXACT/4842.runbox.com.xml');
INSERT INTO `schemata` VALUES ('46', 'edit.yahoo.com', '1', null, 'schema/edit.yahoo.com.xml_4842.runbox.com.xml_EXACT/edit.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('47', 'edit2.yahoo.com', '1', null, 'schema/edit2.yahoo.com.xml_ldbreg.lycos.com.xml_EXACT/edit2.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('48', 'ldbreg.lycos.com', '1', null, 'schema/edit2.yahoo.com.xml_ldbreg.lycos.com.xml_EXACT/ldbreg.lycos.com.xml');
INSERT INTO `schemata` VALUES ('49', 'fanclub.wd40.com', '1', null, 'schema/fanclub.wd40.com.xml_www.am630.net.xml_EXACT/fanclub.wd40.com.xml');
INSERT INTO `schemata` VALUES ('50', 'www.am630.net', '1', null, 'schema/fanclub.wd40.com.xml_www.am630.net.xml_EXACT/www.am630.net.xml');
INSERT INTO `schemata` VALUES ('51', 'flipfloptrunkshow.com', '1', null, 'schema/flipfloptrunkshow.com.xml_www.finishline.com.xml_EXACT/flipfloptrunkshow.com.xml');
INSERT INTO `schemata` VALUES ('52', 'www.finishline.com', '1', null, 'schema/flipfloptrunkshow.com.xml_www.finishline.com.xml_EXACT/www.finishline.com.xml');
INSERT INTO `schemata` VALUES ('53', 'forums.sijun.com', '1', null, 'schema/forums.sijun.com.xml_www.eatpoo.com.xml_EXACT/forums.sijun.com.xml');
INSERT INTO `schemata` VALUES ('54', 'www.eatpoo.com', '1', null, 'schema/forums.sijun.com.xml_www.eatpoo.com.xml_EXACT/www.eatpoo.com.xml');
INSERT INTO `schemata` VALUES ('55', 'Galei.Eilat', '1', null, 'schema/Galei_Eilat.xml_www.danielhotel.com.xml_EXACT/Galei.Eilat.xml');
INSERT INTO `schemata` VALUES ('56', 'www.danielhotel.com', '1', null, 'schema/Galei_Eilat.xml_www.danielhotel.com.xml_EXACT/www.danielhotel.com.xml');
INSERT INTO `schemata` VALUES ('57', 'hotel.de', '1', null, 'schema/hotel.de_res.findlocalhotels.com/hotel.de.xml');
INSERT INTO `schemata` VALUES ('58', 'res.findlocalhotels.com', '1', null, 'schema/hotel.de_res.findlocalhotels.com/res.findlocalhotels.com.xml');
INSERT INTO `schemata` VALUES ('59', 'hotels.securebooking.org', '1', null, 'schema/hotels.securebooking.org_www.ichotelsgroup.com/hotels.securebooking.org.xml');
INSERT INTO `schemata` VALUES ('60', 'www.ichotelsgroup.com', '1', null, 'schema/hotels.securebooking.org_www.ichotelsgroup.com/www.ichotelsgroup.com.xml');
INSERT INTO `schemata` VALUES ('61', 'edit.yahoo.com', '1', null, 'schema/hotmail.passport.net.xml_edit.yahoo.com.xml_EXACT/edit.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('62', 'hotmail.passport.net', '1', null, 'schema/hotmail.passport.net.xml_edit.yahoo.com.xml_EXACT/hotmail.passport.net.xml');
INSERT INTO `schemata` VALUES ('63', 'frisco-colorado.where-to-stay.com', '1', null, 'schema/info.hotelsvalued.com_frisco-colorado.where-to-stay.com/frisco-colorado.where-to-stay.com.xml');
INSERT INTO `schemata` VALUES ('64', 'info.hotelsvalued.com', '1', null, 'schema/info.hotelsvalued.com_frisco-colorado.where-to-stay.com/info.hotelsvalued.com.xml');
INSERT INTO `schemata` VALUES ('65', 'kaxy', '1', null, 'schema/kaxy.xml_www.cashette.com.xml_EXACT/kaxy.xml');
INSERT INTO `schemata` VALUES ('66', 'www.cashette.com', '1', null, 'schema/kaxy.xml_www.cashette.com.xml_EXACT/www.cashette.com.xml');
INSERT INTO `schemata` VALUES ('67', 'hotmail.passport.net', '1', null, 'schema/ldbreg.lycos.com.xml_hotmail.passport.net.xml_EXACT/hotmail.passport.net.xml');
INSERT INTO `schemata` VALUES ('68', 'ldbreg.lycos.com', '1', null, 'schema/ldbreg.lycos.com.xml_hotmail.passport.net.xml_EXACT/ldbreg.lycos.com.xml');
INSERT INTO `schemata` VALUES ('69', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_secure.sportsinteraction.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('70', 'secure.sportsinteraction.com', '1', null, 'schema/linesmaker.com.xml_secure.sportsinteraction.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('71', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_wp.eurobet.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('72', 'wp.eurobet.com', '1', null, 'schema/linesmaker.com.xml_wp.eurobet.com.xml_EXACT/wp.eurobet.com.xml');
INSERT INTO `schemata` VALUES ('73', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.bet-at-home.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('74', 'www.bet-at-home.com', '1', null, 'schema/linesmaker.com.xml_www.bet-at-home.com.xml_EXACT/www.bet-at-home.com.xml');
INSERT INTO `schemata` VALUES ('75', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.betdirect.net.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('76', 'www.betdirect.net', '1', null, 'schema/linesmaker.com.xml_www.betdirect.net.xml_EXACT/www.betdirect.net.xml');
INSERT INTO `schemata` VALUES ('77', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.bettingexpress.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('78', 'www.bettingexpress.com', '1', null, 'schema/linesmaker.com.xml_www.bettingexpress.com.xml_EXACT/www.bettingexpress.com.xml');
INSERT INTO `schemata` VALUES ('79', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.cybersportsbook.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('80', 'www.cybersportsbook.com', '1', null, 'schema/linesmaker.com.xml_www.cybersportsbook.com.xml_EXACT/www.cybersportsbook.com.xml');
INSERT INTO `schemata` VALUES ('81', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.gonegambling.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('82', 'www.gonegambling.com', '1', null, 'schema/linesmaker.com.xml_www.gonegambling.com.xml_EXACT/www.gonegambling.com.xml');
INSERT INTO `schemata` VALUES ('83', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.vegas-sportsbetting.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('84', 'www.vegas-sportsbetting.com', '1', null, 'schema/linesmaker.com.xml_www.vegas-sportsbetting.com.xml_EXACT/www.vegas-sportsbetting.com.xml');
INSERT INTO `schemata` VALUES ('85', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.willhill.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('86', 'www.willhill.com', '1', null, 'schema/linesmaker.com.xml_www.willhill.com.xml_EXACT/www.willhill.com.xml');
INSERT INTO `schemata` VALUES ('87', 'linesmaker.com', '1', null, 'schema/linesmaker.com.xml_www.youbet.com.xml_EXACT/linesmaker.com.xml');
INSERT INTO `schemata` VALUES ('88', 'www.youbet.com', '1', null, 'schema/linesmaker.com.xml_www.youbet.com.xml_EXACT/www.youbet.com.xml');
INSERT INTO `schemata` VALUES ('89', 'lonelyplanet', '1', null, 'schema/lonelyplanet.xml_www.klm.com.xml_EXACT/lonelyplanet.xml');
INSERT INTO `schemata` VALUES ('90', 'www.klm.com', '1', null, 'schema/lonelyplanet.xml_www.klm.com.xml_EXACT/www.klm.com.xml');
INSERT INTO `schemata` VALUES ('91', 'lonelyplanet', '1', null, 'schema/lonelyplanet.xml_www.ryanair.com.xml_EXACT/lonelyplanet.xml');
INSERT INTO `schemata` VALUES ('92', 'www.ryanair.com', '1', null, 'schema/lonelyplanet.xml_www.ryanair.com.xml_EXACT/www.ryanair.com.xml');
INSERT INTO `schemata` VALUES ('93', 'MevoJerusalem', '1', null, 'schema/MevoJerusalem.xml_NeptuneEilat.xml_EXACT/MevoJerusalem.xml');
INSERT INTO `schemata` VALUES ('94', 'NeptuneEilat', '1', null, 'schema/MevoJerusalem.xml_NeptuneEilat.xml_EXACT/NeptuneEilat.xml');
INSERT INTO `schemata` VALUES ('95', 'moneycentral.msn.com', '1', null, 'schema/moneycentral.msn.com-screen.morningstar.com/moneycentral.msn.com.xml');
INSERT INTO `schemata` VALUES ('96', 'screen.morningstar.com', '1', null, 'schema/moneycentral.msn.com-screen.morningstar.com/screen.morningstar.com.xml');
INSERT INTO `schemata` VALUES ('97', 'OrchidEilat', '1', null, 'schema/OrchidEilat.xml_www.royal-plaza.co.il.xml_EXACT/OrchidEilat.xml');
INSERT INTO `schemata` VALUES ('98', 'www.royal-plaza.co.il', '1', null, 'schema/OrchidEilat.xml_www.royal-plaza.co.il.xml_EXACT/www.royal-plaza.co.il.xml');
INSERT INTO `schemata` VALUES ('99', 'passport.care2.net', '1', null, 'schema/passport.care2.net.xml_rapids.canoe.ca.xml_EXACT/passport.care2.net.xml');
INSERT INTO `schemata` VALUES ('100', 'rapids.canoe.ca', '1', null, 'schema/passport.care2.net.xml_rapids.canoe.ca.xml_EXACT/rapids.canoe.ca.xml');
INSERT INTO `schemata` VALUES ('101', 'passport.care2.net', '1', null, 'schema/passport.care2.net.xml_registration.excite.com.xml_EXACT/passport.care2.net.xml');
INSERT INTO `schemata` VALUES ('102', 'registration.excite.com', '1', null, 'schema/passport.care2.net.xml_registration.excite.com.xml_EXACT/registration.excite.com.xml');
INSERT INTO `schemata` VALUES ('103', 'moneycentral.msn.com', '1', null, 'schema/prosearch.businessweek.com-moneycentral.msn.com/moneycentral.msn.com.xml');
INSERT INTO `schemata` VALUES ('104', 'prosearch.businessweek.com', '1', null, 'schema/prosearch.businessweek.com-moneycentral.msn.com/prosearch.businessweek.com.xml');
INSERT INTO `schemata` VALUES ('105', 'rapids.canoe.ca', '1', null, 'schema/rapids.canoe.ca.xml_www.fusemail.com.xml_EXACT/rapids.canoe.ca.xml');
INSERT INTO `schemata` VALUES ('106', 'www.fusemail.com', '1', null, 'schema/rapids.canoe.ca.xml_www.fusemail.com.xml_EXACT/www.fusemail.com.xml');
INSERT INTO `schemata` VALUES ('107', 'registration.excite.com', '1', null, 'schema/registration.excite.com.xml_www.cashette.com.xml_EXACT/registration.excite.com.xml');
INSERT INTO `schemata` VALUES ('108', 'www.cashette.com', '1', null, 'schema/registration.excite.com.xml_www.cashette.com.xml_EXACT/www.cashette.com.xml');
INSERT INTO `schemata` VALUES ('109', 'finance.aol.com', '1', null, 'schema/screen.finance.yahoo.com-finance.aol.com/finance.aol.com.xml');
INSERT INTO `schemata` VALUES ('110', 'screen.finance.yahoo.com', '1', null, 'schema/screen.finance.yahoo.com-finance.aol.com/screen.finance.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('111', 'search.abcnews.go.com', '1', null, 'schema/search.abcnews.go.com.xml_www.washingtonpost.com.xml_EXACT/search.abcnews.go.com.xml');
INSERT INTO `schemata` VALUES ('112', 'www.washingtonpost.com', '1', null, 'schema/search.abcnews.go.com.xml_www.washingtonpost.com.xml_EXACT/www.washingtonpost.com.xml');
INSERT INTO `schemata` VALUES ('113', 'search.scotsman.com', '1', null, 'schema/search.scotsman.com.xml_search.sky.com.xml_EXACT/search.scotsman.com.xml');
INSERT INTO `schemata` VALUES ('114', 'search.sky.com', '1', null, 'schema/search.scotsman.com.xml_search.sky.com.xml_EXACT/search.sky.com.xml');
INSERT INTO `schemata` VALUES ('115', 'secure.kalmbach.com', '1', null, 'schema/secure.kalmbach.com.xml_www.kable.com.xml_EXACT/secure.kalmbach.com.xml');
INSERT INTO `schemata` VALUES ('116', 'www.kable.com', '1', null, 'schema/secure.kalmbach.com.xml_www.kable.com.xml_EXACT/www.kable.com.xml');
INSERT INTO `schemata` VALUES ('117', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_wp.eurobet.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('118', 'wp.eurobet.com', '1', null, 'schema/secure.sportsinteraction.com.xml_wp.eurobet.com.xml_EXACT/wp.eurobet.com.xml');
INSERT INTO `schemata` VALUES ('119', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.bet-at-home.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('120', 'www.bet-at-home.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.bet-at-home.com.xml_EXACT/www.bet-at-home.com.xml');
INSERT INTO `schemata` VALUES ('121', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.betdirect.net.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('122', 'www.betdirect.net', '1', null, 'schema/secure.sportsinteraction.com.xml_www.betdirect.net.xml_EXACT/www.betdirect.net.xml');
INSERT INTO `schemata` VALUES ('123', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.bettingexpress.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('124', 'www.bettingexpress.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.bettingexpress.com.xml_EXACT/www.bettingexpress.com.xml');
INSERT INTO `schemata` VALUES ('125', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.cybersportsbook.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('126', 'www.cybersportsbook.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.cybersportsbook.com.xml_EXACT/www.cybersportsbook.com.xml');
INSERT INTO `schemata` VALUES ('127', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.gonegambling.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('128', 'www.gonegambling.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.gonegambling.com.xml_EXACT/www.gonegambling.com.xml');
INSERT INTO `schemata` VALUES ('129', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.vegas-sportsbetting.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('130', 'www.vegas-sportsbetting.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.vegas-sportsbetting.com.xml_EXACT/www.vegas-sportsbetting.com.xml');
INSERT INTO `schemata` VALUES ('131', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.willhill.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('132', 'www.willhill.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.willhill.com.xml_EXACT/www.willhill.com.xml');
INSERT INTO `schemata` VALUES ('133', 'secure.sportsinteraction.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.youbet.com.xml_EXACT/secure.sportsinteraction.com.xml');
INSERT INTO `schemata` VALUES ('134', 'www.youbet.com', '1', null, 'schema/secure.sportsinteraction.com.xml_www.youbet.com.xml_EXACT/www.youbet.com.xml');
INSERT INTO `schemata` VALUES ('135', 'securewn.com', '1', null, 'schema/securewn.com.xml_www.eldan.co.il.xml_EXACT/securewn.com.xml');
INSERT INTO `schemata` VALUES ('136', 'www.eldan.co.il', '1', null, 'schema/securewn.com.xml_www.eldan.co.il.xml_EXACT/www.eldan.co.il.xml');
INSERT INTO `schemata` VALUES ('137', 'shop.sae.org', '1', null, 'schema/shop.sae.org.xml_www.knag.nl.xml_EXACT/shop.sae.org.xml');
INSERT INTO `schemata` VALUES ('138', 'www.knag.nl', '1', null, 'schema/shop.sae.org.xml_www.knag.nl.xml_EXACT/www.knag.nl.xml');
INSERT INTO `schemata` VALUES ('139', 'subscribe.americancityandcounty.com', '1', null, 'schema/subscribe.americancityandcounty.com.xml_www.nightclub.com.xml_EXACT/subscribe.americancityandcounty.com.xml');
INSERT INTO `schemata` VALUES ('140', 'www.nightclub.com', '1', null, 'schema/subscribe.americancityandcounty.com.xml_www.nightclub.com.xml_EXACT/www.nightclub.com.xml');
INSERT INTO `schemata` VALUES ('141', 'taut.securesites.com', '1', null, 'schema/taut.securesites.co.xml_www1522.boca15-verio.co1.xml_EXACT/taut.securesites.com.xml');
INSERT INTO `schemata` VALUES ('142', 'www1522.boca15-verio.com', '1', null, 'schema/taut.securesites.co.xml_www1522.boca15-verio.co1.xml_EXACT/www1522.boca15-verio.com.xml');
INSERT INTO `schemata` VALUES ('143', 'travelwww.aexp.be', '1', null, 'schema/travelwww.aexp.be.xml_www.experienced-people.co.uk.xml_EXACT/travelwww.aexp.be.xml');
INSERT INTO `schemata` VALUES ('144', 'www.experienced-people.co.uk', '1', null, 'schema/travelwww.aexp.be.xml_www.experienced-people.co.uk.xml_EXACT/www.experienced-people.co.uk.xml');
INSERT INTO `schemata` VALUES ('145', 'twinkletoesbabyshoes.com', '1', null, 'schema/twinkletoesbabyshoes.com.xml_www.hilohattie.com.xml_EXACT/twinkletoesbabyshoes.com.xml');
INSERT INTO `schemata` VALUES ('146', 'www.hilohattie.com', '1', null, 'schema/twinkletoesbabyshoes.com.xml_www.hilohattie.com.xml_EXACT/www.hilohattie.com.xml');
INSERT INTO `schemata` VALUES ('147', 'wbln0018.worldbank.org', '1', null, 'schema/wbln0018.worldbank.org.xml_www.gravesham.gov.uk.xml_EXACT/wbln0018.worldbank.org.xml');
INSERT INTO `schemata` VALUES ('148', 'www.gravesham.gov.uk', '1', null, 'schema/wbln0018.worldbank.org.xml_www.gravesham.gov.uk.xml_EXACT/www.gravesham.gov.uk.xml');
INSERT INTO `schemata` VALUES ('149', 'webcenter.love.match.netscape.com', '1', null, 'schema/webcenter.love.match.netscape.com.xml_www.date.com.xml_EXACT/webcenter.love.match.netscape.com.xml');
INSERT INTO `schemata` VALUES ('150', 'www.date.com', '1', null, 'schema/webcenter.love.match.netscape.com.xml_www.date.com.xml_EXACT/www.date.com.xml');
INSERT INTO `schemata` VALUES ('151', 'webcenter.love.match.netscape.com', '1', null, 'schema/webcenter.love.match.netscape.com.xml_www.lovetomeetyou.com.xml_EXACT/webcenter.love.match.netscape.com.xml');
INSERT INTO `schemata` VALUES ('152', 'www.lovetomeetyou.com', '1', null, 'schema/webcenter.love.match.netscape.com.xml_www.lovetomeetyou.com.xml_EXACT/www.lovetomeetyou.com.xml');
INSERT INTO `schemata` VALUES ('153', 'webmail.co.za', '1', null, 'schema/webmail.co.za.xml_www.marchmail.com.xml_EXACT/webmail.co.za.xml');
INSERT INTO `schemata` VALUES ('154', 'www.marchmail.com', '1', null, 'schema/webmail.co.za.xml_www.marchmail.com.xml_EXACT/www.marchmail.com.xml');
INSERT INTO `schemata` VALUES ('155', 'wp.eurobet.com', '1', null, 'schema/wp.eurobet.com.xml_www.bet-at-home.com.xml_EXACT/wp.eurobet.com.xml');
INSERT INTO `schemata` VALUES ('156', 'www.bet-at-home.com', '1', null, 'schema/wp.eurobet.com.xml_www.bet-at-home.com.xml_EXACT/www.bet-at-home.com.xml');
INSERT INTO `schemata` VALUES ('157', 'www.amazon.com', '1', null, 'schema/www.amazon.com.xml_www.audible.com.xml_EXACT/www.amazon.com.xml');
INSERT INTO `schemata` VALUES ('158', 'www.audible.com', '1', null, 'schema/www.amazon.com.xml_www.audible.com.xml_EXACT/www.audible.com.xml');
INSERT INTO `schemata` VALUES ('159', 'www.amazon.com', '1', null, 'schema/www.amazon.com.xml_www.powells.com.xml_EXACT/www.amazon.com.xml');
INSERT INTO `schemata` VALUES ('160', 'www.powells.com', '1', null, 'schema/www.amazon.com.xml_www.powells.com.xml_EXACT/www.powells.com.xml');
INSERT INTO `schemata` VALUES ('161', 'www.amerciansingles.com', '1', null, 'schema/www.amerciansingles.com.xml_www.datemeister.com.xml_EXACT/www.amerciansingles.com.xml');
INSERT INTO `schemata` VALUES ('162', 'www.datemeister.com', '1', null, 'schema/www.amerciansingles.com.xml_www.datemeister.com.xml_EXACT/www.datemeister.com.xml');
INSERT INTO `schemata` VALUES ('163', 'www.americanairlines.com', '1', null, 'schema/www.americanairlines.com.xml_www.continental.com.xml_EXACT/www.americanairlines.com.xml');
INSERT INTO `schemata` VALUES ('164', 'www.continental.com', '1', null, 'schema/www.americanairlines.com.xml_www.continental.com.xml_EXACT/www.continental.com.xml');
INSERT INTO `schemata` VALUES ('165', 'www.another.com', '1', null, 'schema/www.another.com.xml_www.topica.com.xml_EXACT/www.another.com.xml');
INSERT INTO `schemata` VALUES ('166', 'www.topica.com', '1', null, 'schema/www.another.com.xml_www.topica.com.xml_EXACT/www.topica.com.xml');
INSERT INTO `schemata` VALUES ('167', 'www.arabia.com', '1', null, 'schema/www.arabia.com.xml_www.dbzmail.com.xml_EXACT/www.arabia.com.xml');
INSERT INTO `schemata` VALUES ('168', 'www.dbzmail.com', '1', null, 'schema/www.arabia.com.xml_www.dbzmail.com.xml_EXACT/www.dbzmail.com.xml');
INSERT INTO `schemata` VALUES ('169', 'www.atlastwemeet.com', '1', null, 'schema/www.atlastwemeet.com.xml_www.ezboard.com.xml_EXACT/www.atlastwemeet.com.xml');
INSERT INTO `schemata` VALUES ('170', 'www.ezboard.com', '1', null, 'schema/www.atlastwemeet.com.xml_www.ezboard.com.xml_EXACT/www.ezboard.com.xml');
INSERT INTO `schemata` VALUES ('171', 'www.audible.com', '1', null, 'schema/www.audible.com.xml_www.sagebrushcorp.com.xml_EXACT/www.audible.com.xml');
INSERT INTO `schemata` VALUES ('172', 'www.sagebrushcorp.com', '1', null, 'schema/www.audible.com.xml_www.sagebrushcorp.com.xml_EXACT/www.sagebrushcorp.com.xml');
INSERT INTO `schemata` VALUES ('173', 'www.autoeurope.co.il', '1', null, 'schema/www.autoeurope.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.autoeurope.co.il.xml');
INSERT INTO `schemata` VALUES ('174', 'www.rentdirect.co.il', '1', null, 'schema/www.autoeurope.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.rentdirect.co.il.xml');
INSERT INTO `schemata` VALUES ('175', 'www.bbltamex.com', '1', null, 'schema/www.bbltamex.com.xml_www.experienced-people.co.uk.xml_EXACT/www.bbltamex.com.xml');
INSERT INTO `schemata` VALUES ('176', 'www.experienced-people.co.uk', '1', null, 'schema/www.bbltamex.com.xml_www.experienced-people.co.uk.xml_EXACT/www.experienced-people.co.uk.xml');
INSERT INTO `schemata` VALUES ('177', 'www.besthotel.com', '1', null, 'schema/www.besthotel.com_www.secure-reservations.net/www.besthotel.com.xml');
INSERT INTO `schemata` VALUES ('178', 'www.secure-reservations.net', '1', null, 'schema/www.besthotel.com_www.secure-reservations.net/www.secure-reservations.net.xml');
INSERT INTO `schemata` VALUES ('179', 'www.bestindianhotels.com', '1', null, 'schema/www.bestindianhotels.com.xml_www.hotelclub.net.xml_EXACT/www.bestindianhotels.com.xml');
INSERT INTO `schemata` VALUES ('180', 'www.hotelclub.net', '1', null, 'schema/www.bestindianhotels.com.xml_www.hotelclub.net.xml_EXACT/www.hotelclub.net.xml');
INSERT INTO `schemata` VALUES ('181', 'www.bet-at-home.com', '1', null, 'schema/www.bet-at-home.com.xml_www.youbet.com.xml_EXACT/www.bet-at-home.com.xml');
INSERT INTO `schemata` VALUES ('182', 'www.youbet.com', '1', null, 'schema/www.bet-at-home.com.xml_www.youbet.com.xml_EXACT/www.youbet.com.xml');
INSERT INTO `schemata` VALUES ('183', 'wp.eurobet.com', '1', null, 'schema/www.betdirect.net.xml_wp.eurobet.com.xml_EXACT/wp.eurobet.com.xml');
INSERT INTO `schemata` VALUES ('184', 'www.betdirect.net', '1', null, 'schema/www.betdirect.net.xml_wp.eurobet.com.xml_EXACT/www.betdirect.net.xml');
INSERT INTO `schemata` VALUES ('185', 'www.boardermail.com', '1', null, 'schema/www.boardermail.com.xml_www.linuxmail.com.xml_EXACT/www.boardermail.com.xml');
INSERT INTO `schemata` VALUES ('186', 'www.linuxmail.com', '1', null, 'schema/www.boardermail.com.xml_www.linuxmail.com.xml_EXACT/www.linuxmail.com.xml');
INSERT INTO `schemata` VALUES ('187', 'ldbreg.lycos.com', '1', null, 'schema/www.bravenet.com.xml_ldbreg.lycos.com.xml_EXACT/ldbreg.lycos.com.xml');
INSERT INTO `schemata` VALUES ('188', 'www.bravenet.com', '1', null, 'schema/www.bravenet.com.xml_ldbreg.lycos.com.xml_EXACT/www.bravenet.com.xml');
INSERT INTO `schemata` VALUES ('189', 'www.britishairways.com', '1', null, 'schema/www.britishairways.com.xml_www.thy.com.xml_EXACT/www.britishairways.com.xml');
INSERT INTO `schemata` VALUES ('190', 'www.thy.com', '1', null, 'schema/www.britishairways.com.xml_www.thy.com.xml_EXACT/www.thy.com.xml');
INSERT INTO `schemata` VALUES ('191', 'www.britishairways.com', '1', null, 'schema/www.britishairways.com.xml_www.ual.com.xml_EXACT/www.britishairways.com.xml');
INSERT INTO `schemata` VALUES ('192', 'www.ual.com', '1', null, 'schema/www.britishairways.com.xml_www.ual.com.xml_EXACT/www.ual.com.xml');
INSERT INTO `schemata` VALUES ('193', 'www.budget.co.il', '1', null, 'schema/www.budget.co.il.xml_www.eldan.co.il.xml_EXACT/www.budget.co.il.xml');
INSERT INTO `schemata` VALUES ('194', 'www.eldan.co.il', '1', null, 'schema/www.budget.co.il.xml_www.eldan.co.il.xml_EXACT/www.eldan.co.il.xml');
INSERT INTO `schemata` VALUES ('195', 'accountservices.passport.net', '1', null, 'schema/www.cashette.com.xml_accountservices.passport.net.xml_EXACT/accountservices.passport.net.xml');
INSERT INTO `schemata` VALUES ('196', 'www.cashette.com', '1', null, 'schema/www.cashette.com.xml_accountservices.passport.net.xml_EXACT/www.cashette.com.xml');
INSERT INTO `schemata` VALUES ('197', 'login.myspace.com', '1', null, 'schema/www.cashette.com.xml_login.myspace.com.xml_EXACT/login.myspace.com.xml');
INSERT INTO `schemata` VALUES ('198', 'www.cashette.com', '1', null, 'schema/www.cashette.com.xml_login.myspace.com.xml_EXACT/www.cashette.com.xml');
INSERT INTO `schemata` VALUES ('199', 'www.cashette.com', '1', null, 'schema/www.cashette.com.xml_www2.inmail24.com.xml_EXACT/www.cashette.com.xml');
INSERT INTO `schemata` VALUES ('200', 'www2.inmail24.com', '1', null, 'schema/www.cashette.com.xml_www2.inmail24.com.xml_EXACT/www2.inmail24.com.xml');
INSERT INTO `schemata` VALUES ('201', 'www.coupleme.com', '1', null, 'schema/www.coupleme.com.xml_www.loveaccess.com.xml_EXACT/www.coupleme.com.xml');
INSERT INTO `schemata` VALUES ('202', 'www.loveaccess.com', '1', null, 'schema/www.coupleme.com.xml_www.loveaccess.com.xml_EXACT/www.loveaccess.com.xml');
INSERT INTO `schemata` VALUES ('203', 'www.coupleme.com', '1', null, 'schema/www.coupleme.com.xml_www.singleme.com.xml_EXACT/www.coupleme.com.xml');
INSERT INTO `schemata` VALUES ('204', 'www.singleme.com', '1', null, 'schema/www.coupleme.com.xml_www.singleme.com.xml_EXACT/www.singleme.com.xml');
INSERT INTO `schemata` VALUES ('205', 'www.couponforum.com', '1', null, 'schema/www.couponforum.com.xml_www.ghchealth.com.xml_EXACT/www.couponforum.com.xml');
INSERT INTO `schemata` VALUES ('206', 'www.ghchealth.com', '1', null, 'schema/www.couponforum.com.xml_www.ghchealth.com.xml_EXACT/www.ghchealth.com.xml');
INSERT INTO `schemata` VALUES ('207', 'www.cupidusa.com', '1', null, 'schema/www.cupidusa.com.xml_www.jdate.com.xml_EXACT/www.cupidusa.com.xml');
INSERT INTO `schemata` VALUES ('208', 'www.jdate.com', '1', null, 'schema/www.cupidusa.com.xml_www.jdate.com.xml_EXACT/www.jdate.com.xml');
INSERT INTO `schemata` VALUES ('209', 'www.bettingexpress.com', '1', null, 'schema/www.cybersportsbook.com.xml_www.bettingexpress.com.xml_EXACT/www.bettingexpress.com.xml');
INSERT INTO `schemata` VALUES ('210', 'www.cybersportsbook.com', '1', null, 'schema/www.cybersportsbook.com.xml_www.bettingexpress.com.xml_EXACT/www.cybersportsbook.com.xml');
INSERT INTO `schemata` VALUES ('211', 'www.cybersuitors.com', '1', null, 'schema/www.cybersuitors.com.xml_www.date.com.xml_EXACT.xml/www.cybersuitors.com.xml');
INSERT INTO `schemata` VALUES ('212', 'www.date.com', '1', null, 'schema/www.cybersuitors.com.xml_www.date.com.xml_EXACT.xml/www.date.com.xml');
INSERT INTO `schemata` VALUES ('213', 'www.debtbusterloans.com', '1', null, 'schema/www.debtbusterloans.com.xml_www.doubleclickloans.co.uk.xml_EXACT/www.debtbusterloans.com.xml');
INSERT INTO `schemata` VALUES ('214', 'www.doubleclickloans.co.uk', '1', null, 'schema/www.debtbusterloans.com.xml_www.doubleclickloans.co.uk.xml_EXACT/www.doubleclickloans.co.uk.xml');
INSERT INTO `schemata` VALUES ('215', 'www.delta.com', '1', null, 'schema/www.delta.com.xml_www.elal.co.il.xml_EXACT/www.delta.com.xml');
INSERT INTO `schemata` VALUES ('216', 'www.elal.co.il', '1', null, 'schema/www.delta.com.xml_www.elal.co.il.xml_EXACT/www.elal.co.il.xml');
INSERT INTO `schemata` VALUES ('217', 'www.delta.com', '1', null, 'schema/www.delta.com.xml_www.ual.com.xml_EXACT/www.delta.com.xml');
INSERT INTO `schemata` VALUES ('218', 'www.ual.com', '1', null, 'schema/www.delta.com.xml_www.ual.com.xml_EXACT/www.ual.com.xml');
INSERT INTO `schemata` VALUES ('219', 'screen.finance.yahoo.com', '1', null, 'schema/www.digitallook.com-screen.finance.yahoo.com/screen.finance.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('220', 'www.digitallook.com', '1', null, 'schema/www.digitallook.com-screen.finance.yahoo.com/www.digitallook.com.xml');
INSERT INTO `schemata` VALUES ('221', 'www.chemistry.com', '1', null, 'schema/www.dream-dating.org.xml_www.chemistry.com.xml_EXACT/www.chemistry.com.xml');
INSERT INTO `schemata` VALUES ('222', 'www.dream-dating.org', '1', null, 'schema/www.dream-dating.org.xml_www.chemistry.com.xml_EXACT/www.dream-dating.org.xml');
INSERT INTO `schemata` VALUES ('223', 'www.dream-dating.org', '1', null, 'schema/www.dream-dating.org.xml_www.matchmaker.com.xml_EXACT/www.dream-dating.org.xml');
INSERT INTO `schemata` VALUES ('224', 'www.matchmaker.com', '1', null, 'schema/www.dream-dating.org.xml_www.matchmaker.com.xml_EXACT/www.matchmaker.com.xml');
INSERT INTO `schemata` VALUES ('225', 'www.dream-dating.org', '1', null, 'schema/www.dream-dating.org.xml_www.singleme.com.xml_EXACT/www.dream-dating.org.xml');
INSERT INTO `schemata` VALUES ('226', 'www.singleme.com', '1', null, 'schema/www.dream-dating.org.xml_www.singleme.com.xml_EXACT/www.singleme.com.xml');
INSERT INTO `schemata` VALUES ('227', 'www.elal.co.il', '1', null, 'schema/www.elal.co.il.xml_www.thy.com.xml_EXACT/www.elal.co.il.xml');
INSERT INTO `schemata` VALUES ('228', 'www.thy.com', '1', null, 'schema/www.elal.co.il.xml_www.thy.com.xml_EXACT/www.thy.com.xml');
INSERT INTO `schemata` VALUES ('229', 'www.enriqueiglesias.com', '1', null, 'schema/www.enriqueiglesias.com.xml_www.yanni.com.xml_EXACT/www.enriqueiglesias.com.xml');
INSERT INTO `schemata` VALUES ('230', 'www.yanni.com', '1', null, 'schema/www.enriqueiglesias.com.xml_www.yanni.com.xml_EXACT/www.yanni.com.xml');
INSERT INTO `schemata` VALUES ('231', 'www.eservus.com', '1', null, 'schema/www.eservus.com.xml_www.theatremania.org.xml_EXACT/www.eservus.com.xml');
INSERT INTO `schemata` VALUES ('232', 'www.theatremania.org', '1', null, 'schema/www.eservus.com.xml_www.theatremania.org.xml_EXACT/www.theatremania.org.xml');
INSERT INTO `schemata` VALUES ('233', 'www.financial-securesite.com', '1', null, 'schema/www.firstmeritib.com.xml_www.financial-securesite.com.xml_EXACT/www.financial-securesite.com.xml');
INSERT INTO `schemata` VALUES ('234', 'www.firstmeritib.com', '1', null, 'schema/www.firstmeritib.com.xml_www.financial-securesite.com.xml_EXACT/www.firstmeritib.com.xml');
INSERT INTO `schemata` VALUES ('235', 'www.firstmeritib.com', '1', null, 'schema/www.firstmeritib.com.xml_www.moneymart.ca.xml_EXACT/www.firstmeritib.com.xml');
INSERT INTO `schemata` VALUES ('236', 'www.moneymart.ca', '1', null, 'schema/www.firstmeritib.com.xml_www.moneymart.ca.xml_EXACT/www.moneymart.ca.xml');
INSERT INTO `schemata` VALUES ('237', 'www.frbatlanta.org', '1', null, 'schema/www.frbatlanta.org.xml_www.state.il.us.xml_EXACT/www.frbatlanta.org.xml');
INSERT INTO `schemata` VALUES ('238', 'www.state.il.us', '1', null, 'schema/www.frbatlanta.org.xml_www.state.il.us.xml_EXACT/www.state.il.us.xml');
INSERT INTO `schemata` VALUES ('239', 'login.myspace.com', '1', null, 'schema/www.fusemail.com.xml_login.myspace.com.xml_EXACT/login.myspace.com.xml');
INSERT INTO `schemata` VALUES ('240', 'www.fusemail.com', '1', null, 'schema/www.fusemail.com.xml_login.myspace.com.xml_EXACT/www.fusemail.com.xml');
INSERT INTO `schemata` VALUES ('241', 'finance.aol.com', '1', null, 'schema/www.globeinvestor.com-finance.aol.com/finance.aol.com.xml');
INSERT INTO `schemata` VALUES ('242', 'www.globeinvestor.com', '1', null, 'schema/www.globeinvestor.com-finance.aol.com/www.globeinvestor.com.xml');
INSERT INTO `schemata` VALUES ('243', 'www.gonegambling.com', '1', null, 'schema/www.gonegambling.com.xml_www.willhill.com.xml_EXACT/www.gonegambling.com.xml');
INSERT INTO `schemata` VALUES ('244', 'www.willhill.com', '1', null, 'schema/www.gonegambling.com.xml_www.willhill.com.xml_EXACT/www.willhill.com.xml');
INSERT INTO `schemata` VALUES ('245', 'www.google.com', '1', null, 'schema/www.google.com.xml_www.yahoo.com.xml_EXACT/www.google.com.xml');
INSERT INTO `schemata` VALUES ('246', 'www.yahoo.com', '1', null, 'schema/www.google.com.xml_www.yahoo.com.xml_EXACT/www.yahoo.com.xml');
INSERT INTO `schemata` VALUES ('247', 'www.hertz.co.il', '1', null, 'schema/www.hertz.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.hertz.co.il.xml');
INSERT INTO `schemata` VALUES ('248', 'www.rentdirect.co.il', '1', null, 'schema/www.hertz.co.il.xml_www.rentdirect.co.il.xml_EXACT/www.rentdirect.co.il.xml');
INSERT INTO `schemata` VALUES ('249', 'www.italyhotelink.com', '1', null, 'schema/www.italyhotelink.com.xml_www.venere.com.xml_EXACT/www.italyhotelink.com.xml');
INSERT INTO `schemata` VALUES ('250', 'www.venere.com', '1', null, 'schema/www.italyhotelink.com.xml_www.venere.com.xml_EXACT/www.venere.com.xml');
INSERT INTO `schemata` VALUES ('251', 'www.kswiss.com', '1', null, 'schema/www.kswiss.com.xml_www.mgear.com.xml_EXACT/www.kswiss.com.xml');
INSERT INTO `schemata` VALUES ('252', 'www.mgear.com', '1', null, 'schema/www.kswiss.com.xml_www.mgear.com.xml_EXACT/www.mgear.com.xml');
INSERT INTO `schemata` VALUES ('253', 'www.klm.com', '1', null, 'schema/www.lastminute.com.xml_www.klm.com.xml_EXACT/www.klm.com.xml');
INSERT INTO `schemata` VALUES ('254', 'www.lastminute.com', '1', null, 'schema/www.lastminute.com.xml_www.klm.com.xml_EXACT/www.lastminute.com.xml');
INSERT INTO `schemata` VALUES ('255', 'www.financialaid.com', '1', null, 'schema/www.maineadvantage.com.xml_www.financialaid.com.xml_EXACT/www.financialaid.com.xml');
INSERT INTO `schemata` VALUES ('256', 'www.maineadvantage.com', '1', null, 'schema/www.maineadvantage.com.xml_www.financialaid.com.xml_EXACT/www.maineadvantage.com.xml');
INSERT INTO `schemata` VALUES ('257', 'www.globeinvestor.com', '1', null, 'schema/www.marketwatch.com-www.globeinvestor.com/www.globeinvestor.com.xml');
INSERT INTO `schemata` VALUES ('258', 'www.marketwatch.com', '1', null, 'schema/www.marketwatch.com-www.globeinvestor.com/www.marketwatch.com.xml');
INSERT INTO `schemata` VALUES ('259', 'www.miss-janet.com', '1', null, 'schema/www.miss-janet.com.xml_www.myreg.net.xml_EXACT/www.miss-janet.com.xml');
INSERT INTO `schemata` VALUES ('260', 'www.myreg.net', '1', null, 'schema/www.miss-janet.com.xml_www.myreg.net.xml_EXACT/www.myreg.net.xml');
INSERT INTO `schemata` VALUES ('261', 'www.moneymart.ca', '1', null, 'schema/www.moneymart.ca.xml_www.no-fax-loan.com.xml_EXACT/www.moneymart.ca.xml');
INSERT INTO `schemata` VALUES ('262', 'www.no-fax-loan.com', '1', null, 'schema/www.moneymart.ca.xml_www.no-fax-loan.com.xml_EXACT/www.no-fax-loan.com.xml');
INSERT INTO `schemata` VALUES ('263', 'www.hotellocators.com', '1', null, 'schema/www.motels.com.xml_www.hotellocators.com.xml_EXACT/www.hotellocators.com.xml');
INSERT INTO `schemata` VALUES ('264', 'www.motels.com', '1', null, 'schema/www.motels.com.xml_www.hotellocators.com.xml_EXACT/www.motels.com.xml');
INSERT INTO `schemata` VALUES ('265', 'www.myfunnymail.com', '1', null, 'schema/www.myfunnymail.co-www.postmaster.co.u1_EXACT/www.myfunnymail.com.xml');
INSERT INTO `schemata` VALUES ('266', 'www.postmaster.co.uk', '1', null, 'schema/www.myfunnymail.co-www.postmaster.co.u1_EXACT/www.postmaster.co.uk.xml');
INSERT INTO `schemata` VALUES ('267', 'magshop.co.nz', '1', null, 'schema/www.nationaldefensemagazine.org.xml_magshop.co.nz.xml_EXACT/magshop.co.nz.xml');
INSERT INTO `schemata` VALUES ('268', 'www.nationaldefensemagazine.org', '1', null, 'schema/www.nationaldefensemagazine.org.xml_magshop.co.nz.xml_EXACT/www.nationaldefensemagazine.org.xml');
INSERT INTO `schemata` VALUES ('269', 'www.bootsunlimited.com', '1', null, 'schema/www.nbwebexpress.com.xml_www.bootsunlimited.com.xml_EXACT/www.bootsunlimited.com.xml');
INSERT INTO `schemata` VALUES ('270', 'www.nbwebexpress.com', '1', null, 'schema/www.nbwebexpress.com.xml_www.bootsunlimited.com.xml_EXACT/www.nbwebexpress.com.xml');
INSERT INTO `schemata` VALUES ('271', 'www.netcheck.com', '1', null, 'schema/www.netcheck.com.xml_www.omisan.com.xml_EXACT/www.netcheck.com.xml');
INSERT INTO `schemata` VALUES ('272', 'www.omisan.com', '1', null, 'schema/www.netcheck.com.xml_www.omisan.com.xml_EXACT/www.omisan.com.xml');
INSERT INTO `schemata` VALUES ('273', 'applications.us.com', '1', null, 'schema/www.no-fax-loan.com.xml_applications.us.com.xml_EXACT/applications.us.com.xml');
INSERT INTO `schemata` VALUES ('274', 'www.no-fax-loan.com', '1', null, 'schema/www.no-fax-loan.com.xml_applications.us.com.xml_EXACT/www.no-fax-loan.com.xml');
INSERT INTO `schemata` VALUES ('275', 'www.alamo.co.il', '1', null, 'schema/www.ofran.com.xml_www.alamo.co.il.xml_EXACT/www.alamo.co.il.xml');
INSERT INTO `schemata` VALUES ('276', 'www.ofran.com', '1', null, 'schema/www.ofran.com.xml_www.alamo.co.il.xml_EXACT/www.ofran.com.xml');
INSERT INTO `schemata` VALUES ('277', 'www.besthotel.com', '1', null, 'schema/www.onlinereservationz.com_www.besthotel.com/www.besthotel.com.xml');
INSERT INTO `schemata` VALUES ('278', 'www.onlinereservationz.com', '1', null, 'schema/www.onlinereservationz.com_www.besthotel.com/www.onlinereservationz.com.xml');
INSERT INTO `schemata` VALUES ('279', 'www.omeda.com', '1', null, 'schema/www.opensystems-publishing.com.xml_www.omeda.com.xml_EXACT/www.omeda.com.xml');
INSERT INTO `schemata` VALUES ('280', 'www.opensystems-publishing.com', '1', null, 'schema/www.opensystems-publishing.com.xml_www.omeda.com.xml_EXACT/www.opensystems-publishing.com.xml');
INSERT INTO `schemata` VALUES ('281', 'www.filastore.com', '1', null, 'schema/www.paragonsports.com.xml_www.filastore.com.xml_EXACT/www.filastore.com.xml');
INSERT INTO `schemata` VALUES ('282', 'www.paragonsports.com', '1', null, 'schema/www.paragonsports.com.xml_www.filastore.com.xml_EXACT/www.paragonsports.com.xml');
INSERT INTO `schemata` VALUES ('283', 'www.powells.com', '1', null, 'schema/www.powells.com.xml_www.sagebrushcorp.com.xml_EXACT/www.powells.com.xml');
INSERT INTO `schemata` VALUES ('284', 'www.sagebrushcorp.com', '1', null, 'schema/www.powells.com.xml_www.sagebrushcorp.com.xml_EXACT/www.sagebrushcorp.com.xml');
INSERT INTO `schemata` VALUES ('285', 'www.orbitz.com', '1', null, 'schema/www.priceline.com.xml_www.orbitz.com.xml_EXACT/www.orbitz.com.xml');
INSERT INTO `schemata` VALUES ('286', 'www.priceline.com', '1', null, 'schema/www.priceline.com.xml_www.orbitz.com.xml_EXACT/www.priceline.com.xml');
INSERT INTO `schemata` VALUES ('287', 'www.associationmanager.co.uk', '1', null, 'schema/www.printsolutionsmag.com.xml_www.associationmanager.co.uk.xml_EXACT/www.associationmanager.co.uk.xml');
INSERT INTO `schemata` VALUES ('288', 'www.printsolutionsmag.com', '1', null, 'schema/www.printsolutionsmag.com.xml_www.associationmanager.co.uk.xml_EXACT/www.printsolutionsmag.com.xml');
INSERT INTO `schemata` VALUES ('289', 'www.clevelandfed.org', '1', null, 'schema/www.psc.state.al.us.xml_www.clevelandfed.org.xml_EXACT/www.clevelandfed.org.xml');
INSERT INTO `schemata` VALUES ('290', 'www.psc.state.al.us', '1', null, 'schema/www.psc.state.al.us.xml_www.clevelandfed.org.xml_EXACT/www.psc.state.al.us.xml');
INSERT INTO `schemata` VALUES ('291', 'www.mfc.premiumtv.co.uk', '1', null, 'schema/www.safc.com.xml_www.mfc.premiumtv.co.uk.xml_EXACT/www.mfc.premiumtv.co.uk.xml');
INSERT INTO `schemata` VALUES ('292', 'www.safc.com', '1', null, 'schema/www.safc.com.xml_www.mfc.premiumtv.co.uk.xml_EXACT/www.safc.com.xml');
INSERT INTO `schemata` VALUES ('293', 'www.northlandmarine.com', '1', null, 'schema/www.sportsmansguide.com.xml_www.northlandmarine.com.xml_EXACT/www.northlandmarine.com.xml');
INSERT INTO `schemata` VALUES ('294', 'www.sportsmansguide.com', '1', null, 'schema/www.sportsmansguide.com.xml_www.northlandmarine.com.xml_EXACT/www.sportsmansguide.com.xml');
INSERT INTO `schemata` VALUES ('295', 'secure2.steelers.com', '1', null, 'schema/www.stlouisrams.com.xml_secure2.steelers.com.xml_EXACT/secure2.steelers.com.xml');
INSERT INTO `schemata` VALUES ('296', 'www.stlouisrams.com', '1', null, 'schema/www.stlouisrams.com.xml_secure2.steelers.com.xml_EXACT/www.stlouisrams.com.xml');
INSERT INTO `schemata` VALUES ('297', 'svartifoss2.fcc.gov', '1', null, 'schema/www.tio.com.au.xml_svartifoss2.fcc.gov.xml_EXACT/svartifoss2.fcc.gov.xml');
INSERT INTO `schemata` VALUES ('298', 'www.tio.com.au', '1', null, 'schema/www.tio.com.au.xml_svartifoss2.fcc.gov.xml_EXACT/www.tio.com.au.xml');
INSERT INTO `schemata` VALUES ('299', 'www.thy.com', '1', null, 'schema/www.ual.com.xml_www.thy.com.xml_EXACT/www.thy.com.xml');
INSERT INTO `schemata` VALUES ('300', 'www.ual.com', '1', null, 'schema/www.ual.com.xml_www.thy.com.xml_EXACT/www.ual.com.xml');
INSERT INTO `schemata` VALUES ('301', 'www.syl.com', '1', null, 'schema/www.webdate.com.xml_www.syl.com.xml_EXACT/www.syl.com.xml');
INSERT INTO `schemata` VALUES ('302', 'www.webdate.com', '1', null, 'schema/www.webdate.com.xml_www.syl.com.xml_EXACT/www.webdate.com.xml');
INSERT INTO `schemata` VALUES ('303', 'www.ci.glendale.az.us', '1', null, 'schema/www2.cityofseattle.net.xml_www.ci.glendale.az.us.xml_EXACT/www.ci.glendale.az.us.xml');
INSERT INTO `schemata` VALUES ('304', 'www2.cityofseattle.net', '1', null, 'schema/www2.cityofseattle.net.xml_www.ci.glendale.az.us.xml_EXACT/www2.cityofseattle.net.xml');
INSERT INTO `schemata` VALUES ('305', 'Gmail', '1', null, 'schema/www2.inmail24.com.xml_Gmail.xml_EXACT/Gmail.xml');
INSERT INTO `schemata` VALUES ('306', 'www2.inmail24.com', '1', null, 'schema/www2.inmail24.com.xml_Gmail.xml_EXACT/www2.inmail24.com.xml');
INSERT INTO `schemata` VALUES ('307', '11thHourVacations', '2', null, 'schema/Airfares/11thHourVacations.xml');
INSERT INTO `schemata` VALUES ('308', 'Air Canada', '2', null, 'schema/Airfares/Air Canada.xml');
INSERT INTO `schemata` VALUES ('309', 'Air Europa', '2', null, 'schema/Airfares/Air Europa.xml');
INSERT INTO `schemata` VALUES ('310', 'Air North', '2', null, 'schema/Airfares/Air North.xml');
INSERT INTO `schemata` VALUES ('311', 'AirCharter.com', '2', null, 'schema/Airfares/AirCharter.com.xml');
INSERT INTO `schemata` VALUES ('312', 'AirIndian', '2', null, 'schema/Airfares/AirIndian.xml');
INSERT INTO `schemata` VALUES ('313', 'AirlineConsolidator.com', '2', null, 'schema/Airfares/AirlineConsolidator.com.xml');
INSERT INTO `schemata` VALUES ('314', 'Alaska Airline', '2', null, 'schema/Airfares/Alaska Airline.xml');
INSERT INTO `schemata` VALUES ('315', 'Aloha Airlines', '2', null, 'schema/Airfares/Aloha Airlines.xml');
INSERT INTO `schemata` VALUES ('316', 'American Airline', '2', null, 'schema/Airfares/American Airline.xml');
INSERT INTO `schemata` VALUES ('317', 'American West Airlines', '2', null, 'schema/Airfares/American West Airlines.xml');
INSERT INTO `schemata` VALUES ('318', 'Asiana Airlines', '2', null, 'schema/Airfares/Asiana Airlines.xml');
INSERT INTO `schemata` VALUES ('319', 'American Express', '2', null, 'schema/Airfares/American Express.xml');
INSERT INTO `schemata` VALUES ('320', 'BananaTravel.com', '2', null, 'schema/Airfares/BananaTravel.com.xml');
INSERT INTO `schemata` VALUES ('321', 'BargainTravel.com', '2', null, 'schema/Airfares/BargainTravel.com.xml');
INSERT INTO `schemata` VALUES ('322', 'biztravel.com', '2', null, 'schema/Airfares/biztravel.com.xml');
INSERT INTO `schemata` VALUES ('323', 'Cheap Airlines', '2', null, 'schema/Airfares/Cheap Airlines.xml');
INSERT INTO `schemata` VALUES ('324', 'CheapFares.com', '2', null, 'schema/Airfares/CheapFares.com.xml');
INSERT INTO `schemata` VALUES ('325', 'Continental Airlines', '2', null, 'schema/Airfares/Continental Airlines.xml');
INSERT INTO `schemata` VALUES ('326', 'Orbitz Flight', '2', null, 'schema/Airfares/Orbitz Flight.xml');
INSERT INTO `schemata` VALUES ('327', 'Delta Airlines', '2', null, 'schema/Airfares/Delta Airlines.xml');
INSERT INTO `schemata` VALUES ('328', 'expedia.com', '2', null, 'schema/Airfares/expedia.com.xml');
INSERT INTO `schemata` VALUES ('329', 'faremax.com', '2', null, 'schema/Airfares/faremax.com.xml');
INSERT INTO `schemata` VALUES ('330', 'Finnair.com', '2', null, 'schema/Airfares/Finnair.com.xml');
INSERT INTO `schemata` VALUES ('331', 'flights.com', '2', null, 'schema/Airfares/flights.com.xml');
INSERT INTO `schemata` VALUES ('332', 'France Airline', '2', null, 'schema/Airfares/France Airline.xml');
INSERT INTO `schemata` VALUES ('333', 'hotwire.com', '2', null, 'schema/Airfares/hotwire.com.xml');
INSERT INTO `schemata` VALUES ('334', 'Korean Airlines', '2', null, 'schema/Airfares/Korean Airlines.xml');
INSERT INTO `schemata` VALUES ('335', 'LowestFare.com', '2', null, 'schema/Airfares/LowestFare.com.xml');
INSERT INTO `schemata` VALUES ('336', 'NorthWest Airlines', '2', null, 'schema/Airfares/NorthWest Airlines.xml');
INSERT INTO `schemata` VALUES ('337', 'OneTravel.com', '2', null, 'schema/Airfares/OneTravel.com.xml');
INSERT INTO `schemata` VALUES ('338', 'Shopping-SuperSaver.com', '2', null, 'schema/Airfares/Shopping-SuperSaver.com.xml');
INSERT INTO `schemata` VALUES ('339', 'Singapore Airlines', '2', null, 'schema/Airfares/Singapore Airlines.xml');
INSERT INTO `schemata` VALUES ('340', 'SkyAuction.com', '2', null, 'schema/Airfares/SkyAuction.com.xml');
INSERT INTO `schemata` VALUES ('341', 'Smarter Living', '2', null, 'schema/Airfares/Smarter Living.xml');
INSERT INTO `schemata` VALUES ('342', 'SouthWest Airlines', '2', null, 'schema/Airfares/SouthWest Airlines.xml');
INSERT INTO `schemata` VALUES ('343', 'Spirit Airlines', '2', null, 'schema/Airfares/Spirit Airlines.xml');
INSERT INTO `schemata` VALUES ('344', 'sunfinder.com', '2', null, 'schema/Airfares/sunfinder.com.xml');
INSERT INTO `schemata` VALUES ('345', 'Swiss Airlines', '2', null, 'schema/Airfares/Swiss Airlines.xml');
INSERT INTO `schemata` VALUES ('346', 'travel4less.com', '2', null, 'schema/Airfares/travel4less.com.xml');
INSERT INTO `schemata` VALUES ('347', 'TravelHero.com', '2', null, 'schema/Airfares/TravelHero.com.xml');
INSERT INTO `schemata` VALUES ('348', 'travelocity.com', '2', null, 'schema/Airfares/travelocity.com.xml');
INSERT INTO `schemata` VALUES ('349', 'travelselect.com', '2', null, 'schema/Airfares/travelselect.com.xml');
INSERT INTO `schemata` VALUES ('350', 'United Airlines', '2', null, 'schema/Airfares/United Airlines.xml');
INSERT INTO `schemata` VALUES ('351', 'United Airways', '2', null, 'schema/Airfares/United Airways.xml');
INSERT INTO `schemata` VALUES ('352', '1StopAuto.com', '2', null, 'schema/Automobiles/1StopAuto.com.xml');
INSERT INTO `schemata` VALUES ('353', '401 Carfinder', '2', null, 'schema/Automobiles/401 Carfinder.xml');
INSERT INTO `schemata` VALUES ('354', 'A Cohen\'s Auto Search Page', '2', null, 'schema/Automobiles/A Cohen\'s Auto Search Page.xml');
INSERT INTO `schemata` VALUES ('355', 'ALotofCars.com', '2', null, 'schema/Automobiles/ALotofCars.com.xml');
INSERT INTO `schemata` VALUES ('356', 'AmarilloAutos.com', '2', null, 'schema/Automobiles/AmarilloAutos.com.xml');
INSERT INTO `schemata` VALUES ('357', 'Ask the Manufacturer Search', '2', null, 'schema/Automobiles/Ask the Manufacturer Search.xml');
INSERT INTO `schemata` VALUES ('358', 'Atkins Kroll, Inc of Guam', '2', null, 'schema/Automobiles/Atkins Kroll, Inc of Guam.xml');
INSERT INTO `schemata` VALUES ('359', 'Austin American Statesman - Austin, Texas', '2', null, 'schema/Automobiles/Austin American Statesman - Austin, Texas.xml');
INSERT INTO `schemata` VALUES ('360', 'Autobytel Used Car Search', '2', null, 'schema/Automobiles/Autobytel Used Car Search.xml');
INSERT INTO `schemata` VALUES ('361', 'Autofinder - Find a used car', '2', null, 'schema/Automobiles/Autofinder - Find a used car.xml');
INSERT INTO `schemata` VALUES ('362', 'AutoMob.com - The Car Site For Car People', '2', null, 'schema/Automobiles/AutoMob.com - The Car Site For Car People.xml');
INSERT INTO `schemata` VALUES ('363', 'AutoNation', '2', null, 'schema/Automobiles/AutoNation.xml');
INSERT INTO `schemata` VALUES ('364', 'AutoNation', '2', null, 'schema/Automobiles/AutoNation.xml');
INSERT INTO `schemata` VALUES ('365', 'Autonet.ca', '2', null, 'schema/Automobiles/Autonet.ca.xml');
INSERT INTO `schemata` VALUES ('366', 'AutoPoint Used Car Marketplace', '2', null, 'schema/Automobiles/AutoPoint Used Car Marketplace.xml');
INSERT INTO `schemata` VALUES ('367', 'Autoweb How to Buy a Car', '2', null, 'schema/Automobiles/Autoweb How to Buy a Car.xml');
INSERT INTO `schemata` VALUES ('368', 'Barrier Motors, Incorporated', '2', null, 'schema/Automobiles/Barrier Motors, Incorporated.xml');
INSERT INTO `schemata` VALUES ('369', 'Barrier Motors, Incorporated', '2', null, 'schema/Automobiles/Barrier Motors, Incorporated.xml');
INSERT INTO `schemata` VALUES ('370', 'Beechmont AUTOMILE', '2', null, 'schema/Automobiles/Beechmont AUTOMILE.xml');
INSERT INTO `schemata` VALUES ('371', 'BestAutoBuys.com', '2', null, 'schema/Automobiles/BestAutoBuys.com.xml');
INSERT INTO `schemata` VALUES ('372', 'Big Billy Barrett Chrysler Jeep Hyundai Mazda', '2', null, 'schema/Automobiles/Big Billy Barrett Chrysler Jeep Hyundai Mazda.xml');
INSERT INTO `schemata` VALUES ('373', 'Bob Smith Automotive', '2', null, 'schema/Automobiles/Bob Smith Automotive.xml');
INSERT INTO `schemata` VALUES ('374', 'Carbuyer.com', '2', null, 'schema/Automobiles/Carbuyer.com.xml');
INSERT INTO `schemata` VALUES ('375', 'CarCast Marketplaces', '2', null, 'schema/Automobiles/CarCast Marketplaces.xml');
INSERT INTO `schemata` VALUES ('376', 'CarParts.com', '2', null, 'schema/Automobiles/CarParts.com.xml');
INSERT INTO `schemata` VALUES ('377', 'CarPrices.com', '2', null, 'schema/Automobiles/CarPrices.com.xml');
INSERT INTO `schemata` VALUES ('378', 'CarPrices.com', '2', null, 'schema/Automobiles/CarPrices.com.xml');
INSERT INTO `schemata` VALUES ('379', 'CarPrices.com', '2', null, 'schema/Automobiles/CarPrices.com.xml');
INSERT INTO `schemata` VALUES ('380', 'Cars For Sale By Owner, Auto Classified Advertising Car Listings', '2', null, 'schema/Automobiles/Cars For Sale By Owner, Auto Classified Advertising Car Listings.xml');
INSERT INTO `schemata` VALUES ('381', 'cars.com', '2', null, 'schema/Automobiles/cars.com.xml');
INSERT INTO `schemata` VALUES ('382', 'cars.com', '2', null, 'schema/Automobiles/cars.com.xml');
INSERT INTO `schemata` VALUES ('383', 'CarsDirect.com', '2', null, 'schema/Automobiles/CarsDirect.com.xml');
INSERT INTO `schemata` VALUES ('384', 'Carsforsale.com - Buy Used Cars, Sell Your Car', '2', null, 'schema/Automobiles/Carsforsale.com - Buy Used Cars, Sell Your Car.xml');
INSERT INTO `schemata` VALUES ('385', 'CarSmart.com', '2', null, 'schema/Automobiles/CarSmart.com.xml');
INSERT INTO `schemata` VALUES ('386', 'Classic Motors, Inc.', '2', null, 'schema/Automobiles/Classic Motors, Inc..xml');
INSERT INTO `schemata` VALUES ('387', 'Colvin Auto Center', '2', null, 'schema/Automobiles/Colvin Auto Center.xml');
INSERT INTO `schemata` VALUES ('388', 'ComsumerReports.com', '2', null, 'schema/Automobiles/ComsumerReports.com.xml');
INSERT INTO `schemata` VALUES ('389', 'Consumer Guide', '2', null, 'schema/Automobiles/Consumer Guide.xml');
INSERT INTO `schemata` VALUES ('390', 'Crystal Auto Mall  - Green Brook, New Jersey', '2', null, 'schema/Automobiles/Crystal Auto Mall  - Green Brook, New Jersey.xml');
INSERT INTO `schemata` VALUES ('391', 'Crystal Auto Mall  - Green Brook, New Jersey', '2', null, 'schema/Automobiles/Crystal Auto Mall  - Green Brook, New Jersey.xml');
INSERT INTO `schemata` VALUES ('392', 'Cunningham Motors', '2', null, 'schema/Automobiles/Cunningham Motors.xml');
INSERT INTO `schemata` VALUES ('393', 'Cyber Motors', '2', null, 'schema/Automobiles/Cyber Motors.xml');
INSERT INTO `schemata` VALUES ('394', 'DealerNet.com - The Key To Your Car', '2', null, 'schema/Automobiles/DealerNet.com - The Key To Your Car.xml');
INSERT INTO `schemata` VALUES ('395', 'DealerNet.com - The Key To Your Car', '2', null, 'schema/Automobiles/DealerNet.com - The Key To Your Car.xml');
INSERT INTO `schemata` VALUES ('396', 'Dick Walter Auto Center', '2', null, 'schema/Automobiles/Dick Walter Auto Center.xml');
INSERT INTO `schemata` VALUES ('397', 'Dick Walter Auto Center', '2', null, 'schema/Automobiles/Dick Walter Auto Center.xml');
INSERT INTO `schemata` VALUES ('398', 'Discount Auto Pricing', '2', null, 'schema/Automobiles/Discount Auto Pricing.xml');
INSERT INTO `schemata` VALUES ('399', 'Drive.com.au', '2', null, 'schema/Automobiles/Drive.com.au.xml');
INSERT INTO `schemata` VALUES ('400', 'Earnhardt auto dealerships', '2', null, 'schema/Automobiles/Earnhardt auto dealerships.xml');
INSERT INTO `schemata` VALUES ('401', 'Earnhardt auto dealerships', '2', null, 'schema/Automobiles/Earnhardt auto dealerships.xml');
INSERT INTO `schemata` VALUES ('402', 'eBay Motors Search', '2', null, 'schema/Automobiles/eBay Motors Search.xml');
INSERT INTO `schemata` VALUES ('403', 'Exchange & Mart Motoring', '2', null, 'schema/Automobiles/Exchange & Mart Motoring.xml');
INSERT INTO `schemata` VALUES ('404', 'Extended Vehicle Warranty - Warranty Gold', '2', null, 'schema/Automobiles/Extended Vehicle Warranty - Warranty Gold.xml');
INSERT INTO `schemata` VALUES ('405', 'Fast-Net\'s World Parts Center Main Menu', '2', null, 'schema/Automobiles/Fast-Net\'s World Parts Center Main Menu.xml');
INSERT INTO `schemata` VALUES ('406', 'find used cars pickup trucks 4x4s or suvs used car locator', '2', null, 'schema/Automobiles/find used cars pickup trucks 4x4s or suvs used car locator.xml');
INSERT INTO `schemata` VALUES ('407', 'findausedcaronline.com car_finder', '2', null, 'schema/Automobiles/findausedcaronline.com car_finder.xml');
INSERT INTO `schemata` VALUES ('408', 'FindMe Cars - Cars for sale', '2', null, 'schema/Automobiles/FindMe Cars - Cars for sale.xml');
INSERT INTO `schemata` VALUES ('409', 'fish4cars - UK car search', '2', null, 'schema/Automobiles/fish4cars - UK car search.xml');
INSERT INTO `schemata` VALUES ('410', 'Fred Bondesen Chevrolet Oldsmobile Cadillac', '2', null, 'schema/Automobiles/Fred Bondesen Chevrolet Oldsmobile Cadillac.xml');
INSERT INTO `schemata` VALUES ('411', 'Free Carfinder service from Arizona AutoNet', '2', null, 'schema/Automobiles/Free Carfinder service from Arizona AutoNet.xml');
INSERT INTO `schemata` VALUES ('412', 'Get Auto', '2', null, 'schema/Automobiles/Get Auto.xml');
INSERT INTO `schemata` VALUES ('413', 'Got Cars 4 Sale - Search Listings', '2', null, 'schema/Automobiles/Got Cars 4 Sale - Search Listings.xml');
INSERT INTO `schemata` VALUES ('414', 'Herzog Meier Auto Center', '2', null, 'schema/Automobiles/Herzog Meier Auto Center.xml');
INSERT INTO `schemata` VALUES ('415', 'Herzog Meier Auto Center', '2', null, 'schema/Automobiles/Herzog Meier Auto Center.xml');
INSERT INTO `schemata` VALUES ('416', 'Hoak Motors', '2', null, 'schema/Automobiles/Hoak Motors.xml');
INSERT INTO `schemata` VALUES ('417', 'Huling Bros. Auto Center', '2', null, 'schema/Automobiles/Huling Bros. Auto Center.xml');
INSERT INTO `schemata` VALUES ('418', 'IntelliChoice CarCenter Auto Information', '2', null, 'schema/Automobiles/IntelliChoice CarCenter Auto Information.xml');
INSERT INTO `schemata` VALUES ('419', 'InvoiceDealers.com', '2', null, 'schema/Automobiles/InvoiceDealers.com.xml');
INSERT INTO `schemata` VALUES ('420', 'Jordan Motors', '2', null, 'schema/Automobiles/Jordan Motors.xml');
INSERT INTO `schemata` VALUES ('421', 'Kelley Blue Book - Used Car Bluebook Values and New Car Pricing', '2', null, 'schema/Automobiles/Kelley Blue Book - Used Car Bluebook Values and New Car Pricing.xml');
INSERT INTO `schemata` VALUES ('422', 'Kelly Grimsley Auto Group', '2', null, 'schema/Automobiles/Kelly Grimsley Auto Group.xml');
INSERT INTO `schemata` VALUES ('423', 'Kelly Grimsley Auto Group', '2', null, 'schema/Automobiles/Kelly Grimsley Auto Group.xml');
INSERT INTO `schemata` VALUES ('424', 'Krieger', '2', null, 'schema/Automobiles/Krieger.xml');
INSERT INTO `schemata` VALUES ('425', 'Lease Trader - The auto lease finder', '2', null, 'schema/Automobiles/Lease Trader - The auto lease finder.xml');
INSERT INTO `schemata` VALUES ('426', 'maxwellauto.com', '2', null, 'schema/Automobiles/maxwellauto.com.xml');
INSERT INTO `schemata` VALUES ('427', 'Merchants Auto Superstore', '2', null, 'schema/Automobiles/Merchants Auto Superstore.xml');
INSERT INTO `schemata` VALUES ('428', 'MotorNET.ie - Ireland\'s premier motoring website', '2', null, 'schema/Automobiles/MotorNET.ie - Ireland\'s premier motoring website.xml');
INSERT INTO `schemata` VALUES ('429', 'MotorNET.ie - Ireland\'s premier motoring website', '2', null, 'schema/Automobiles/MotorNET.ie - Ireland\'s premier motoring website.xml');
INSERT INTO `schemata` VALUES ('430', 'motors.scotsman.com', '2', null, 'schema/Automobiles/motors.scotsman.com.xml');
INSERT INTO `schemata` VALUES ('431', 'MS Webmasters, Inc.', '2', null, 'schema/Automobiles/MS Webmasters, Inc..xml');
INSERT INTO `schemata` VALUES ('432', 'MS Webmasters, Inc.', '2', null, 'schema/Automobiles/MS Webmasters, Inc..xml');
INSERT INTO `schemata` VALUES ('433', 'MSN Autos - Site Search Page', '2', null, 'schema/Automobiles/MSN Autos - Site Search Page.xml');
INSERT INTO `schemata` VALUES ('434', 'NADA - National Automobile Dealers Association Online', '2', null, 'schema/Automobiles/NADA - National Automobile Dealers Association Online.xml');
INSERT INTO `schemata` VALUES ('435', 'New car prices, used car pricing, auto reviews by Edmund\'s car buying guide', '2', null, 'schema/Automobiles/New car prices, used car pricing, auto reviews by Edmund\'s car buying guide.xml');
INSERT INTO `schemata` VALUES ('436', 'OCCarFinder.com', '2', null, 'schema/Automobiles/OCCarFinder.com.xml');
INSERT INTO `schemata` VALUES ('437', 'PalBeachPost.com', '2', null, 'schema/Automobiles/PalBeachPost.com.xml');
INSERT INTO `schemata` VALUES ('438', 'Quality Motors', '2', null, 'schema/Automobiles/Quality Motors.xml');
INSERT INTO `schemata` VALUES ('439', 'Rothrock Motors', '2', null, 'schema/Automobiles/Rothrock Motors.xml');
INSERT INTO `schemata` VALUES ('440', 'Search for Cars', '2', null, 'schema/Automobiles/Search for Cars.xml');
INSERT INTO `schemata` VALUES ('441', 'Shop Toyota Certified Used Vehicles', '2', null, 'schema/Automobiles/Shop Toyota Certified Used Vehicles.xml');
INSERT INTO `schemata` VALUES ('442', 'StoneAge.com', '2', null, 'schema/Automobiles/StoneAge.com.xml');
INSERT INTO `schemata` VALUES ('443', 'StoneAge.com', '2', null, 'schema/Automobiles/StoneAge.com.xml');
INSERT INTO `schemata` VALUES ('444', 'Tokin.com', '2', null, 'schema/Automobiles/Tokin.com.xml');
INSERT INTO `schemata` VALUES ('445', 'Traverse Motors', '2', null, 'schema/Automobiles/Traverse Motors.xml');
INSERT INTO `schemata` VALUES ('446', 'Used Car Search In UK', '2', null, 'schema/Automobiles/Used Car Search In UK.xml');
INSERT INTO `schemata` VALUES ('447', 'usedcar.com', '2', null, 'schema/Automobiles/usedcar.com.xml');
INSERT INTO `schemata` VALUES ('448', 'WebCar2000', '2', null, 'schema/Automobiles/WebCar2000.xml');
INSERT INTO `schemata` VALUES ('449', '1bookstreet.com', '2', null, 'schema/Books/1bookstreet.com.xml');
INSERT INTO `schemata` VALUES ('450', '1stcyberstore.com Book', '2', null, 'schema/Books/1stcyberstore.com Book.xml');
INSERT INTO `schemata` VALUES ('451', 'A1Books.com', '2', null, 'schema/Books/A1Books.com.xml');
INSERT INTO `schemata` VALUES ('452', 'Aaronbooks.com', '2', null, 'schema/Books/Aaronbooks.com.xml');
INSERT INTO `schemata` VALUES ('453', 'abebooks.com', '2', null, 'schema/Books/abebooks.com.xml');
INSERT INTO `schemata` VALUES ('454', 'Butterworth-Heinemann', '2', null, 'schema/Books/Butterworth-Heinemann.xml');
INSERT INTO `schemata` VALUES ('455', 'addall.com', '2', null, 'schema/Books/addall.com.xml');
INSERT INTO `schemata` VALUES ('456', 'alibris.com', '2', null, 'schema/Books/alibris.com.xml');
INSERT INTO `schemata` VALUES ('457', 'AllBookStores.com', '2', null, 'schema/Books/AllBookStores.com.xml');
INSERT INTO `schemata` VALUES ('458', 'amazon.com Book Search', '2', null, 'schema/Books/amazon.com Book Search.xml');
INSERT INTO `schemata` VALUES ('459', 'anybook.com', '2', null, 'schema/Books/anybook.com.xml');
INSERT INTO `schemata` VALUES ('460', 'anybook.com', '2', null, 'schema/Books/anybook.com.xml');
INSERT INTO `schemata` VALUES ('461', 'Barnes and Noble Book Search', '2', null, 'schema/Books/Barnes and Noble Book Search.xml');
INSERT INTO `schemata` VALUES ('462', 'BigTreeBooks.com', '2', null, 'schema/Books/BigTreeBooks.com.xml');
INSERT INTO `schemata` VALUES ('463', 'Bigwords.com', '2', null, 'schema/Books/Bigwords.com.xml');
INSERT INTO `schemata` VALUES ('464', 'Blackwell\'s Online Bookshop', '2', null, 'schema/Books/Blackwell\'s Online Bookshop.xml');
INSERT INTO `schemata` VALUES ('465', 'BookAMillion', '2', null, 'schema/Books/BookAMillion.xml');
INSERT INTO `schemata` VALUES ('466', 'bookbrain', '2', null, 'schema/Books/bookbrain.xml');
INSERT INTO `schemata` VALUES ('467', 'bookcloseouts', '2', null, 'schema/Books/bookcloseouts.xml');
INSERT INTO `schemata` VALUES ('468', 'bookgarden', '2', null, 'schema/Books/bookgarden.xml');
INSERT INTO `schemata` VALUES ('469', 'bookmarkphysics', '2', null, 'schema/Books/bookmarkphysics.xml');
INSERT INTO `schemata` VALUES ('470', 'bookopoly', '2', null, 'schema/Books/bookopoly.xml');
INSERT INTO `schemata` VALUES ('471', 'bookpool', '2', null, 'schema/Books/bookpool.xml');
INSERT INTO `schemata` VALUES ('472', 'booksense', '2', null, 'schema/Books/booksense.xml');
INSERT INTO `schemata` VALUES ('473', 'BooksInprint', '2', null, 'schema/Books/BooksInprint.xml');
INSERT INTO `schemata` VALUES ('474', 'bookstore', '2', null, 'schema/Books/bookstore.xml');
INSERT INTO `schemata` VALUES ('475', 'brian\'s books', '2', null, 'schema/Books/brian\'s books.xml');
INSERT INTO `schemata` VALUES ('476', 'Business Minds', '2', null, 'schema/Books/Business Minds.xml');
INSERT INTO `schemata` VALUES ('477', 'buy.com', '2', null, 'schema/Books/buy.com.xml');
INSERT INTO `schemata` VALUES ('478', 'campusi.com', '2', null, 'schema/Books/campusi.com.xml');
INSERT INTO `schemata` VALUES ('479', 'cheapest text books', '2', null, 'schema/Books/cheapest text books.xml');
INSERT INTO `schemata` VALUES ('480', 'Christianbooks.com', '2', null, 'schema/Books/Christianbooks.com.xml');
INSERT INTO `schemata` VALUES ('481', 'circleBooks Online', '2', null, 'schema/Books/circleBooks Online.xml');
INSERT INTO `schemata` VALUES ('482', 'classbook.com', '2', null, 'schema/Books/classbook.com.xml');
INSERT INTO `schemata` VALUES ('483', 'cokesbury', '2', null, 'schema/Books/cokesbury.xml');
INSERT INTO `schemata` VALUES ('484', 'computer Manuals', '2', null, 'schema/Books/computer Manuals.xml');
INSERT INTO `schemata` VALUES ('485', 'ComputerBookExpress', '2', null, 'schema/Books/ComputerBookExpress.xml');
INSERT INTO `schemata` VALUES ('486', 'devx.com', '2', null, 'schema/Books/devx.com.xml');
INSERT INTO `schemata` VALUES ('487', 'dymocks', '2', null, 'schema/Books/dymocks.xml');
INSERT INTO `schemata` VALUES ('488', 'ebay.com', '2', null, 'schema/Books/ebay.com.xml');
INSERT INTO `schemata` VALUES ('489', 'ecampus', '2', null, 'schema/Books/ecampus.xml');
INSERT INTO `schemata` VALUES ('490', 'Elsevier Science', '2', null, 'schema/Books/Elsevier Science.xml');
INSERT INTO `schemata` VALUES ('491', 'fabmall.com Book Search', '2', null, 'schema/Books/fabmall.com Book Search.xml');
INSERT INTO `schemata` VALUES ('492', 'havard book store', '2', null, 'schema/Books/havard book store.xml');
INSERT INTO `schemata` VALUES ('493', 'investmentbooks.com', '2', null, 'schema/Books/investmentbooks.com.xml');
INSERT INTO `schemata` VALUES ('494', 'life away', '2', null, 'schema/Books/life away.xml');
INSERT INTO `schemata` VALUES ('495', 'Lippincott Williams and Wilins', '2', null, 'schema/Books/Lippincott Williams and Wilins.xml');
INSERT INTO `schemata` VALUES ('496', 'MIT Press', '2', null, 'schema/Books/MIT Press.xml');
INSERT INTO `schemata` VALUES ('497', 'mySimon.com', '2', null, 'schema/Books/mySimon.com.xml');
INSERT INTO `schemata` VALUES ('498', 'Powells.com', '2', null, 'schema/Books/Powells.com.xml');
INSERT INTO `schemata` VALUES ('499', 'RandomHouse.com', '2', null, 'schema/Books/RandomHouse.com.xml');
INSERT INTO `schemata` VALUES ('500', 'SageBrush', '2', null, 'schema/Books/SageBrush.xml');
INSERT INTO `schemata` VALUES ('501', 'student book world', '2', null, 'schema/Books/student book world.xml');
INSERT INTO `schemata` VALUES ('502', 'talking books', '2', null, 'schema/Books/talking books.xml');
INSERT INTO `schemata` VALUES ('503', 'textbookx.com', '2', null, 'schema/Books/textbookx.com.xml');
INSERT INTO `schemata` VALUES ('504', 'American Book Center', '2', null, 'schema/Books/American Book Center.xml');
INSERT INTO `schemata` VALUES ('505', 'Drama Book Shop', '2', null, 'schema/Books/Drama Book Shop.xml');
INSERT INTO `schemata` VALUES ('506', 'HTML Writer\'s Guide', '2', null, 'schema/Books/HTML Writer\'s Guide.xml');
INSERT INTO `schemata` VALUES ('507', 'theinfo.com', '2', null, 'schema/Books/theinfo.com.xml');
INSERT INTO `schemata` VALUES ('508', 'TotalCampus.com', '2', null, 'schema/Books/TotalCampus.com.xml');
INSERT INTO `schemata` VALUES ('509', 'University Bookstore of UW', '2', null, 'schema/Books/University Bookstore of UW.xml');
INSERT INTO `schemata` VALUES ('510', 'University of Chicago, Press', '2', null, 'schema/Books/University of Chicago, Press.xml');
INSERT INTO `schemata` VALUES ('511', 'vasitybooks.com', '2', null, 'schema/Books/vasitybooks.com.xml');
INSERT INTO `schemata` VALUES ('512', 'walmart.com Books', '2', null, 'schema/Books/walmart.com Books.xml');
INSERT INTO `schemata` VALUES ('513', 'wordworth.com', '2', null, 'schema/Books/wordworth.com.xml');
INSERT INTO `schemata` VALUES ('514', 'zevelekakis Online BookStore', '2', null, 'schema/Books/zevelekakis Online BookStore.xml');
INSERT INTO `schemata` VALUES ('515', '1st UK Car Rentals', '2', null, 'schema/CarRentals/1st UK Car Rentals.xml');
INSERT INTO `schemata` VALUES ('516', 'airlines-hotels.com', '2', null, 'schema/CarRentals/airlines-hotels.com.xml');
INSERT INTO `schemata` VALUES ('517', 'Alamo.com', '2', null, 'schema/CarRentals/Alamo.com.xml');
INSERT INTO `schemata` VALUES ('518', 'AllState Car Rental', '2', null, 'schema/CarRentals/AllState Car Rental.xml');
INSERT INTO `schemata` VALUES ('519', 'AsiaTravelmart.com', '2', null, 'schema/CarRentals/AsiaTravelmart.com.xml');
INSERT INTO `schemata` VALUES ('520', 'AutoEurope.com ', '2', null, 'schema/CarRentals/AutoEurope.com .xml');
INSERT INTO `schemata` VALUES ('521', 'Avis.com', '2', null, 'schema/CarRentals/Avis.com.xml');
INSERT INTO `schemata` VALUES ('522', 'Budget Car Rental', '2', null, 'schema/CarRentals/Budget Car Rental.xml');
INSERT INTO `schemata` VALUES ('523', 'CheapFares.com Car Rental', '2', null, 'schema/CarRentals/CheapFares.com Car Rental.xml');
INSERT INTO `schemata` VALUES ('524', 'Dollar.com', '2', null, 'schema/CarRentals/Dollar.com.xml');
INSERT INTO `schemata` VALUES ('525', 'E-Vacation.com', '2', null, 'schema/CarRentals/E-Vacation.com.xml');
INSERT INTO `schemata` VALUES ('526', 'Expedia.com', '2', null, 'schema/CarRentals/Expedia.com.xml');
INSERT INTO `schemata` VALUES ('527', 'FoxRentACar.com', '2', null, 'schema/CarRentals/FoxRentACar.com.xml');
INSERT INTO `schemata` VALUES ('528', 'France.com Car Rental', '2', null, 'schema/CarRentals/France.com Car Rental.xml');
INSERT INTO `schemata` VALUES ('529', 'HotWire.com', '2', null, 'schema/CarRentals/HotWire.com.xml');
INSERT INTO `schemata` VALUES ('530', 'National Car Rental', '2', null, 'schema/CarRentals/National Car Rental.xml');
INSERT INTO `schemata` VALUES ('531', 'OneTravel.com', '2', null, 'schema/CarRentals/OneTravel.com.xml');
INSERT INTO `schemata` VALUES ('532', 'Orbitz', '2', null, 'schema/CarRentals/Orbitz.xml');
INSERT INTO `schemata` VALUES ('533', 'priceline', '2', null, 'schema/CarRentals/priceline.xml');
INSERT INTO `schemata` VALUES ('534', 'sunfinder.com Car Rental', '2', null, 'schema/CarRentals/sunfinder.com Car Rental.xml');
INSERT INTO `schemata` VALUES ('535', 'Travelocity.com', '2', null, 'schema/CarRentals/Travelocity.com.xml');
INSERT INTO `schemata` VALUES ('536', 'UK Car Rental', '2', null, 'schema/CarRentals/UK Car Rental.xml');
INSERT INTO `schemata` VALUES ('537', 'Woods Car Rental', '2', null, 'schema/CarRentals/Woods Car Rental.xml');
INSERT INTO `schemata` VALUES ('538', 'Yahoo Car Rental', '2', null, 'schema/CarRentals/Yahoo Car Rental.xml');
INSERT INTO `schemata` VALUES ('539', ' 1,000\'s of Hotels, Guest Houses, Lodgings - UK Accommodation Directory for England, Scotland, Wales and Ireland ', '2', null, 'schema/Hotels/ 1,000\'s of Hotels, Guest Houses, Lodgings - UK Accommodation Directory for England, Scotland, Wales and Ireland .xml');
INSERT INTO `schemata` VALUES ('540', '1st Hotels', '2', null, 'schema/Hotels/1st Hotels.xml');
INSERT INTO `schemata` VALUES ('541', 'Amerihost Inn', '2', null, 'schema/Hotels/Amerihost Inn.xml');
INSERT INTO `schemata` VALUES ('542', 'ATLANTA, GEORGIA Hotels and Hotel Reservations', '2', null, 'schema/Hotels/ATLANTA, GEORGIA Hotels and Hotel Reservations.xml');
INSERT INTO `schemata` VALUES ('543', 'Auto Europe Hotel Reservations', '2', null, 'schema/Hotels/Auto Europe Hotel Reservations.xml');
INSERT INTO `schemata` VALUES ('544', 'Bali Indonesia Travel Portal - Indo.Com', '2', null, 'schema/Hotels/Bali Indonesia Travel Portal - Indo.Com.xml');
INSERT INTO `schemata` VALUES ('545', 'Biz-stay.com Booking Engine', '2', null, 'schema/Hotels/Biz-stay.com Booking Engine.xml');
INSERT INTO `schemata` VALUES ('546', 'Cancun Central Reservations', '2', null, 'schema/Hotels/Cancun Central Reservations.xml');
INSERT INTO `schemata` VALUES ('547', 'Cape Cod hotels and travel guide', '2', null, 'schema/Hotels/Cape Cod hotels and travel guide.xml');
INSERT INTO `schemata` VALUES ('548', 'CheapFares.com', '2', null, 'schema/Hotels/CheapFares.com.xml');
INSERT INTO `schemata` VALUES ('549', 'Conrad Hotels Home', '2', null, 'schema/Hotels/Conrad Hotels Home.xml');
INSERT INTO `schemata` VALUES ('550', 'Days Inn', '2', null, 'schema/Hotels/Days Inn.xml');
INSERT INTO `schemata` VALUES ('551', 'DoubleTree Home', '2', null, 'schema/Hotels/DoubleTree Home.xml');
INSERT INTO `schemata` VALUES ('552', 'East Africa Internet Alliance', '2', null, 'schema/Hotels/East Africa Internet Alliance.xml');
INSERT INTO `schemata` VALUES ('553', 'Hilton Worldwide', '2', null, 'schema/Hotels/Hilton Worldwide.xml');
INSERT INTO `schemata` VALUES ('554', 'Hotel Reservations Discount Online Reservations For Hotels Worldwide.', '2', null, 'schema/Hotels/Hotel Reservations Discount Online Reservations For Hotels Worldwide..xml');
INSERT INTO `schemata` VALUES ('555', 'Hotel Reservations and Hotel Discounts from Hotwire', '2', null, 'schema/Hotels/Hotel Reservations and Hotel Discounts from Hotwire.xml');
INSERT INTO `schemata` VALUES ('556', 'Hotelbook.com', '2', null, 'schema/Hotels/Hotelbook.com.xml');
INSERT INTO `schemata` VALUES ('557', 'Hotelwiz - find discount hotels, cheap hotel reservations, hotel discounts, luxury hotels & more', '2', null, 'schema/Hotels/Hotelwiz - find discount hotels, cheap hotel reservations, hotel discounts, luxury hotels & more.xml');
INSERT INTO `schemata` VALUES ('558', 'HOTRES.com - Discount New York City Hotel Room Reservations', '2', null, 'schema/Hotels/HOTRES.com - Discount New York City Hotel Room Reservations.xml');
INSERT INTO `schemata` VALUES ('559', 'Howard Johnson - A Better Quality of Life on The Road', '2', null, 'schema/Hotels/Howard Johnson - A Better Quality of Life on The Road.xml');
INSERT INTO `schemata` VALUES ('560', 'Hyatt Hotels and Resorts', '2', null, 'schema/Hotels/Hyatt Hotels and Resorts.xml');
INSERT INTO `schemata` VALUES ('561', 'InnSite The Internet Directory of Bed and Breakfasts', '2', null, 'schema/Hotels/InnSite The Internet Directory of Bed and Breakfasts.xml');
INSERT INTO `schemata` VALUES ('562', 'Knights Inn Home', '2', null, 'schema/Hotels/Knights Inn Home.xml');
INSERT INTO `schemata` VALUES ('563', 'Las Vegas Hotels', '2', null, 'schema/Hotels/Las Vegas Hotels.xml');
INSERT INTO `schemata` VALUES ('564', 'Lodging.com Hotel Reservations Online', '2', null, 'schema/Hotels/Lodging.com Hotel Reservations Online.xml');
INSERT INTO `schemata` VALUES ('565', 'Marriott.com\'s hotel search', '2', null, 'schema/Hotels/Marriott.com\'s hotel search.xml');
INSERT INTO `schemata` VALUES ('566', 'Online Discount Hotel Reservations at top worldwide hotels and resorts.', '2', null, 'schema/Hotels/Online Discount Hotel Reservations at top worldwide hotels and resorts..xml');
INSERT INTO `schemata` VALUES ('567', 'Orbitz Hotel Reservations and Lodging', '2', null, 'schema/Hotels/Orbitz Hotel Reservations and Lodging.xml');
INSERT INTO `schemata` VALUES ('568', 'PlaceAReservation.com', '2', null, 'schema/Hotels/PlaceAReservation.com.xml');
INSERT INTO `schemata` VALUES ('569', 'Priceline.com', '2', null, 'schema/Hotels/Priceline.com.xml');
INSERT INTO `schemata` VALUES ('570', 'Respond.com', '2', null, 'schema/Hotels/Respond.com.xml');
INSERT INTO `schemata` VALUES ('571', 'Travel British Columbia Canada reservations accommodations tours packages tourism.', '2', null, 'schema/Hotels/Travel British Columbia Canada reservations accommodations tours packages tourism..xml');
INSERT INTO `schemata` VALUES ('572', 'Travel, hotels, discount hotel reservations and lodgings', '2', null, 'schema/Hotels/Travel, hotels, discount hotel reservations and lodgings.xml');
INSERT INTO `schemata` VALUES ('573', 'Travelfacts Hotel Search', '2', null, 'schema/Hotels/Travelfacts Hotel Search.xml');
INSERT INTO `schemata` VALUES ('574', 'Travelocity.com - Lodging', '2', null, 'schema/Hotels/Travelocity.com - Lodging.xml');
INSERT INTO `schemata` VALUES ('575', 'Travelodge Homepage', '2', null, 'schema/Hotels/Travelodge Homepage.xml');
INSERT INTO `schemata` VALUES ('576', 'Tropicana Casino And Resort', '2', null, 'schema/Hotels/Tropicana Casino And Resort.xml');
INSERT INTO `schemata` VALUES ('577', 'Yahoo! Travel - Hotel Search', '2', null, 'schema/Hotels/Yahoo! Travel - Hotel Search.xml');
INSERT INTO `schemata` VALUES ('578', 'Academic Employment Network', '2', null, 'schema/Jobs/Academic Employment Network.xml');
INSERT INTO `schemata` VALUES ('579', 'Accountemps Home', '2', null, 'schema/Jobs/Accountemps Home.xml');
INSERT INTO `schemata` VALUES ('580', 'Admin Clerical Jobs - Net-Temps', '2', null, 'schema/Jobs/Admin Clerical Jobs - Net-Temps.xml');
INSERT INTO `schemata` VALUES ('581', 'AIRS Recruiter Jobs', '2', null, 'schema/Jobs/AIRS Recruiter Jobs.xml');
INSERT INTO `schemata` VALUES ('582', 'Bankjobs.com', '2', null, 'schema/Jobs/Bankjobs.com.xml');
INSERT INTO `schemata` VALUES ('583', 'BestJobsUSA.com', '2', null, 'schema/Jobs/BestJobsUSA.com.xml');
INSERT INTO `schemata` VALUES ('584', 'BostonWorks Job Search', '2', null, 'schema/Jobs/BostonWorks Job Search.xml');
INSERT INTO `schemata` VALUES ('585', 'canjobs.com Your source for Jobs in Canada', '2', null, 'schema/Jobs/canjobs.com Your source for Jobs in Canada.xml');
INSERT INTO `schemata` VALUES ('586', 'CareerCorner.com', '2', null, 'schema/Jobs/CareerCorner.com.xml');
INSERT INTO `schemata` VALUES ('587', 'CareerExchange.com', '2', null, 'schema/Jobs/CareerExchange.com.xml');
INSERT INTO `schemata` VALUES ('588', 'CareerJournal Europe Job Search', '2', null, 'schema/Jobs/CareerJournal Europe Job Search.xml');
INSERT INTO `schemata` VALUES ('589', 'CareersCafe.com', '2', null, 'schema/Jobs/CareersCafe.com.xml');
INSERT INTO `schemata` VALUES ('590', 'ChicagoJobNetwork.com', '2', null, 'schema/Jobs/ChicagoJobNetwork.com.xml');
INSERT INTO `schemata` VALUES ('591', 'Christian Job Opportunity', '2', null, 'schema/Jobs/Christian Job Opportunity.xml');
INSERT INTO `schemata` VALUES ('592', 'Community Career Center', '2', null, 'schema/Jobs/Community Career Center.xml');
INSERT INTO `schemata` VALUES ('593', 'CSC CareerSource', '2', null, 'schema/Jobs/CSC CareerSource.xml');
INSERT INTO `schemata` VALUES ('594', 'Dice.com', '2', null, 'schema/Jobs/Dice.com.xml');
INSERT INTO `schemata` VALUES ('595', 'Engineer-Cad.com Home Page Engineer Architect Jobs Florida', '2', null, 'schema/Jobs/Engineer-Cad.com Home Page Engineer Architect Jobs Florida.xml');
INSERT INTO `schemata` VALUES ('596', 'Engineering jobs and engineering recruitment', '2', null, 'schema/Jobs/Engineering jobs and engineering recruitment.xml');
INSERT INTO `schemata` VALUES ('597', 'Express Personnel - Job Search. Employment. Franchises', '2', null, 'schema/Jobs/Express Personnel - Job Search. Employment. Franchises.xml');
INSERT INTO `schemata` VALUES ('598', 'Find Jobs at CareerBuilder', '2', null, 'schema/Jobs/Find Jobs at CareerBuilder.xml');
INSERT INTO `schemata` VALUES ('599', 'Find Jobs at CareerBuilder', '2', null, 'schema/Jobs/Find Jobs at CareerBuilder.xml');
INSERT INTO `schemata` VALUES ('600', 'Find Jobs at CareerBuilder', '2', null, 'schema/Jobs/Find Jobs at CareerBuilder.xml');
INSERT INTO `schemata` VALUES ('601', 'Gaapjobs.com', '2', null, 'schema/Jobs/Gaapjobs.com.xml');
INSERT INTO `schemata` VALUES ('602', 'GoJobSite UK', '2', null, 'schema/Jobs/GoJobSite UK.xml');
INSERT INTO `schemata` VALUES ('603', 'Health Care Jobs at HealthCareerWeb', '2', null, 'schema/Jobs/Health Care Jobs at HealthCareerWeb.xml');
INSERT INTO `schemata` VALUES ('604', 'Health Care Jobs at HealthCareerWeb', '2', null, 'schema/Jobs/Health Care Jobs at HealthCareerWeb.xml');
INSERT INTO `schemata` VALUES ('605', 'Hospitality Careers Online', '2', null, 'schema/Jobs/Hospitality Careers Online.xml');
INSERT INTO `schemata` VALUES ('606', 'Hotjobs.com', '2', null, 'schema/Jobs/Hotjobs.com.xml');
INSERT INTO `schemata` VALUES ('607', 'job news online, uk job vacancies, online job search - The Telegraph', '2', null, 'schema/Jobs/job news online, uk job vacancies, online job search - The Telegraph.xml');
INSERT INTO `schemata` VALUES ('608', 'Job.com', '2', null, 'schema/Jobs/Job.com.xml');
INSERT INTO `schemata` VALUES ('609', 'JobAA.com', '2', null, 'schema/Jobs/JobAA.com.xml');
INSERT INTO `schemata` VALUES ('610', 'JobGusher.com', '2', null, 'schema/Jobs/JobGusher.com.xml');
INSERT INTO `schemata` VALUES ('611', 'JobOptions -Job Database, Private Resume Builder and Recruitment Resource', '2', null, 'schema/Jobs/JobOptions -Job Database, Private Resume Builder and Recruitment Resource.xml');
INSERT INTO `schemata` VALUES ('612', 'Jobs.Net', '2', null, 'schema/Jobs/Jobs.Net.xml');
INSERT INTO `schemata` VALUES ('613', 'Jobsomega.com - Employment Exchange Online', '2', null, 'schema/Jobs/Jobsomega.com - Employment Exchange Online.xml');
INSERT INTO `schemata` VALUES ('614', 'Monstertrak.com', '2', null, 'schema/Jobs/Monstertrak.com.xml');
INSERT INTO `schemata` VALUES ('615', 'OCJobFinder.com', '2', null, 'schema/Jobs/OCJobFinder.com.xml');
INSERT INTO `schemata` VALUES ('616', 'Opportunity Knocks', '2', null, 'schema/Jobs/Opportunity Knocks.xml');
INSERT INTO `schemata` VALUES ('617', 'Reed.co.uk', '2', null, 'schema/Jobs/Reed.co.uk.xml');
INSERT INTO `schemata` VALUES ('618', 'Robert Half Finance & Accounting Home', '2', null, 'schema/Jobs/Robert Half Finance & Accounting Home.xml');
INSERT INTO `schemata` VALUES ('619', 'Telecommunications job search for job seekers - TelecomCareers.Net', '2', null, 'schema/Jobs/Telecommunications job search for job seekers - TelecomCareers.Net.xml');
INSERT INTO `schemata` VALUES ('620', 'TheSite', '2', null, 'schema/Jobs/TheSite.xml');
INSERT INTO `schemata` VALUES ('621', 'TrueCareers.com', '2', null, 'schema/Jobs/TrueCareers.com.xml');
INSERT INTO `schemata` VALUES ('622', 'UK Jobs at Gis-a-Job UK Recruitment', '2', null, 'schema/Jobs/UK Jobs at Gis-a-Job UK Recruitment.xml');
INSERT INTO `schemata` VALUES ('623', 'USAJOBS - Professional, Administrative and Technical Positions', '2', null, 'schema/Jobs/USAJOBS - Professional, Administrative and Technical Positions.xml');
INSERT INTO `schemata` VALUES ('624', 'Vault Job Board', '2', null, 'schema/Jobs/Vault Job Board.xml');
INSERT INTO `schemata` VALUES ('625', 'Volpe Center', '2', null, 'schema/Jobs/Volpe Center.xml');
INSERT INTO `schemata` VALUES ('626', 'Wanted Jobs', '2', null, 'schema/Jobs/Wanted Jobs.xml');
INSERT INTO `schemata` VALUES ('627', 'Workopolis.com Canada\'s Biggest Job Site', '2', null, 'schema/Jobs/Workopolis.com Canada\'s Biggest Job Site.xml');
INSERT INTO `schemata` VALUES ('628', 'Workthing.com', '2', null, 'schema/Jobs/Workthing.com.xml');
INSERT INTO `schemata` VALUES ('629', 'ZDNet UK Jobs', '2', null, 'schema/Jobs/ZDNet UK Jobs.xml');
INSERT INTO `schemata` VALUES ('630', ' 1stcyberstore.com ', '2', null, 'schema/Movies/ 1stcyberstore.com .xml');
INSERT INTO `schemata` VALUES ('631', ' 949online.com ', '2', null, 'schema/Movies/ 949online.com .xml');
INSERT INTO `schemata` VALUES ('632', ' All Movie Guide ', '2', null, 'schema/Movies/ All Movie Guide .xml');
INSERT INTO `schemata` VALUES ('633', ' chapters.indigo.ca video ', '2', null, 'schema/Movies/ chapters.indigo.ca video .xml');
INSERT INTO `schemata` VALUES ('634', ' Excalibur Films ', '2', null, 'schema/Movies/ Excalibur Films .xml');
INSERT INTO `schemata` VALUES ('635', ' Half.com Movie ', '2', null, 'schema/Movies/ Half.com Movie .xml');
INSERT INTO `schemata` VALUES ('636', ' Yahoo! Movies ', '2', null, 'schema/Movies/ Yahoo! Movies .xml');
INSERT INTO `schemata` VALUES ('637', ' Hollywood.com ', '2', null, 'schema/Movies/ Hollywood.com .xml');
INSERT INTO `schemata` VALUES ('638', ' Robert\'s Hard to find Videos ', '2', null, 'schema/Movies/ Robert\'s Hard to find Videos .xml');
INSERT INTO `schemata` VALUES ('639', ' EntertainMe.com ', '2', null, 'schema/Movies/ EntertainMe.com .xml');
INSERT INTO `schemata` VALUES ('640', ' Sensasian.com ', '2', null, 'schema/Movies/ Sensasian.com .xml');
INSERT INTO `schemata` VALUES ('641', ' Tower Records.com ', '2', null, 'schema/Movies/ Tower Records.com .xml');
INSERT INTO `schemata` VALUES ('642', ' The Internet Movie Database (IMDb) ', '2', null, 'schema/Movies/ The Internet Movie Database (IMDb) .xml');
INSERT INTO `schemata` VALUES ('643', ' Barnes Noble.com Video ', '2', null, 'schema/Movies/ Barnes Noble.com Video .xml');
INSERT INTO `schemata` VALUES ('644', ' wsbradio.com  ', '2', null, 'schema/Movies/ wsbradio.com  .xml');
INSERT INTO `schemata` VALUES ('645', ' Sound Online ', '2', null, 'schema/Movies/ Sound Online .xml');
INSERT INTO `schemata` VALUES ('646', ' Ain\'t It Cool News ', '2', null, 'schema/Movies/ Ain\'t It Cool News .xml');
INSERT INTO `schemata` VALUES ('647', ' AllDirect.com ', '2', null, 'schema/Movies/ AllDirect.com .xml');
INSERT INTO `schemata` VALUES ('648', ' The Amazing Video Network ', '2', null, 'schema/Movies/ The Amazing Video Network .xml');
INSERT INTO `schemata` VALUES ('649', ' Amazon.com DVD ', '2', null, 'schema/Movies/ Amazon.com DVD .xml');
INSERT INTO `schemata` VALUES ('650', ' Best Video Home ', '2', null, 'schema/Movies/ Best Video Home .xml');
INSERT INTO `schemata` VALUES ('651', ' Best Video Buys DVD and VHS Movie Search ', '2', null, 'schema/Movies/ Best Video Buys DVD and VHS Movie Search .xml');
INSERT INTO `schemata` VALUES ('652', 'www.bigmoviezone.com ', '2', null, 'schema/Movies/www.bigmoviezone.com .xml');
INSERT INTO `schemata` VALUES ('653', ' BizRate Videos Price Comparison Shopping ', '2', null, 'schema/Movies/ BizRate Videos Price Comparison Shopping .xml');
INSERT INTO `schemata` VALUES ('654', ' BlockBuster ', '2', null, 'schema/Movies/ BlockBuster .xml');
INSERT INTO `schemata` VALUES ('655', ' Britannia Music and Video - Video ', '2', null, 'schema/Movies/ Britannia Music and Video - Video .xml');
INSERT INTO `schemata` VALUES ('656', ' Critic\'s Choice Video ', '2', null, 'schema/Movies/ Critic\'s Choice Video .xml');
INSERT INTO `schemata` VALUES ('657', ' CD Universe ', '2', null, 'schema/Movies/ CD Universe .xml');
INSERT INTO `schemata` VALUES ('658', ' Djangos - Used Music & Movies ', '2', null, 'schema/Movies/ Djangos - Used Music & Movies .xml');
INSERT INTO `schemata` VALUES ('659', ' DVD.com ', '2', null, 'schema/Movies/ DVD.com .xml');
INSERT INTO `schemata` VALUES ('660', ' DVD Empire ', '2', null, 'schema/Movies/ DVD Empire .xml');
INSERT INTO `schemata` VALUES ('661', ' EurekaMovies.com ', '2', null, 'schema/Movies/ EurekaMovies.com .xml');
INSERT INTO `schemata` VALUES ('662', ' Fandango - Buy movie tickets online ', '2', null, 'schema/Movies/ Fandango - Buy movie tickets online .xml');
INSERT INTO `schemata` VALUES ('663', ' Family Boxoffice ', '2', null, 'schema/Movies/ Family Boxoffice .xml');
INSERT INTO `schemata` VALUES ('664', ' FirstandSecond.com - India\'s Biggest Video Stop ', '2', null, 'schema/Movies/ FirstandSecond.com - India\'s Biggest Video Stop .xml');
INSERT INTO `schemata` VALUES ('665', ' ForeignFilms.com ', '2', null, 'schema/Movies/ ForeignFilms.com .xml');
INSERT INTO `schemata` VALUES ('666', ' Formovies.com ', '2', null, 'schema/Movies/ Formovies.com .xml');
INSERT INTO `schemata` VALUES ('667', ' FYE Movies ', '2', null, 'schema/Movies/ FYE Movies .xml');
INSERT INTO `schemata` VALUES ('668', ' Great Classic Movies ', '2', null, 'schema/Movies/ Great Classic Movies .xml');
INSERT INTO `schemata` VALUES ('669', ' Hollywood Video ', '2', null, 'schema/Movies/ Hollywood Video .xml');
INSERT INTO `schemata` VALUES ('670', ' J&R Music and Computer World ', '2', null, 'schema/Movies/ J&R Music and Computer World .xml');
INSERT INTO `schemata` VALUES ('671', ' Kmart.com - Movies ', '2', null, 'schema/Movies/ Kmart.com - Movies .xml');
INSERT INTO `schemata` VALUES ('672', ' SendMeMovies.com ', '2', null, 'schema/Movies/ SendMeMovies.com .xml');
INSERT INTO `schemata` VALUES ('673', ' SendMeMovies.com ', '2', null, 'schema/Movies/ SendMeMovies.com .xml');
INSERT INTO `schemata` VALUES ('674', ' Lions Gate Films ', '2', null, 'schema/Movies/ Lions Gate Films .xml');
INSERT INTO `schemata` VALUES ('675', ' Mission Direct ', '2', null, 'schema/Movies/ Mission Direct .xml');
INSERT INTO `schemata` VALUES ('676', ' Moviefone.com ', '2', null, 'schema/Movies/ Moviefone.com .xml');
INSERT INTO `schemata` VALUES ('677', ' MovieGallery.com ', '2', null, 'schema/Movies/ MovieGallery.com .xml');
INSERT INTO `schemata` VALUES ('678', ' MoviesUnlimited.com ', '2', null, 'schema/Movies/ MoviesUnlimited.com .xml');
INSERT INTO `schemata` VALUES ('679', ' MoviesUnlimited.com ', '2', null, 'schema/Movies/ MoviesUnlimited.com .xml');
INSERT INTO `schemata` VALUES ('680', ' MoviesUnlimited.com ', '2', null, 'schema/Movies/ MoviesUnlimited.com .xml');
INSERT INTO `schemata` VALUES ('681', ' MoviesUnlimited.com ', '2', null, 'schema/Movies/ MoviesUnlimited.com .xml');
INSERT INTO `schemata` VALUES ('682', ' MovieTickets.com ', '2', null, 'schema/Movies/ MovieTickets.com .xml');
INSERT INTO `schemata` VALUES ('683', ' Music Video Distributors ', '2', null, 'schema/Movies/ Music Video Distributors .xml');
INSERT INTO `schemata` VALUES ('684', ' mySimon ', '2', null, 'schema/Movies/ mySimon .xml');
INSERT INTO `schemata` VALUES ('685', ' Nostalgia Family Video ', '2', null, 'schema/Movies/ Nostalgia Family Video .xml');
INSERT INTO `schemata` VALUES ('686', ' PlanetOut ', '2', null, 'schema/Movies/ PlanetOut .xml');
INSERT INTO `schemata` VALUES ('687', ' PlayCentric.com ', '2', null, 'schema/Movies/ PlayCentric.com .xml');
INSERT INTO `schemata` VALUES ('688', ' PlayCentric.com ', '2', null, 'schema/Movies/ PlayCentric.com .xml');
INSERT INTO `schemata` VALUES ('689', ' RareVideo.com ', '2', null, 'schema/Movies/ RareVideo.com .xml');
INSERT INTO `schemata` VALUES ('690', ' Reel.com ', '2', null, 'schema/Movies/ Reel.com .xml');
INSERT INTO `schemata` VALUES ('691', ' ROTTEN TOMATOES Movie Reviews Previews ', '2', null, 'schema/Movies/ ROTTEN TOMATOES Movie Reviews Previews .xml');
INSERT INTO `schemata` VALUES ('692', ' Skinnyguy.com ', '2', null, 'schema/Movies/ Skinnyguy.com .xml');
INSERT INTO `schemata` VALUES ('693', ' The Movie Page ', '2', null, 'schema/Movies/ The Movie Page .xml');
INSERT INTO `schemata` VALUES ('694', ' Suncoast.com ', '2', null, 'schema/Movies/ Suncoast.com .xml');
INSERT INTO `schemata` VALUES ('695', ' theinfo.com movies and videos ', '2', null, 'schema/Movies/ theinfo.com movies and videos .xml');
INSERT INTO `schemata` VALUES ('696', ' tlavideo.com - Your Online Movie Source ', '2', null, 'schema/Movies/ tlavideo.com - Your Online Movie Source .xml');
INSERT INTO `schemata` VALUES ('697', ' BUY.COM - VHS Action Comedy Drama Videos at Low Prices ', '2', null, 'schema/Movies/ BUY.COM - VHS Action Comedy Drama Videos at Low Prices .xml');
INSERT INTO `schemata` VALUES ('698', ' VCD Movies & SVCD at VCDGallery ', '2', null, 'schema/Movies/ VCD Movies & SVCD at VCDGallery .xml');
INSERT INTO `schemata` VALUES ('699', ' Videocollection.com ', '2', null, 'schema/Movies/ Videocollection.com .xml');
INSERT INTO `schemata` VALUES ('700', ' Videoflicks ', '2', null, 'schema/Movies/ Videoflicks .xml');
INSERT INTO `schemata` VALUES ('701', ' Videomas ', '2', null, 'schema/Movies/ Videomas .xml');
INSERT INTO `schemata` VALUES ('702', ' Videomatica Online - VHS and DVD ', '2', null, 'schema/Movies/ Videomatica Online - VHS and DVD .xml');
INSERT INTO `schemata` VALUES ('703', ' Videomoviehouse.com - DVD & VHS Movies for Sale ', '2', null, 'schema/Movies/ Videomoviehouse.com - DVD & VHS Movies for Sale .xml');
INSERT INTO `schemata` VALUES ('704', ' Vidiots video VHS DVD VCR Rental Sales movie film Los Angeles ', '2', null, 'schema/Movies/ Vidiots video VHS DVD VCR Rental Sales movie film Los Angeles .xml');
INSERT INTO `schemata` VALUES ('705', ' Walmart.com - Movies ', '2', null, 'schema/Movies/ Walmart.com - Movies .xml');
INSERT INTO `schemata` VALUES ('706', ' WHSmith.co.uk ', '2', null, 'schema/Movies/ WHSmith.co.uk .xml');
INSERT INTO `schemata` VALUES ('707', ' Original VCD Movies ', '2', null, 'schema/Movies/ Original VCD Movies .xml');
INSERT INTO `schemata` VALUES ('708', ' Movies myTELUS ', '2', null, 'schema/Movies/ Movies myTELUS .xml');
INSERT INTO `schemata` VALUES ('709', ' 101cd.com ', '2', null, 'schema/MusicRecords/ 101cd.com .xml');
INSERT INTO `schemata` VALUES ('710', ' Streets Online ', '2', null, 'schema/MusicRecords/ Streets Online .xml');
INSERT INTO `schemata` VALUES ('711', ' CD Universe ', '2', null, 'schema/MusicRecords/ CD Universe .xml');
INSERT INTO `schemata` VALUES ('712', ' CD Universe ', '2', null, 'schema/MusicRecords/ CD Universe .xml');
INSERT INTO `schemata` VALUES ('713', ' Amazon.com Music Homepage ', '2', null, 'schema/MusicRecords/ Amazon.com Music Homepage .xml');
INSERT INTO `schemata` VALUES ('714', ' Saturn Records ', '2', null, 'schema/MusicRecords/ Saturn Records .xml');
INSERT INTO `schemata` VALUES ('715', ' Songsearch CD Store ', '2', null, 'schema/MusicRecords/ Songsearch CD Store .xml');
INSERT INTO `schemata` VALUES ('716', ' Records by Mail ', '2', null, 'schema/MusicRecords/ Records by Mail .xml');
INSERT INTO `schemata` VALUES ('717', ' Vinyl Venders ', '2', null, 'schema/MusicRecords/ Vinyl Venders .xml');
INSERT INTO `schemata` VALUES ('718', ' UBL Artist Direct ', '2', null, 'schema/MusicRecords/ UBL Artist Direct .xml');
INSERT INTO `schemata` VALUES ('719', ' CDPlus.com ', '2', null, 'schema/MusicRecords/ CDPlus.com .xml');
INSERT INTO `schemata` VALUES ('720', ' IUMA ', '2', null, 'schema/MusicRecords/ IUMA .xml');
INSERT INTO `schemata` VALUES ('721', ' IUMA ', '2', null, 'schema/MusicRecords/ IUMA .xml');
INSERT INTO `schemata` VALUES ('722', ' The Last Unicorn ', '2', null, 'schema/MusicRecords/ The Last Unicorn .xml');
INSERT INTO `schemata` VALUES ('723', ' mySimon ', '2', null, 'schema/MusicRecords/ mySimon .xml');
INSERT INTO `schemata` VALUES ('724', ' Sony Music ', '2', null, 'schema/MusicRecords/ Sony Music .xml');
INSERT INTO `schemata` VALUES ('725', ' SongFile Home ', '2', null, 'schema/MusicRecords/ SongFile Home .xml');
INSERT INTO `schemata` VALUES ('726', ' All Classical Guide ', '2', null, 'schema/MusicRecords/ All Classical Guide .xml');
INSERT INTO `schemata` VALUES ('727', ' theinfo.com -- Search for music information ', '2', null, 'schema/MusicRecords/ theinfo.com -- Search for music information .xml');
INSERT INTO `schemata` VALUES ('728', ' Barns & Noble.com Music ', '2', null, 'schema/MusicRecords/ Barns & Noble.com Music .xml');
INSERT INTO `schemata` VALUES ('729', ' BizRate Music Price Comparison Shopping ', '2', null, 'schema/MusicRecords/ BizRate Music Price Comparison Shopping .xml');
INSERT INTO `schemata` VALUES ('730', ' Music at Gracenote and CDDB ', '2', null, 'schema/MusicRecords/ Music at Gracenote and CDDB .xml');
INSERT INTO `schemata` VALUES ('731', ' FolkWeb ', '2', null, 'schema/MusicRecords/ FolkWeb .xml');
INSERT INTO `schemata` VALUES ('732', ' Khazana Music ', '2', null, 'schema/MusicRecords/ Khazana Music .xml');
INSERT INTO `schemata` VALUES ('733', ' Your Arts Network for Classical Jazz Opera WorldMusic Theater & Ballet ', '2', null, 'schema/MusicRecords/ Your Arts Network for Classical Jazz Opera WorldMusic Theater & Ballet .xml');
INSERT INTO `schemata` VALUES ('734', ' Liquid Store ', '2', null, 'schema/MusicRecords/ Liquid Store .xml');
INSERT INTO `schemata` VALUES ('735', ' BUY.COM - Buy Music CDs at Low Prices ', '2', null, 'schema/MusicRecords/ BUY.COM - Buy Music CDs at Low Prices .xml');
INSERT INTO `schemata` VALUES ('736', ' WWW.CD999.COM ', '2', null, 'schema/MusicRecords/ WWW.CD999.COM .xml');
INSERT INTO `schemata` VALUES ('737', ' IranMelody Music Section,Iranian Music ', '2', null, 'schema/MusicRecords/ IranMelody Music Section,Iranian Music .xml');
INSERT INTO `schemata` VALUES ('738', ' CD Store Of Original And Exclusive CDs ', '2', null, 'schema/MusicRecords/ CD Store Of Original And Exclusive CDs .xml');
INSERT INTO `schemata` VALUES ('739', ' ALLEGRO MUSIC ', '2', null, 'schema/MusicRecords/ ALLEGRO MUSIC .xml');
INSERT INTO `schemata` VALUES ('740', ' Compact Disc Europe ', '2', null, 'schema/MusicRecords/ Compact Disc Europe .xml');
INSERT INTO `schemata` VALUES ('741', ' Music dot Web For All your Music Needs ', '2', null, 'schema/MusicRecords/ Music dot Web For All your Music Needs .xml');
INSERT INTO `schemata` VALUES ('742', ' Music Imports ', '2', null, 'schema/MusicRecords/ Music Imports .xml');
INSERT INTO `schemata` VALUES ('743', ' The Music Safe ', '2', null, 'schema/MusicRecords/ The Music Safe .xml');
INSERT INTO `schemata` VALUES ('744', ' Energy Music ', '2', null, 'schema/MusicRecords/ Energy Music .xml');
INSERT INTO `schemata` VALUES ('745', ' Tower Records ', '2', null, 'schema/MusicRecords/ Tower Records .xml');
INSERT INTO `schemata` VALUES ('746', ' FYE Music ', '2', null, 'schema/MusicRecords/ FYE Music .xml');
INSERT INTO `schemata` VALUES ('747', ' FYE Music ', '2', null, 'schema/MusicRecords/ FYE Music .xml');
INSERT INTO `schemata` VALUES ('748', ' PricingCentrial.com ', '2', null, 'schema/MusicRecords/ PricingCentrial.com .xml');
INSERT INTO `schemata` VALUES ('749', ' Half.com Music ', '2', null, 'schema/MusicRecords/ Half.com Music .xml');
INSERT INTO `schemata` VALUES ('750', ' AMG All Music Guide ', '2', null, 'schema/MusicRecords/ AMG All Music Guide .xml');
INSERT INTO `schemata` VALUES ('751', ' Artist Direct Search ', '2', null, 'schema/MusicRecords/ Artist Direct Search .xml');
INSERT INTO `schemata` VALUES ('752', ' Cool Site of the Day ', '2', null, 'schema/MusicRecords/ Cool Site of the Day .xml');
INSERT INTO `schemata` VALUES ('753', ' MTV.com - Music ', '2', null, 'schema/MusicRecords/ MTV.com - Music .xml');
INSERT INTO `schemata` VALUES ('754', ' Walmart.com - Music ', '2', null, 'schema/MusicRecords/ Walmart.com - Music .xml');
INSERT INTO `schemata` VALUES ('755', ' Kmart.com - Music ', '2', null, 'schema/MusicRecords/ Kmart.com - Music .xml');
INSERT INTO `schemata` VALUES ('756', ' DVD Empire ', '2', null, 'schema/MusicRecords/ DVD Empire .xml');
INSERT INTO `schemata` VALUES ('757', ' Shop at DealTime.com ', '2', null, 'schema/MusicRecords/ Shop at DealTime.com .xml');
INSERT INTO `schemata` VALUES ('758', ' A&B Sound Online ', '2', null, 'schema/MusicRecords/ A&B Sound Online .xml');
INSERT INTO `schemata` VALUES ('759', ' A&B Sound Online ', '2', null, 'schema/MusicRecords/ A&B Sound Online .xml');
INSERT INTO `schemata` VALUES ('760', ' Audiohouse Used CDs ', '2', null, 'schema/MusicRecords/ Audiohouse Used CDs .xml');
INSERT INTO `schemata` VALUES ('761', ' CDBanzai - English ', '2', null, 'schema/MusicRecords/ CDBanzai - English .xml');
INSERT INTO `schemata` VALUES ('762', ' CDconnection.com ', '2', null, 'schema/MusicRecords/ CDconnection.com .xml');
INSERT INTO `schemata` VALUES ('763', ' Skinnyguy.com ', '2', null, 'schema/MusicRecords/ Skinnyguy.com .xml');
INSERT INTO `schemata` VALUES ('764', ' Sensasian.com ', '2', null, 'schema/MusicRecords/ Sensasian.com .xml');
INSERT INTO `schemata` VALUES ('765', ' CDExpress ', '2', null, 'schema/MusicRecords/ CDExpress .xml');
INSERT INTO `schemata` VALUES ('766', ' CDExpress ', '2', null, 'schema/MusicRecords/ CDExpress .xml');
INSERT INTO `schemata` VALUES ('767', ' PlayCentric.com Music ', '2', null, 'schema/MusicRecords/ PlayCentric.com Music .xml');
INSERT INTO `schemata` VALUES ('768', ' Vintage Vinyl ', '2', null, 'schema/MusicRecords/ Vintage Vinyl .xml');
INSERT INTO `schemata` VALUES ('769', ' WWW Music Database ', '2', null, 'schema/MusicRecords/ WWW Music Database .xml');
INSERT INTO `schemata` VALUES ('770', ' 1stcyberstore.com ', '2', null, 'schema/MusicRecords/ 1stcyberstore.com .xml');
INSERT INTO `schemata` VALUES ('771', ' Britannia Music and Video - Music ', '2', null, 'schema/MusicRecords/ Britannia Music and Video - Music .xml');
INSERT INTO `schemata` VALUES ('772', ' CyberMusic Surplus ', '2', null, 'schema/MusicRecords/ CyberMusic Surplus .xml');
INSERT INTO `schemata` VALUES ('773', ' EntertainMe.com ', '2', null, 'schema/MusicRecords/ EntertainMe.com .xml');
INSERT INTO `schemata` VALUES ('774', ' J&R Music and Computer World ', '2', null, 'schema/MusicRecords/ J&R Music and Computer World .xml');
INSERT INTO `schemata` VALUES ('775', ' Best Video Buys CD Search', '2', null, 'schema/MusicRecords/ Best Video Buys CD Search.xml');
INSERT INTO `schemata` VALUES ('776', ' Djangos - Used Music & Movies ', '2', null, 'schema/MusicRecords/ Djangos - Used Music & Movies .xml');
INSERT INTO `schemata` VALUES ('777', ' FirstandSecond.com ', '2', null, 'schema/MusicRecords/ FirstandSecond.com .xml');
INSERT INTO `schemata` VALUES ('778', ' WHSmith.co.uk ', '2', null, 'schema/MusicRecords/ WHSmith.co.uk .xml');

-- ----------------------------
-- Table structure for `similaritymatrices`
-- ----------------------------
DROP TABLE IF EXISTS `similaritymatrices`;
CREATE TABLE `similaritymatrices` (
  `TargetSchemaID` bigint(20) NOT NULL,
  `TargetTermID` int(11) NOT NULL,
  `CandidateSchemaID` bigint(20) NOT NULL,
  `CandidateTermID` int(11) NOT NULL,
  `SMID` bigint(20) NOT NULL,
  `confidence` double NOT NULL,
  `TTermName` text,
  `CTermName` text,
  PRIMARY KEY (`TargetSchemaID`,`TargetTermID`,`CandidateSchemaID`,`CandidateTermID`,`SMID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of similaritymatrices
-- ----------------------------

-- ----------------------------
-- Table structure for `similaritymeasures`
-- ----------------------------
DROP TABLE IF EXISTS `similaritymeasures`;
CREATE TABLE `similaritymeasures` (
  `SMID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MeasureName` varchar(50) NOT NULL,
  `System` char(10) DEFAULT NULL,
  `SMType` int(11) DEFAULT NULL,
  PRIMARY KEY (`SMID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of similaritymeasures
-- ----------------------------

-- ----------------------------
-- Table structure for `smtypes`
-- ----------------------------
DROP TABLE IF EXISTS `smtypes`;
CREATE TABLE `smtypes` (
  `SMType_no` int(11) NOT NULL AUTO_INCREMENT,
  `SMType_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`SMType_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of smtypes
-- ----------------------------

-- ----------------------------
-- Table structure for `sysdiagrams`
-- ----------------------------
DROP TABLE IF EXISTS `sysdiagrams`;
CREATE TABLE `sysdiagrams` (
  `name` varchar(128) NOT NULL,
  `principal_id` int(11) NOT NULL,
  `diagram_id` int(11) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `definition` blob,
  PRIMARY KEY (`diagram_id`),
  UNIQUE KEY `UK_principal_name` (`principal_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sysdiagrams
-- ----------------------------

-- ----------------------------
-- Table structure for `terms`
-- ----------------------------
DROP TABLE IF EXISTS `terms`;
CREATE TABLE `terms` (
  `SchemaID` bigint(20) NOT NULL,
  `Tid` int(11) NOT NULL,
  `TName` varchar(0) NOT NULL,
  `TType` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SchemaID`,`Tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of terms
-- ----------------------------

-- ----------------------------
-- View structure for `datasets_num`
-- ----------------------------
DROP VIEW IF EXISTS `datasets_num`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `datasets_num` AS select count(0) AS `num` from `datasets`;

-- ----------------------------
-- View structure for `schemapairs_num`
-- ----------------------------
DROP VIEW IF EXISTS `schemapairs_num`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `schemapairs_num` AS select count(0) AS `num` from `schemapairs`;

-- ----------------------------
-- View structure for `schemata_num`
-- ----------------------------
DROP VIEW IF EXISTS `schemata_num`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `schemata_num` AS select count(0) AS `num` from `schemata`;
