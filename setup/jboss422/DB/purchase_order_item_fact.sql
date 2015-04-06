/*
SQLyog Community Edition- MySQL GUI v8.2 
MySQL - 5.0.51b-community-nt 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `purchase_order_item_fact` (
	`ORDER_ID` varchar (60),
	`ORDER_ITEM_SEQ_ID` varchar (60),
	`ORDER_DATE_DIM_ID` varchar (60),
	`PRODUCT_DIM_ID` varchar (60),
	`ORIG_CURRENCY_DIM_ID` varchar (60),
	`BILL_TO_CUSTOMER_DIM_ID` varchar (60),
	`ORDER_DATE` datetime ,
	`PRODUCT_STORE_ID` varchar (60),
	`SALES_CHANNEL_ENUM_ID` varchar (60),
	`SALE_CHANNEL` varchar (765),
	`ORDER_STATUS` varchar (765),
	`VISIT_ID` varchar (60),
	`INITIAL_REFERRER` varchar (765),
	`PRODUCT_PROMO_CODE` varchar (60),
	`CATEGORY_NAME` blob ,
	`QUANTITY` Decimal (20),
	`EXT_GROSS_AMOUNT` Decimal (20),
	`EXT_GROSS_COST` Decimal (20),
	`EXT_DISCOUNT_AMOUNT` Decimal (20),
	`EXT_NET_AMOUNT` Decimal (20),
	`EXT_SHIPPING_AMOUNT` Decimal (20),
	`EXT_TAX_AMOUNT` Decimal (20),
	`GROSS_SALES` Decimal (20),
	`GROSS_MERCHANDIZE_SALES` Decimal (20),
	`GROSS_MERCHANDIZE_PROFIT` Decimal (20),
	`GROSS_SHIPPING_PROFIT` Decimal (20),
	`GROSS_PROFIT` Decimal (20),
	`E_BAY` Decimal (20),
	`R_R_C` Decimal (20),
	`OTHER_FREE` Decimal (20),
	`N_B_O` Decimal (20),
	`COUNT_DATE` Decimal (20),
	`GROUP_NAME` varchar (300),
	`PARTY_ID` varchar (60),
	`PRODUCT_ID` varchar (60),
	`INVENTORY_ITEM_ID` varchar (60),
	`PRODUCT_CATEGORY_ID` varchar (60),
	`DESCRIPTION` varchar (765),
	`QUANTITY_UOM_ID` varchar (60),
	`QTY_ORDERED` Decimal (20),
	`QTY_SHIPPED` Decimal (20),
	`LAST_UPDATED_STAMP` datetime ,
	`LAST_UPDATED_TX_STAMP` datetime ,
	`CREATED_STAMP` datetime ,
	`CREATED_TX_STAMP` datetime 
); 
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10000','00001','_NF_','10029','10154','_NA_','2011-03-21 11:07:03',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10010','',NULL,NULL,'100.000000','565.000','600.000','0.000','565.000','0.000','0.000','565.000','565.000','-35.000','0.000','-35.000','7.000','2.000','5.000','-49.000','1.000','DemoSupplier',NULL,'GZ-1000',NULL,NULL,'Tiny Gizmo',NULL,'100.000000','100.000000','2011-03-21 11:07:34','2011-03-21 11:07:33','2011-03-21 11:07:04','2011-03-21 11:07:04');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10022','00001','_NF_','_NF_','_NF_','_NA_','2010-10-25 11:16:42',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10110','',NULL,NULL,'1000.000000','5650.000','6000.000','0.000','5650.000','0.000','0.000','5650.000','5650.000','-350.000','0.000','-350.000','7.000','2.000','5.000','-364.000','1.000','DemoSupplier',NULL,'GZ-1000',NULL,NULL,'Tiny Gizmo',NULL,'1000.000000','1000.000000','2010-10-25 11:17:11','2010-10-25 11:17:09','2010-10-25 11:16:42','2010-10-25 11:16:42');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10030','00001','_NF_','10091','10154','_NA_','2010-10-27 12:30:19',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10304','https://localhost:8443/catalog/control/EditProductStore?productStoreId=10000',NULL,NULL,'1000.000000','10000.000','10000.000','0.000','10000.000','0.000','0.000','10000.000','10000.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10001',NULL,NULL,'Dettol',NULL,'1000.000000','1000.000000','2010-10-27 12:31:20','2010-10-27 12:31:19','2010-10-27 12:30:19','2010-10-27 12:30:19');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10066','00001','_NF_','10100','10154','_NA_','2010-10-29 15:48:24',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10374','https://localhost:8443/ordermgr/control/processorder',NULL,NULL,'100000.000000','18000000.000','18000000.000','0.000','18000000.000','0.000','180000.000','18000000.000','18000000.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10010',NULL,NULL,'Carrier-Global',NULL,'100000.000000','100000.000000','2010-10-29 15:49:02','2010-10-29 15:49:00','2010-10-29 15:48:25','2010-10-29 15:48:25');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10081','00001','_NF_','10100','10154','_NA_','2010-11-03 11:02:24',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10480','',NULL,NULL,'20.000000','3600.000','3600.000','0.000','3600.000','0.000','36.000','3600.000','3600.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10010',NULL,NULL,'Carrier-Global',NULL,'20.000000','20.000000','2010-11-03 11:14:21','2010-11-03 11:14:19','2010-11-03 11:02:24','2010-11-03 11:02:24');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10082','00001','_NF_','10100','10154','_NA_','2010-11-03 11:52:44',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10480','',NULL,NULL,'10000.000000','1800000.000','1800000.000','0.000','1800000.000','0.000','18000.000','1800000.000','1800000.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10010',NULL,NULL,'Carrier-Global',NULL,'10000.000000','10000.000000','2010-11-03 11:53:14','2010-11-03 11:53:12','2010-11-03 11:52:44','2010-11-03 11:52:44');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10085','00001','_NF_','10100','10154','_NA_','2010-11-03 12:47:46',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10480','',NULL,NULL,'2.000000','360.000','360.000','0.000','360.000','0.000','3.600','360.000','360.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10010',NULL,NULL,'Carrier-Global',NULL,'2.000000','2.000000','2010-11-03 12:49:14','2010-11-03 12:49:13','2010-11-03 12:47:46','2010-11-03 12:47:46');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('10086','00001','_NF_','10100','10154','_NA_','2010-11-03 12:57:11',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Approved','10480','',NULL,NULL,'8.000000','1440.000','1440.000','0.000','1440.000','0.000','14.400','1440.000','1440.000','0.000','0.000','0.000','7.000','2.000','5.000','-14.000','1.000','DemoSupplier',NULL,'10010',NULL,NULL,'Carrier-Global',NULL,'8.000000','8.000000','2010-11-03 12:59:23','2010-11-03 12:59:22','2010-11-03 12:57:11','2010-11-03 12:57:11');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('DEMO10091','00001','_NF_','_NF_','_NF_','_NA_','2008-06-10 13:27:07','9000','UNKNWN_SALES_CHANNEL','Unknown Channel','Created','10000','https://localhost:8443/humanresext/control/CreateSalaryStructure',NULL,NULL,'5.000000','108.000','96.000','0.000','108.000','0.000','0.000','108.000','108.000','12.000','0.000','12.000','7.000','2.000','5.000','-2.000','1.000','DemoSupplier',NULL,'GZ-2644',NULL,NULL,'Round Gizmo',NULL,'5.000000','5.000000','2011-03-18 12:47:18','2011-03-18 12:47:17','2010-10-21 16:35:16','2010-10-21 16:35:16');
insert into `purchase_order_item_fact` (`ORDER_ID`, `ORDER_ITEM_SEQ_ID`, `ORDER_DATE_DIM_ID`, `PRODUCT_DIM_ID`, `ORIG_CURRENCY_DIM_ID`, `BILL_TO_CUSTOMER_DIM_ID`, `ORDER_DATE`, `PRODUCT_STORE_ID`, `SALES_CHANNEL_ENUM_ID`, `SALE_CHANNEL`, `ORDER_STATUS`, `VISIT_ID`, `INITIAL_REFERRER`, `PRODUCT_PROMO_CODE`, `CATEGORY_NAME`, `QUANTITY`, `EXT_GROSS_AMOUNT`, `EXT_GROSS_COST`, `EXT_DISCOUNT_AMOUNT`, `EXT_NET_AMOUNT`, `EXT_SHIPPING_AMOUNT`, `EXT_TAX_AMOUNT`, `GROSS_SALES`, `GROSS_MERCHANDIZE_SALES`, `GROSS_MERCHANDIZE_PROFIT`, `GROSS_SHIPPING_PROFIT`, `GROSS_PROFIT`, `E_BAY`, `R_R_C`, `OTHER_FREE`, `N_B_O`, `COUNT_DATE`, `GROUP_NAME`, `PARTY_ID`, `PRODUCT_ID`, `INVENTORY_ITEM_ID`, `PRODUCT_CATEGORY_ID`, `DESCRIPTION`, `QUANTITY_UOM_ID`, `QTY_ORDERED`, `QTY_SHIPPED`, `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP`) values('Demo1001','00001','_NF_','_NF_','_NF_','_NA_','2009-08-13 17:45:50',NULL,'UNKNWN_SALES_CHANNEL','Unknown Channel','Completed',NULL,NULL,NULL,NULL,'2.000000','48.000','38.400','0.000','48.000','0.000','0.000','48.000','48.000','9.600','0.000','9.600','7.000','2.000','5.000','-4.400','1.000','DemoSupplier',NULL,'GZ-2644',NULL,NULL,'Round Gizmo',NULL,'2.000000','2.000000','2011-03-18 12:47:18','2011-03-18 12:47:17','2010-10-21 16:35:16','2010-10-21 16:35:16');