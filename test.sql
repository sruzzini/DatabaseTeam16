CREATE OR REPLACE TRIGGER SellShare
AFTER INSERT ON trxlog
FOR EACH ROW
WHEN (new.action = 'sell')
BEGIN
	sell_share(:new.trans_id, :new.login, :new.symbol, :new.num_shares);
END;
/
commit;