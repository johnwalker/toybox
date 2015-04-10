create table userrole (
       userroleid serial,
       userid int references user(userid),
       urdid int references urd(urdid),
       primary key (serial)
);
