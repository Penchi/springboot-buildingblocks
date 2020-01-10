insert into user values (101, 'Address-1', 'Apple.A@gmail.com', 'Apple', 'A', 'Manager', 'ssn101', 'Apple A');
insert into user values (102, 'Address-2', 'Ball.B@gmail.com', 'Ball', 'B', 'TechLead', 'ssn102', 'Ball B');
insert into user values (103, 'Address-3', 'Cat.C@gmail.com', 'Cat', 'C', 'Associate', 'ssn103', 'Cat C');

insert into orders (orderid, orderdescription, user_id) values (2001, 'Order-1', 101);
insert into orders (orderid, orderdescription, user_id) values (2002, 'Order-2', 101);
insert into orders (orderid, orderdescription, user_id) values (2003, 'Order-3', 101);
insert into orders (orderid, orderdescription, user_id) values (2004, 'Order-1', 102);
insert into orders (orderid, orderdescription, user_id) values (2005, 'Order-2', 102);