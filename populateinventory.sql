insert into login (userid, pwd, usertype)
values
(1, '1234', 'CLERK'),
(2, 'pass', 'CLERK'),
(3, 'word', 'MANAGER'),
(4, '4321', 'MANAGER');
--done

insert into inventory (item_id, item_name, item_price, item_stock, item_sold)
values 
(1, 'APPLE', '$0.99', 20, 0),
(2, 'ORANGE', '$1.19', 25, 0),
(3, 'PAPER', '$0.19', 120, 0),
(4, 'PENCIL', '$1.29', 60, 0),
(5, 'BATTERY', '$2.99', 40, 5);
--done