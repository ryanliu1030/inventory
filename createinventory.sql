DROP TABLE if exists Login;
DROP TABLE if exists Inventory;

CREATE TABLE Login (
    userid int not null,
    pwd varchar(20) not null,
    usertype varchar(20),

    PRIMARY KEY(userid);
);

CREATE TABLE Inventory (
    item_id int not null,
    item_name varchar(20) not null,
    item_price varchar(20) not null,
    item_stock int not null,
    item_sold int not null,

    PRIMARY KEY(item_id);
);