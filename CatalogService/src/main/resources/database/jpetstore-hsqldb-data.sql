--
--    Copyright 2010-2023 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       https://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

create table SUPPLIER (
    suppid int not null,
    name varchar(80) null,
    status varchar(2) not null,
    addr1 varchar(80) null,
    addr2 varchar(80) null,
    city varchar(80) null,
    state varchar(80) null,
    zip varchar(5) null,
    phone varchar(80) null,
    constraint pk_supplier primary key (suppid)
);

create table CATEGORY (
	catid varchar(10) not null,
	name varchar(80) null,
	descn varchar(255) null,
	constraint pk_category primary key (catid)
);

create table PRODUCT (
    productid varchar(10) not null,
    category varchar(10) not null,
    name varchar(80) null,
    descn varchar(255) null,
    constraint pk_product primary key (productid),
        constraint fk_product_1 foreign key (category)
        references CATEGORY (catid)
);

create index PRODUCTCAT on PRODUCT (category);
create index PRODUCTNAME on PRODUCT (name);

create table ITEM (
    itemid varchar(10) not null,
    productid varchar(10) not null,
    listprice decimal(10,2) null,
    unitcost decimal(10,2) null,
    supplier int null,
    status varchar(2) null,
    attr1 varchar(80) null,
    attr2 varchar(80) null,
    attr3 varchar(80) null,
    attr4 varchar(80) null,
    attr5 varchar(80) null,
    constraint pk_item primary key (itemid),
        constraint fk_item_1 foreign key (productid)
        references PRODUCT (productid),
        constraint fk_item_2 foreign key (supplier)
        references SUPPLIER (suppid)
);

create index ITEMPROD on ITEM (productid);

create table INVENTORY (
    itemid varchar(10) not null,
    qty int not null,
    constraint pk_inventory primary key (itemid)
);

INSERT INTO CATEGORY VALUES ('FISH','Fish','<image src="/catalog/images/fish_icon.gif"><font size="5" color="blue"> Fish</font>');
INSERT INTO CATEGORY VALUES ('DOGS','Dogs','<image src="/catalog/images/dogs_icon.gif"><font size="5" color="blue"> Dogs</font>');
INSERT INTO CATEGORY VALUES ('REPTILES','Reptiles','<image src="/catalog/images/reptiles_icon.gif"><font size="5" color="blue"> Reptiles</font>');
INSERT INTO CATEGORY VALUES ('CATS','Cats','<image src="/catalog/images/cats_icon.gif"><font size="5" color="blue"> Cats</font>');
INSERT INTO CATEGORY VALUES ('BIRDS','Birds','<image src="/catalog/images/birds_icon.gif"><font size="5" color="blue"> Birds</font>');

