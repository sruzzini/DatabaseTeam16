--INSERT INTO allocation values(2, 'eliz', TO_DATE('30-MAR-14', 'DD-MON-YY'));
--INSERT INTO customer values('eliz', 'Elizabeth', 'eliz@better.edu', '1st street', 'pwd', 750);

INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(3, 'mike', 'MM', TO_DATE('01-APR-14', 'DD-MON-YY'), 'sell', 50, 15, 750);

commit;
