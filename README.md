Michael Kudaty
Stephen Ruzzini

Certain functionality after creating triggers, procedures, and functions do not work
Such as adding new customers or selling share

11/13/2014 - Debugging Notes

Test inserts and their respective error output.

Code:
INSERT INTO customer values('eliz', 'Elizabeth', 'eliz@better.edu', '1st street', 'pwd', 750);

Error:
INSERT INTO customer values('eliz', 'Elizabeth', 'eliz@better.edu', '1st street', 'pwd', 750)
*
ERROR at line 1:
ORA-01403: no data found
ORA-06512: at "MBK24.INVESTFUNDS", line 43
ORA-06512: at "MBK24.INVESTDEPOSIT", line 2
ORA-04088: error during execution of trigger 'MBK24.INVESTDEPOSIT'


Code:
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(4, 'mike', 'MM', TO_DATE('02-APR-14', 'DD-MON-YY'), 'sell', 20, 10, 200);

Error:
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
            *
ERROR at line 1:
ORA-01403: no data found
ORA-06512: at "MBK24.SELL_SHARE", line 13
ORA-06512: at "MBK24.SELLSHARE", line 2
ORA-04088: error during execution of trigger 'MBK24.SELLSHARE'