INSERT INTO PRODUCT VALUES ('FI-SW-01','FISH','Angelfish','<image src="/catalog/images/fish1.gif">Salt Water fish from Australia');
INSERT INTO PRODUCT VALUES ('FI-SW-02','FISH','Tiger Shark','<image src="/catalog/images/fish4.gif">Salt Water fish from Australia');
INSERT INTO PRODUCT VALUES ('FI-FW-01','FISH', 'Koi','<image src="/catalog/images/fish3.gif">Fresh Water fish from Japan');
INSERT INTO PRODUCT VALUES ('FI-FW-02','FISH', 'Goldfish','<image src="/catalog/images/fish2.gif">Fresh Water fish from China');
INSERT INTO PRODUCT VALUES ('K9-BD-01','DOGS','Bulldog','<image src="/catalog/images/dog2.gif">Friendly dog from England');
INSERT INTO PRODUCT VALUES ('K9-PO-02','DOGS','Poodle','<image src="/catalog/images/dog6.gif">Cute dog from France');
INSERT INTO PRODUCT VALUES ('K9-DL-01','DOGS', 'Dalmation','<image src="/catalog/images/dog5.gif">Great dog for a Fire Station');
INSERT INTO PRODUCT VALUES ('K9-RT-01','DOGS', 'Golden Retriever','<image src="/catalog/images/dog1.gif">Great family dog');
INSERT INTO PRODUCT VALUES ('K9-RT-02','DOGS', 'Labrador Retriever','<image src="/catalog/images/dog5.gif">Great hunting dog');
INSERT INTO PRODUCT VALUES ('K9-CW-01','DOGS', 'Chihuahua','<image src="/catalog/images/dog4.gif">Great companion dog');
INSERT INTO PRODUCT VALUES ('RP-SN-01','REPTILES','Rattlesnake','<image src="/catalog/images/snake1.gif">Doubles as a watch dog');
INSERT INTO PRODUCT VALUES ('RP-LI-02','REPTILES','Iguana','<image src="/catalog/images/lizard1.gif">Friendly green friend');
INSERT INTO PRODUCT VALUES ('FL-DSH-01','CATS','Manx','<image src="/catalog/images/cat2.gif">Great for reducing mouse populations');
INSERT INTO PRODUCT VALUES ('FL-DLH-02','CATS','Persian','<image src="/catalog/images/cat1.gif">Friendly house cat, doubles as a princess');
INSERT INTO PRODUCT VALUES ('AV-CB-01','BIRDS','Amazon Parrot','<image src="/catalog/images/bird2.gif">Great companion for up to 75 years');
INSERT INTO PRODUCT VALUES ('AV-SB-02','BIRDS','Finch','<image src="/catalog/images/bird1.gif">Great stress reliever');

INSERT INTO SUPPLIER VALUES (1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797');
INSERT INTO SUPPLIER VALUES (2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797');

INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-1','FI-SW-01',16.50,10.00,1,'P','Large');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-2','FI-SW-01',16.50,10.00,1,'P','Small');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-3','FI-SW-02',18.50,12.00,1,'P','Toothless');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-4','FI-FW-01',18.50,12.00,1,'P','Spotted');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-5','FI-FW-01',18.50,12.00,1,'P','Spotless');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-6','K9-BD-01',18.50,12.00,1,'P','Male Adult');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-7','K9-BD-01',18.50,12.00,1,'P','Female Puppy');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-8','K9-PO-02',18.50,12.00,1,'P','Male Puppy');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-9','K9-DL-01',18.50,12.00,1,'P','Spotless Male Puppy');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-10','K9-DL-01',18.50,12.00,1,'P','Spotted Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-11','RP-SN-01',18.50,12.00,1,'P','Venomless');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-12','RP-SN-01',18.50,12.00,1,'P','Rattleless');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-13','RP-LI-02',18.50,12.00,1,'P','Green Adult');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-14','FL-DSH-01',58.50,12.00,1,'P','Tailless');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-15','FL-DSH-01',23.50,12.00,1,'P','With tail');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-16','FL-DLH-02',93.50,12.00,1,'P','Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-17','FL-DLH-02',93.50,12.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-18','AV-CB-01',193.50,92.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-19','AV-SB-02',15.50, 2.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-20','FI-FW-02',5.50, 2.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-21','FI-FW-02',5.29, 1.00,1,'P','Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-22','K9-RT-02',135.50, 100.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-23','K9-RT-02',145.49, 100.00,1,'P','Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-24','K9-RT-02',255.50, 92.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-25','K9-RT-02',325.29, 90.00,1,'P','Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-26','K9-CW-01',125.50, 92.00,1,'P','Adult Male');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-27','K9-CW-01',155.29, 90.00,1,'P','Adult Female');
INSERT INTO  ITEM (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES('EST-28','K9-RT-01',155.29, 90.00,1,'P','Adult Female');

INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-1',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-2',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-3',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-4',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-5',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-6',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-7',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-8',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-9',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-10',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-11',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-12',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-13',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-14',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-15',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-16',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-17',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-18',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-19',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-20',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-21',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-22',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-23',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-24',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-25',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-26',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-27',10000);
INSERT INTO INVENTORY (itemid, qty ) VALUES ('EST-28',10000);

