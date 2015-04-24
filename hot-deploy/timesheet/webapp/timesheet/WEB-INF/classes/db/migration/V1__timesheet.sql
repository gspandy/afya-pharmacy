/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.1.51-community-log : Database - bootstrap
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `attendance_register` */

DROP TABLE IF EXISTS `attendance_register`;

CREATE TABLE `attendance_register` (
  `ID` varchar(255) NOT NULL,
  `DEPARTMENT_ID` varchar(255) DEFAULT NULL,
  `STATUS` varchar(255) NOT NULL,
  `TIMESHEET_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_htugl7gvlh2ks8p4o5d1jpa0g` (`TIMESHEET_ID`),
  CONSTRAINT `FK_htugl7gvlh2ks8p4o5d1jpa0g` FOREIGN KEY (`TIMESHEET_ID`) REFERENCES `timesheet_entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `domain_event_entry` */

DROP TABLE IF EXISTS `domain_event_entry`;

CREATE TABLE `domain_event_entry` (
  `AGGREGATE_IDENTIFIER` varchar(255) NOT NULL,
  `SEQUENCE_NUMBER` bigint(20) NOT NULL,
  `TYPE` varchar(255) NOT NULL,
  `EVENT_IDENTIFIER` varchar(255) NOT NULL,
  `META_DATA` longblob,
  `PAYLOAD` longblob NOT NULL,
  `PAYLOAD_REVISION` varchar(255) DEFAULT NULL,
  `PAYLOAD_TYPE` varchar(255) NOT NULL,
  `TIME_STAMP` varchar(255) NOT NULL,
  PRIMARY KEY (`AGGREGATE_IDENTIFIER`,`SEQUENCE_NUMBER`,`TYPE`),
  UNIQUE KEY `UK_j3lv9g19mdy61dcsdxrvftg85` (`EVENT_IDENTIFIER`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `employee_attendance_entry` */

DROP TABLE IF EXISTS `employee_attendance_entry`;

CREATE TABLE `employee_attendance_entry` (
  `ID` varchar(255) NOT NULL,
  `SDPRESENT` varchar(255) DEFAULT NULL,
  `SDPRESENT_ON_FREE_DAY` varchar(255) DEFAULT NULL,
  `SDTRANSFERRED_AND_PRESENT` varchar(255) DEFAULT NULL,
  `SDWRONG_DEPARTMENT_AND_PRESENT` varchar(255) DEFAULT NULL,
  `ABSENT` varchar(255) DEFAULT NULL,
  `ABSENT_AND_SUSPENSION` varchar(255) DEFAULT NULL,
  `ANNUAL_LEAVE` varchar(255) DEFAULT NULL,
  `ANNUAL_PASSAGE_LEAVE` varchar(255) DEFAULT NULL,
  `AWAY_ON_DUTY` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `COMP_LEAVE` varchar(255) DEFAULT NULL,
  `D_OVERTIME` varchar(255) DEFAULT NULL,
  `EMPLOYEE_ID` varchar(255) DEFAULT NULL,
  `EXCESS_FREE_DAYS` varchar(255) DEFAULT NULL,
  `FREEDAYS` varchar(255) DEFAULT NULL,
  `MATERNITY_LEAVE` varchar(255) DEFAULT NULL,
  `N_OVER_TIME` varchar(255) DEFAULT NULL,
  `OTHER_UNPAID_LEAVE` varchar(255) DEFAULT NULL,
  `PRESENT` varchar(255) DEFAULT NULL,
  `PRESENT_ON_FREE_DAY` varchar(255) DEFAULT NULL,
  `SICK_LEAVE` varchar(255) DEFAULT NULL,
  `SPECIAL_LEAVE` varchar(255) DEFAULT NULL,
  `STANDBY` varchar(255) DEFAULT NULL,
  `SUSPENSION` varchar(255) DEFAULT NULL,
  `TOTAL_SHIFT_DIFFERENTIAL` varchar(255) DEFAULT NULL,
  `ATTENDANCE_REGISTER_ID` varchar(255) DEFAULT NULL,
  `TOTAL_DAYS` varchar(255) DEFAULT NULL,
  `TERMINATED` varchar(255) DEFAULT NULL,
  `RESIGNED` varchar(255) DEFAULT NULL,
  `TRANSFERRED` varchar(255) DEFAULT NULL,
  `DESERTED` varchar(255) DEFAULT NULL,
  `WRONG_DEPARTMENT` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_rs8jljwvhmsbb9kevlcujj6og` (`ATTENDANCE_REGISTER_ID`),
  CONSTRAINT `FK_rs8jljwvhmsbb9kevlcujj6og` FOREIGN KEY (`ATTENDANCE_REGISTER_ID`) REFERENCES `attendance_register` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `employeeattendanceentry_day_entries` */

DROP TABLE IF EXISTS `employeeattendanceentry_day_entries`;

CREATE TABLE `employeeattendanceentry_day_entries` (
  `EMPLOYEEATTENDANCEENTRY` varchar(255) NOT NULL,
  `D_OVERTIME` varchar(255) DEFAULT NULL,
  `DATE` date DEFAULT NULL,
  `N_OVERTIME` varchar(255) DEFAULT NULL,
  `SHIFT_ONE` varchar(255) DEFAULT NULL,
  `SHIFT_TWO` varchar(255) DEFAULT NULL,
  KEY `FK_10ya7whuq2pfoo634o8mvghmj` (`EMPLOYEEATTENDANCEENTRY`),
  CONSTRAINT `FK_10ya7whuq2pfoo634o8mvghmj` FOREIGN KEY (`EMPLOYEEATTENDANCEENTRY`) REFERENCES `employee_attendance_entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `schedule` */

DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `ID` varchar(255) NOT NULL,
  `LAST_EVENT_SEQUENCE_NUMBER` bigint(20) DEFAULT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `FROM_DATE` date NOT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `TO_DATE` date NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `timesheet_entry` */

DROP TABLE IF EXISTS `timesheet_entry`;

CREATE TABLE `timesheet_entry` (
  `ID` varchar(255) NOT NULL,
  `LAST_EVENT_SEQUENCE_NUMBER` bigint(20) DEFAULT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `SCHEDULE_ID` varchar(255) DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
