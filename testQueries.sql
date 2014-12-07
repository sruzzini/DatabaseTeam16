--set transaction read write name "deposit";
--UPDATE customer set balance = 1000.00 where login = 'mike';
--commit;

--set transaction read write name "sell valid";
--ALTER TRIGGER InvestDeposit DISABLE;
--INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(8, 'mike', 'MM', TO_DATE('01-APR-14', 'DD-MON-YY'), 'sell', 20, 0, 0);
--ALTER TRGGER InvestDeposit ENABLE;
--commit;

--set transaction read write name "buy valid";
--SET CONSTRAINT valid_balance DEFERRED;
--INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(9, 'mike', 'RE', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'buy', -1, 0, 100);
--commit;

set transaction read write name "changeAlloc";
UPDATE mutualdate set c_date = TO_DATE('01-DEC-14', 'DD-MON-YY');
INSERT INTO allocation values(3, 'mike', TO_DATE('01-DEC-14', 'DD-MON-YY'));
INSERT INTO prefers values(3, 'RE', .6);
INSERT INTO prefers values(3, 'MM', .4);

commit;

