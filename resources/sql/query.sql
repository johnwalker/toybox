-- name: create-useraccount!
-- Creates the useraccount table.
create table useraccount (
       useraccountid int auto_increment,
       username varchar(255) unique not null,
       password varchar(255) not null,
       userrole enum ('user', 'staff', 'manager') not null,
       primary key (useraccountid));

-- name: drop-useraccount!
-- Deletes the useraccount table.
drop table useraccount;

-- name: create-item!
-- Creates the item table for containing item name, price
-- and quantity tuples.
create table item (
       itemid int auto_increment,
       itemname varchar(255),
       price int, -- for precision
       quantity int check (quantity >= 0),
       primary key(itemid)
);

-- name: drop-item!
-- Drops the item table.
drop table item;

-- name: create-orderitem!
-- Creates the orderitem table for containing the items
-- in an order.
create table orderitem (
       orderitemid int auto_increment,
       orderid int foreign key references ordertable(orderid),
       itemid int foreign key references item(itemid),
       quantity int foreign key check(quantity > 0),
       price int,
       primary key(orderitemid)
);

-- name: create-promotion!
-- Creates the promotion table.
create table promotion (
       promotionid int auto_increment,
       starttime timestamp default current_timestamp,
       endtime timestamp,
       promotionrate int,
       primary key(promotionid),
       check (promotionrate >= 0 and promotionrate <= 100),
       check (endtime > starttime)
);

-- name: drop-promotion!
-- Drops the promotion table.
drop table promotion;

-- name: drop-orderitem!
-- Deletes the orderitem table.
drop table orderitem;

-- name: create-order!
-- Creates the order table for recording the dates orders
-- have been placed and their status.
create table ordertable (
       orderid int auto_increment,
       useraccountid int foreign key references useraccount(useraccountid),
       promotionid int foreign key references promotion(promotionid),
       orderstatus enum ('pending', 'shipped'),
       placementtime timestamp default current_timestamp,
       primary key(orderid)
);

-- name: drop-order!
-- Deletes the order table.
drop table ordertable;

-- name: init-useraccount!
insert into useraccount (username, password, userrole)
values ('manager', 'manager', 'manager'),
       ('staff', 'staff', 'staff'),
       ('user', 'user', 'user');

-- name: init-item!
insert into item (itemname, price, quantity)
values ('super smash bros melee', 5000,10),
       ('captain falcon action figure', 6000,1),
       ('blue falcon model', 10000, 1),
       ('rainbow phoenix action figure', 9000,1);

-- name: insert-orderitem!
-- Adds an item to the given order.
insert into orderitem (orderid, itemid, quantity, price)
values (:orderid, :itemid, :quantity, :price);

-- name: insert-order<!
-- Inserts a new order into the ordertable for the given useraccountid.
insert into ordertable (useraccountid, orderstatus)
values (:useraccountid, 'pending');

-- name: insert-user!
-- Inserts a user with the given username and password.
insert into useraccount (username, password)
values (:username, :password);

-- name: insert-item!
-- Inserts an item with the given name, price and quantity.
insert into item (itemname, price, quantity)
values (:itemname, :price, :quantity);

-- name: find-user+pass
-- Queries for a user with the given password and username.
select * from useraccount
where
(username = :username)
and
(password = :password);

-- name: find-user
-- Selects for a user with the given username.
select *
from useraccount
where username = :username;

-- name: select-user
-- Select all users.
select *
from useraccount;

-- name: select-item
-- Select all items.
select * from item;

-- name: select-order
-- Select all orders.
select * from ordertable;

-- name: select-order-with-status
-- Get orders and their statuses
select * from ordertable
where status = :status

-- name: select-customer-orders
-- Get orders
select ordertable.orderid, item.itemname, orderitem.price, ordertable.orderstatus
from ordertable, orderitem, item
where ordertable.useraccountid = :useraccountid
and orderitem.orderid = ordertable.orderid
and orderitem.itemid = item.itemid
order by ordertable.orderid desc;

-- name: select-orderitem
select * from orderitem;

-- name: approve-order!
-- Approves an order
update item JOIN orderitem ON item.itemid = orderitem.itemid
            JOIN ordertable ON ordertable.orderid = orderitem.orderid
set item.quantity = item.quantity - orderitem.quantity,
    ordertable.orderstatus = 'shipped'
where orderitem.orderid = :orderid
and ordertable.orderstatus = 'pending';

