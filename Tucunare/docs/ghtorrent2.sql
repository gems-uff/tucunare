-- DBLite SQL Dump Version: 0.51
-- http://www.dblite.com
-- May 31, 2015 at 11:29 PM

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
--
-- Current Database: `ghtorrent`
--

/*!40000 DROP DATABASE IF EXISTS `ghtorrent`*/;

CREATE DATABASE IF NOT EXISTS `ghtorrent`;

USE `ghtorrent`;


--
-- Table structure for table `commit_comments`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `commit_comments`;
CREATE TABLE `commit_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `body` varchar(256) DEFAULT NULL,
  `line` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `comment_id` int(11) NOT NULL,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`comment_id`),
  KEY `commit_id` (`commit_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `commit_comments_ibfk_1` FOREIGN KEY (`commit_id`) REFERENCES `commits` (`id`),
  CONSTRAINT `commit_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2575079 DEFAULT CHARSET=utf8;



--
-- Table structure for table `commit_parents`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `commit_parents`;
CREATE TABLE `commit_parents` (
  `commit_id` int(11) NOT NULL,
  `parent_id` int(11) NOT NULL,
  PRIMARY KEY (`commit_id`,`parent_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `commit_parents_ibfk_1` FOREIGN KEY (`commit_id`) REFERENCES `commits` (`id`),
  CONSTRAINT `commit_parents_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `commits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `commits`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `commits`;
CREATE TABLE `commits` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sha` varchar(40) DEFAULT NULL,
  `author_id` int(11) DEFAULT NULL,
  `committer_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sha` (`sha`),
  KEY `author_id` (`author_id`),
  KEY `committer_id` (`committer_id`),
  KEY `project_id` (`project_id`),
  CONSTRAINT `commits_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`),
  CONSTRAINT `commits_ibfk_2` FOREIGN KEY (`committer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `commits_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=260970663 DEFAULT CHARSET=utf8;



--
-- Table structure for table `followers`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `followers`;
CREATE TABLE `followers` (
  `follower_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`follower_id`,`user_id`),
  KEY `follower_id` (`user_id`),
  CONSTRAINT `follower_fk1` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`),
  CONSTRAINT `follower_fk2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `issue_comments`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `issue_comments`;
