/*
SQLyog Community Edition- MySQL GUI v8.2 
MySQL - 5.0.51b-community-nt 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `facility_dimension` (
	`DIMENSION_ID` varchar (60),
	`FACILITY_ID` varchar (60),
	`DESCRIPTION` varchar (765),
	`LAST_UPDATED_STAMP` datetime ,
	`LAST_UPDATED_TX_STAMP` datetime ,
	`CREATED_STAMP` datetime ,
	`CREATED_TX_STAMP` datetime 
); 
