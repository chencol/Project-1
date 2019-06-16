-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 17, 2017 at 03:40 AM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `se`
--

-- --------------------------------------------------------

--
-- Table structure for table `demographic`
--

DROP TABLE IF EXISTS `demographic`;
CREATE TABLE IF NOT EXISTS `demographic` (
  `mac_address` varchar(40) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(15) DEFAULT NULL,
  `email` varchar(60) NOT NULL,
  `gender` char(1) NOT NULL,
  PRIMARY KEY (`mac_address`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
CREATE TABLE IF NOT EXISTS `location` (
  `timestamp` datetime NOT NULL,
  `mac_address` varchar(40) NOT NULL,
  `location_id` int(11) NOT NULL,
  PRIMARY KEY (`timestamp`,`mac_address`),
  KEY `Qualification_fk1` (`location_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `location_lookup`
--

DROP TABLE IF EXISTS `location_lookup`;
CREATE TABLE IF NOT EXISTS `location_lookup` (
  `location_id` int(11) NOT NULL,
  `semantic_place` varchar(40) NOT NULL,
  PRIMARY KEY (`location_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
