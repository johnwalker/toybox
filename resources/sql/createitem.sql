create table item (
       itemid serial,
       itemname varchar(255),
       price int, -- for precision
       quantity int,
       PRIMARY KEY(itemid)
);
