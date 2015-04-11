-- name: create-useraccount!
-- Creates the useraccount table.
create table useraccount (
       userid serial,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       PRIMARY KEY (userid)
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
       userid int references user(userid),
       urdid int references urd(urdid),
       primary key (serial)
);

-- name: create-item!
-- Creates the item table.
create table item (
       itemid serial,
       itemname varchar(255),
       price int, -- for precision
       quantity int,
       PRIMARY KEY(itemid)
);

-- name: create-order!
-- Creates the order table.

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
SELECT userid, username, password
FROM useraccount
where username = :username

-- name: select-user
-- Select all users.
SELECT userid, username, password
FROM useraccount

-- name: select-item
-- Select all items.
SELECT * from item;

-- name: select-order
-- Select all orders.
SELECT * from order;
