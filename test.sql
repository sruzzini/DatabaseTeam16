CREATE OR REPLACE TRIGGER EnsureMutualDate
AFTER INSERT ON mutualdate
FOR EACH ROW
BEGIN
	SELECT COUNT(*) INTO @cnt FROM mutualdate;
	if (@cnt > 1) then
		DELETE FROM mutualdate WHERE c_date = :new.c_date;
	end if;
END;
/
commit;