CREATE TABLE `issue_comments` (
  `issue_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `comment_id` mediumtext NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  KEY `issue_id` (`issue_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `issue_comments_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`id`),
  CONSTRAINT `issue_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `issue_events`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `issue_events`;
CREATE TABLE `issue_events` (
  `event_id` mediumtext NOT NULL,
  `issue_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL,
  `action_specific` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  KEY `issue_id` (`issue_id`),
  KEY `actor_id` (`actor_id`),
  CONSTRAINT `issue_events_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`id`),
  CONSTRAINT `issue_events_ibfk_2` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `issue_labels`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `issue_labels`;
CREATE TABLE `issue_labels` (
  `label_id` int(11) NOT NULL,
  `issue_id` int(11) NOT NULL,
  PRIMARY KEY (`issue_id`,`label_id`),
  KEY `label_id` (`label_id`),
  CONSTRAINT `issue_labels_ibfk_1` FOREIGN KEY (`label_id`) REFERENCES `repo_labels` (`id`),
  CONSTRAINT `issue_labels_ibfk_2` FOREIGN KEY (`issue_id`) REFERENCES `issues` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `issues`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `issues`;
CREATE TABLE `issues` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `repo_id` int(11) DEFAULT NULL,
  `reporter_id` int(11) DEFAULT NULL,
  `assignee_id` int(11) DEFAULT NULL,
  `pull_request` tinyint(1) NOT NULL,
  `pull_request_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  `issue_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `repo_id` (`repo_id`),
  KEY `reporter_id` (`reporter_id`),
  KEY `assignee_id` (`assignee_id`),
  KEY `pull_request_id` (`pull_request_id`),
  KEY `issue_id` (`issue_id`),
  CONSTRAINT `issues_ibfk_1` FOREIGN KEY (`repo_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `issues_ibfk_2` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `issues_ibfk_3` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `issues_ibfk_4` FOREIGN KEY (`pull_request_id`) REFERENCES `pull_requests` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18240405 DEFAULT CHARSET=utf8;



--
-- Table structure for table `organization_members`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `organization_members`;
CREATE TABLE `organization_members` (
  `org_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`org_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `organization_members_ibfk_1` FOREIGN KEY (`org_id`) REFERENCES `users` (`id`),
  CONSTRAINT `organization_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `project_commits`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `project_commits`;
CREATE TABLE `project_commits` (
  `project_id` int(11) NOT NULL DEFAULT '0',
  `commit_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`project_id`,`commit_id`),
  KEY `commit_id` (`commit_id`),
  CONSTRAINT `project_commits_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `project_commits_ibfk_2` FOREIGN KEY (`commit_id`) REFERENCES `commits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `project_members`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `project_members`;
CREATE TABLE `project_members` (
  `repo_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`repo_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `project_members_ibfk_1` FOREIGN KEY (`repo_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `project_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `projects`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `projects`;
CREATE TABLE `projects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  `forked_from` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`owner_id`),
  KEY `owner_id` (`owner_id`),
  KEY `forked_from` (`forked_from`),
  CONSTRAINT `projects_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `projects_ibfk_2` FOREIGN KEY (`forked_from`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20312859 DEFAULT CHARSET=utf8;



--
-- Table structure for table `pull_request_comments`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `pull_request_comments`;
CREATE TABLE `pull_request_comments` (
  `pull_request_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `comment_id` mediumtext NOT NULL,
  `position` int(11) DEFAULT NULL,
  `body` varchar(256) DEFAULT NULL,
  `commit_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  UNIQUE KEY `comment_id` (`comment_id`(9)),
  KEY `pull_request_id` (`pull_request_id`),
  KEY `user_id` (`user_id`),
  KEY `commit_id` (`commit_id`),
  CONSTRAINT `pull_request_comments_ibfk_1` FOREIGN KEY (`pull_request_id`) REFERENCES `pull_requests` (`id`),
  CONSTRAINT `pull_request_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `pull_request_comments_ibfk_3` FOREIGN KEY (`commit_id`) REFERENCES `commits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `pull_request_commits`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `pull_request_commits`;
CREATE TABLE `pull_request_commits` (
  `pull_request_id` int(11) NOT NULL,
  `commit_id` int(11) NOT NULL,
  PRIMARY KEY (`pull_request_id`,`commit_id`),
  KEY `commit_id` (`commit_id`),
  CONSTRAINT `pull_request_commits_ibfk_1` FOREIGN KEY (`pull_request_id`) REFERENCES `pull_requests` (`id`),
  CONSTRAINT `pull_request_commits_ibfk_2` FOREIGN KEY (`commit_id`) REFERENCES `commits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `pull_request_history`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `pull_request_history`;
CREATE TABLE `pull_request_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pull_request_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  `action` varchar(255) NOT NULL,
  `actor_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pull_request_id` (`pull_request_id`),
  KEY `pull_request_history_ibfk_2` (`actor_id`),
  CONSTRAINT `pull_request_history_ibfk_1` FOREIGN KEY (`pull_request_id`) REFERENCES `pull_requests` (`id`),
  CONSTRAINT `pull_request_history_ibfk_2` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34366207 DEFAULT CHARSET=utf8;



--
-- Table structure for table `pull_requests`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `pull_requests`;
CREATE TABLE `pull_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `head_repo_id` int(11) DEFAULT NULL,
  `base_repo_id` int(11) NOT NULL,
  `head_commit_id` int(11) DEFAULT NULL,
  `base_commit_id` int(11) NOT NULL,
  `pullreq_id` int(11) NOT NULL,
  `intra_branch` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pullreq_id` (`pullreq_id`,`base_repo_id`),
  KEY `head_repo_id` (`head_repo_id`),
  KEY `base_repo_id` (`base_repo_id`),
  KEY `head_commit_id` (`head_commit_id`),
  KEY `base_commit_id` (`base_commit_id`),
  KEY `idx_pullreq_id` (`pullreq_id`),
  CONSTRAINT `pull_requests_ibfk_1` FOREIGN KEY (`head_repo_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `pull_requests_ibfk_2` FOREIGN KEY (`base_repo_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `pull_requests_ibfk_3` FOREIGN KEY (`head_commit_id`) REFERENCES `commits` (`id`),
  CONSTRAINT `pull_requests_ibfk_4` FOREIGN KEY (`base_commit_id`) REFERENCES `commits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7643925 DEFAULT CHARSET=utf8;



--
-- Table structure for table `repo_labels`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `repo_labels`;
CREATE TABLE `repo_labels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `repo_id` int(11) DEFAULT NULL,
  `name` varchar(24) NOT NULL,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `repo_id` (`repo_id`),
  CONSTRAINT `repo_labels_ibfk_1` FOREIGN KEY (`repo_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48723323 DEFAULT CHARSET=utf8;



--
-- Table structure for table `repo_milestones`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `repo_milestones`;
CREATE TABLE `repo_milestones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `repo_id` int(11) DEFAULT NULL,
  `name` varchar(24) NOT NULL,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `repo_id` (`repo_id`),
  CONSTRAINT `repo_milestones_ibfk_1` FOREIGN KEY (`repo_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `schema_info`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `schema_info`;
CREATE TABLE `schema_info` (
  `version` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



--
-- Table structure for table `users`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  `type` varchar(255) NOT NULL DEFAULT 'USR',
  `fake` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  KEY `users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8212144 DEFAULT CHARSET=utf8;



--
-- Table structure for table `watchers`
--
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `watchers`;
CREATE TABLE `watchers` (
  `repo_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_ref_id` varchar(24) NOT NULL DEFAULT '0',
  PRIMARY KEY (`repo_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `watchers_ibfk_1` FOREIGN KEY (`repo_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `watchers_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
