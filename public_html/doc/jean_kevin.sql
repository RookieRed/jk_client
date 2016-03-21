-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Client :  127.0.0.1
-- Généré le :  Jeu 17 Mars 2016 à 17:59
-- Version du serveur :  5.6.17
-- Version de PHP :  5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `jean_kevin`
--

-- --------------------------------------------------------

--
-- Structure de la table `image`
--

CREATE TABLE IF NOT EXISTS `image` (
  `chemin` varchar(100) COLLATE utf8_bin NOT NULL,
  `identifiant_jk` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`chemin`),
  KEY `identifiant_jk` (`identifiant_jk`),
  KEY `identifiant_jk_2` (`identifiant_jk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Contenu de la table `image`
--

INSERT INTO `image` (`chemin`, `identifiant_jk`) VALUES
('img/avatars/jk1/avatar.jpg', 'jk1'),
('img/avatars/jk1/avatar1.jpg', 'jk1');

-- --------------------------------------------------------

--
-- Structure de la table `jean_kevin`
--

CREATE TABLE IF NOT EXISTS `jean_kevin` (
  `identifiant` varchar(20) COLLATE utf8_bin NOT NULL,
  `nom` varchar(30) COLLATE utf8_bin NOT NULL,
  `prenom` varchar(20) COLLATE utf8_bin NOT NULL,
  `mail` varchar(75) COLLATE utf8_bin NOT NULL,
  `photo` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `mot_de_passe` varchar(32) COLLATE utf8_bin NOT NULL,
  `actif` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`identifiant`),
  UNIQUE KEY `mail` (`mail`),
  UNIQUE KEY `mail_2` (`mail`),
  UNIQUE KEY `mail_3` (`mail`),
  UNIQUE KEY `mail_4` (`mail`),
  UNIQUE KEY `photo` (`photo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Contenu de la table `jean_kevin`
--

INSERT INTO `jean_kevin` (`identifiant`, `nom`, `prenom`, `mail`, `photo`, `mot_de_passe`, `actif`) VALUES
('jk1', 'Cédric', 'Eloundou', 'mail@mail.com', 'img/avatars/jk1/avatar.jpg', 'yolo', 1),
('jk2', 'Ced', 'dric', '', NULL, 'pass', 0),
('j-k0', 'nom0', 'pren0', 'jk0@mail.cm', NULL, 'pass', 0),
('j-k1', 'nom1', 'pren1', 'jk1@mail.cm', NULL, 'pass', 0),
('j-k2', 'nom2', 'pren2', 'jk2@mail.cm', NULL, 'pass', 0),
('j-k3', 'nom3', 'pren3', 'jk3@mail.cm', NULL, 'pass', 0),
('j-k4', 'nom4', 'pren4', 'jk4@mail.cm', NULL, 'pass', 0),
('j-k5', 'nom5', 'pren5', 'jk5@mail.cm', NULL, 'pass', 0),
('j-k6', 'nom6', 'pren6', 'jk6@mail.cm', NULL, 'pass', 0),
('j-k7', 'nom7', 'pren7', 'jk7@mail.cm', NULL, 'pass', 0),
('j-k8', 'nom8', 'pren8', 'jk8@mail.cm', NULL, 'pass', 0),
('j-k9', 'nom9', 'pren9', 'jk9@mail.cm', NULL, 'pass', 0),
('log1', 'nom1', 'prenom1', 'log1@mail.com', NULL, 'pass', 1);

-- --------------------------------------------------------

--
-- Structure de la table `lieu`
--

CREATE TABLE IF NOT EXISTS `lieu` (
  `id` int(10) unsigned NOT NULL,
  `schema` varchar(100) COLLATE utf8_bin NOT NULL,
  `libelle` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Structure de la table `position`
--

CREATE TABLE IF NOT EXISTS `position` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `identifiant_jk` varchar(20) COLLATE utf8_bin NOT NULL,
  `id_lieu` int(11) NOT NULL,
  `jour` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `identifiant_jk` (`identifiant_jk`,`id_lieu`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `r_jk_lieu`
--

CREATE TABLE IF NOT EXISTS `r_jk_lieu` (
  `identifiant_jk` varchar(20) COLLATE utf8_bin NOT NULL,
  `id_lieu` int(11) NOT NULL,
  PRIMARY KEY (`identifiant_jk`,`id_lieu`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Structure de la table `r_lier`
--

CREATE TABLE IF NOT EXISTS `r_lier` (
  `identifiant1` varchar(20) COLLATE utf8_bin NOT NULL,
  `identifiant2` varchar(20) COLLATE utf8_bin NOT NULL,
  `surnom1` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `surnom2` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `effectif` tinyint(1) NOT NULL,
  PRIMARY KEY (`identifiant1`,`identifiant2`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Contenu de la table `r_lier`
--

INSERT INTO `r_lier` (`identifiant1`, `identifiant2`, `surnom1`, `surnom2`, `effectif`) VALUES
('jk2', 'jk1', NULL, NULL, 0),
('jk1', 'j-k0', NULL, NULL, 1),
('jk1', 'j-k1', NULL, NULL, 1),
('jk1', 'j-k2', NULL, NULL, 1),
('jk1', 'j-k3', NULL, NULL, 1),
('jk1', 'j-k4', NULL, NULL, 1),
('jk1', 'j-k5', NULL, NULL, 1),
('jk1', 'j-k6', NULL, NULL, 1),
('jk1', 'j-k7', NULL, NULL, 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
