-- name: create-useraccount!
-- Creates the useraccount table.
create table useraccount (
       useraccountid serial,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       PRIMARY KEY (useraccountid)
);

-- name: create-urd!
-- Creates the 'urd' user role definition table.
create table urd (
       urdid serial,
       name varchar(80),
       primary key (urdid)
);

-- name: create-userrole!
-- Creates the userrole table. 
create table userrole (
       userroleid serial,
       useraccountid int references user(useraccountid),
       urdid int references urd(urdid),
       primary key (serial)
);

-- name: create-item!
-- Creates the item table for containing item name, price
-- and quantity tuples.
create table item (
       itemid serial,
       itemname varchar(255),
       price int, -- for precision
       quantity int,
       PRIMARY KEY(itemid)
);

-- name: create-orderitem!
-- Creates the orderitem table for containing the items
-- in an order.
create table orderitem (
       orderitemid serial,
       orderid int references order(orderid),
       itemid int references item(itemid),
       quantity int,
       price int,
       coupon varchar(255),
       PRIMARY KEY(orderitemid)
);

create table status (
       statusid serial,
       name varchar(255)
);

-- name: create-order!
-- Creates the order table for recording the dates orders
-- have been placed and their status.

create table order (
       orderid serial,
       useraccountid int references useraccount(useraccountid),
       statusid int references status(statusid)
       placementtime timestamp,
       PRIMARY KEY(orderid)
);

-- name: insert-user!
-- Inserts a user with the given username and password.
INSERT INTO useraccount (username, password)
VALUES (:username, :password);

-- name: insert-item!
-- Inserts an item with the given name, price and quantity.
INSERT INTO item (itemname, price, quantity)
Values (:itemname, :price, :quantity);

-- name: find-user+pass
-- Queries for a user with the given password and username.
select * from useraccount
where
(username = :username)
and
(password = :password);

-- name: find-user
-- Selects for a user with the given username.
SELECT useraccountid, username, password
FROM useraccount
where username = :username

-- name: select-user
-- Select all users.
SELECT useraccountid, username, password
FROM useraccount

-- name: select-item
-- Select all items.
SELECT * from item;

-- name: select-order
-- Select all orders.
SELECT * from order;
