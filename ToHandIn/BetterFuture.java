//team16

import java.io.*;
import java.sql.*;
import java.util.regex.*;
import java.util.HashMap;
import java.util.ArrayList;

public class BetterFuture {

    private String userLogin;
    private boolean isAdmin;
    private BufferedReader reader;
    
    private static Connection connection;
    private Statement statement;
    private PreparedStatement prepStatement;
    private ResultSet resultSet;
    private String query;
    
    public BetterFuture()
    {
        this.isAdmin = false;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public static void main(String[] args) throws SQLException 
    {
        String user = "str28";
        String pass = "3615466";
        try
        {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
            connection = DriverManager.getConnection(url, user, pass);
            
            BetterFuture app = new BetterFuture();
            app.runApp();
        }
        catch (Exception e) {System.out.println("Error connecting to database."); }
        finally { connection.close(); }
    }
    
    public void runApp() {
        int result = welcome();
        while (result == -1)
        {
            System.out.println("Incorrect login!  Please try again.  Enter 'q' to quit.\n");
            result = welcome();
        }
        if (result == 0)  //User Interface
        {
        	boolean stillGoing = true;
        	System.out.print("Welcome, " + userLogin + "!  ");
        	while (stillGoing)
        	{
            	System.out.println("Please enter the operation you would like to perform:");
            	printUserOptions();
            	System.out.print("Choice (0-7): ");
            	
            	//get selection
            	int selection = 0;
            	try { selection = Integer.parseInt(readInput()); }
            	catch (NumberFormatException e) { System.out.println("Invalid entry."); selection = -1;};
            
            	if (selection == 0) { quitApplication(); }
            	else if (selection == 1) //Browse Mutual Funds
            	{
            		int status = browseFunds();
            		if (status == -1) System.out.println("Invalid Entry.  No search was performed.");
            		else if (status != 0) System.out.println("A database error occurred.  No search was performed");
            	}
            	else if (selection == 2) //Search Mutual Funds
            	{
            		int status = searchFunds();
            		if (status == -1) System.out.println("Invalid Entry.  No search was performed.");
            		else if (status != 0) System.out.println("A database error occurred.  No search was performed");
            	}
            	else if (selection == 3) //Invest
            	{
            		int status = invest();
            		if (status == -1) System.out.println("Invest Not Performed: Invalid amount.");
            		else if (status == 0) System.out.println("Invest Successful!");
            		else System.out.println("Invest Not Performed: A database error occurred.");
            	}
            	else if (selection == 4) //Sell Shares
            	{
            		int status = sellShares();
            		if (status == -2) System.out.println("Sale Not Performed: Invalid amount of shares.");
            		else if (status == -1) System.out.println("Sale Not Performed: You don't own any shares of that fund.");
            		else if (status == 0) System.out.println("Sale Successful!");
            		else System.out.println("Sale Not Performed: A database error occurred.");
            	}
            	else if (selection == 5) //Buy Shares
            	{
            		int status = buyShares();
            		if (status == -2) System.out.println("Buy Not Performed: Not enough money to buy.");
            		else if (status == -1) System.out.println("Buy Not Performed: Fund does not exist.");
            		else if (status == 0) System.out.println("Buy Successful!");
            		else System.out.println("Buy Not Performed: A database error occurred.");
            	}
            	else if (selection == 6) //Change Allocation Preference
            	{
            		int status = changeAllocation();
            		if (status == -2) System.out.println("Update Not Performed: Already changed this month.");
            		else if (status == -1) System.out.println("Update Not Performed: Percentages don't add up to '1'.");
            		else if (status == 0) System.out.println("Update Successful!");
            		else System.out.println("Update Not Performed: Invalid user entry.");
            	}
            	else if (selection == 7) //View Portfolio
            	{
            		int status = viewPortfolio();
            		if (status == -1) System.out.println("Invalid Entry.  Portfolio not displayed.");
            		else if (status != 0) System.out.println("A database error occurred.  Portfolio not displayed.");
            	}
            }
        }
        else if (result == 1)  //Admin Interface
        {
            boolean stillGoing = true;
        	System.out.print("Welcome, " + userLogin + "!  ");
        	while (stillGoing)
        	{
            	System.out.println("Please enter the operation you would like to perform:");
            	printAdminOptions();
            	System.out.print("Choice (0-6): ");
            	
            	//get selection
            	int selection = 0;
            	try { selection = Integer.parseInt(readInput()); }
            	catch (NumberFormatException e) { System.out.println("Invalid entry."); selection = -1;};
            
            	if (selection == 0) { quitApplication(); }
            	else if (selection == 1) //Register New Customer
            	{
            		int status = registerCustomer();
            		if (status == -3) System.out.println("Customer Not Created: Invalid balance amount.");
            		else if (status == -2) System.out.println("Customer Not Created: Invalid email.");
            		else if (status == -1) System.out.println("Customer Not Created: login already exists.");
            		else if (status == 0) System.out.println("Customer Created Successfully!");
            		else System.out.println("Customer Not Created: A database error occurred.");
            	}
            	else if (selection == 2) //Update Share Quotes
            	{
            		int status = updateMutualFund();
            		if (status == -2) System.out.println("Mutual Fund Not Updated: invalid balance amount.");
            		else if (status == -1) System.out.println("Mutual Fund Not Updated: mutual fund does not exist.");
            		else if (status == 0) System.out.println("Mutual Fund Updated Successfully!");
            		else System.out.println("Mutual Fund Not Updated: A database error occurred.");
            	}
            	else if (selection == 3) //Add New Mutual Fund
            	{
            		int status = addMutualFund();
            		if (status == -2) System.out.println("Mutual Fund Not Created: Invalid Category.  Must be one of the following: fixed, stocks, bonds, mixed.");
            		else if (status == -1) System.out.println("Mutual Fund Not Created: symbol already exists.");
            		else if (status == 0) System.out.println("Mutual Fund Created Successfully!");
            		else System.out.println("Mutual Fund Not Created: A database error occurred.");
            	}
            	else if (selection == 4) //Update Date
            	{
            		int status = updateDate();
            		if (status < 0) { System.out.println("Date Not Updated: Invalid Date Format"); }
            		else if (status == 0) System.out.println("Date Updated Successfully!");
            		else System.out.println("Date Not Updated: A database error occurred.");
            	}
            	else if (selection == 5) //View Volume Statistics
            	{
            		int status = volumeStats();
            		if (status < 0) { System.out.println("Result Not Gotten: Invalid entry"); }
            		else if (status != 0) System.out.println("Results Not Gotten: A database error occurred.");
            	}
            	else if (selection == 6) //View Investor Statistics
            	{
            		int status = investorStats();
            		if (status < 0) { System.out.println("Result Not Gotten: Invalid entry"); }
            		else if (status != 0) System.out.println("Results Not Gotten: A database error occurred.");
            	}
            }
        }
        else { System.out.println("Unexpected error occurred.  Quitting application."); quitApplication(); }
    }
    
    
    //returns:
    //  -1 = invalid login
    //  0 = user login
    //  1 = admin login
    //  2 = quit application
    private int welcome()
    {
        int result = -1;
        
        System.out.println("Better Future Investments\n");
        System.out.println("Please enter your user login to begin.  Enter 'q' to quit.");
        System.out.print("user login:     ");
        
        String login;
        String password = null;
        
            login = readInput();
            if (!login.equals("q"))
            {
                System.out.print("password:     ");
                password = readInput();
                
                try {
                	connection.setAutoCommit(false);
                	connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                	statement = connection.createStatement();
                
                	query = "SELECT count(login) FROM customer WHERE login = '"+login+"' AND password = '"+password+"'";
                	resultSet = statement.executeQuery(query);
                	int matches = 0;
                	if (resultSet.next()) {
                		matches = resultSet.getInt(1); resultSet.close(); }
                	if (matches == 0)
                	{
                		//check for Admin
                		statement = connection.createStatement();
                		query = "SELECT count(login) FROM administrator WHERE login = '"+login+"' AND password = '"+password+"'";
                		resultSet = statement.executeQuery(query);
                		if (resultSet.next()) {
                			matches = resultSet.getInt(1); resultSet.close(); }
                		if (matches == 1)
                		{
                			userLogin = login;
                			isAdmin = true;
                			result = 1;
                		}
                		else //login not found
                		{
                			result = -1;
                		}
                	}
                	else
                	{
                		userLogin = login;
                		isAdmin = false;
                		result = 0;
                	}
                	connection.commit();
                }
                catch (Exception e){
                	System.out.println("Machine Error: "+e);
                }
                finally {
                	try { if (statement != null) statement.close(); }
                	catch (SQLException e) { System.out.println("Can't close Statement"); }
                }
            }
            else { result = 2; }
        return result;
    }
    
    
    
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\ User Functions \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
    //-----------------------------------------------------------------------------------
    //Browse Funds
    //returns:
    //	-1: invalid entry
    //	0: success
    private int browseFunds()
    {
    	int status = 0;
    	
    	System.out.println("\nBrowse Mutual Funds.\nPlease select browsing option.");
    	System.out.println("\t1: Browse All Funds");
    	System.out.println("\t2: Browse Funds By Category");
    	System.out.print("Choice:  ");
    	String choice = readInput();
    	String category = "*";
    	
    	if (choice.equals("1")) { status = browseCategory(category); }
        else if (choice.equals("2"))
        {
          	System.out.println("\nPlease select a category.");
    		System.out.println("\t1: Fixed");
    		System.out.println("\t2: Stocks");
    		System.out.println("\t3: Bonds");
    		System.out.println("\t4: Mixed");
    		System.out.print("Choice:  ");
    		category = readInput();
    		if (category.equals("1")) { status = browseCategory("fixed"); }
    		else if (category.equals("2")) { status = browseCategory("stocks"); }
    		else if (category.equals("3")) { status = browseCategory("bonds"); }
    		else if (category.equals("4")) { status = browseCategory("mixed"); }
    		else { status = -1; }
        }
        else { status = -1; }
    	
    	return status;
    }
    
    private int browseCategory(String category)
    {
    	int status = 0;
    	try {
            System.out.println("\nPlease select a sorting method.");
    		System.out.println("\t1: Descending Price");
    		System.out.println("\t2: Alphabetically");
    		System.out.print("Choice:  ");
    		String sorting = readInput();
            
            if (category.equals("*")) //all categories
            {
            	query = "SELECT f.symbol, f.name, f.description, f.category, c.price, c.p_date " +
            			"FROM mutualfund f join closingprice c on f.symbol = c.symbol " +
            			"WHERE c.p_date = get_last_trade_date ";
            	if (sorting.equals("1")) { query = query + "ORDER BY c.price DESC"; }
            	else if (sorting.equals("2")) { query = query + "ORDER BY f.name ASC"; }
            	else { return -1; }
            }
            else //specific category
            {
            	query = "SELECT f.symbol, f.name, f.description, f.category, c.price, c.p_date " +
            			"FROM mutualfund f join closingprice c on f.symbol = c.symbol " +
            			"WHERE f.category = '"+category+"' AND c.p_date = get_last_trade_date ";
            	if (sorting.equals("1")) { query = query + "ORDER BY c.price DESC"; }
            	else if (sorting.equals("2")) { query = query + "ORDER BY f.name ASC"; }
            	else { return -1; }
            }
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            
            //print results
            int rownum = 1;
            System.out.println("Results:\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(1);
            	String nam = resultSet.getString(2);
            	String des = resultSet.getString(3);
            	String cat = resultSet.getString(4);
            	float pri = resultSet.getFloat(5);
            	Date dat = resultSet.getDate(6);
            	String x = ""+rownum+") "+sym+
            							":"+nam+
            							"\n\t"+des+
            							"\n\t"+cat+
            							"\n\t$"+pri+" : "+dat.toString();
            	System.out.println(x);
            	rownum = rownum + 1;
            }
            System.out.print("\n");
            resultSet.close();
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
        return status;
    }
    
    
    //Search Funds
    //returns:
    //	-1: invalid entry
    //	0: success
    private int searchFunds()
    {
    	int status = 0;
    	
    	System.out.println("\tSearch Mutual Funds.\nPlease select browsing option.");
    	System.out.println("\t1: Search with 1 keyword");
    	System.out.println("\t2: Search with 2 keywords");
    	System.out.print("Choice:  ");
    	String choice = readInput();
    	
    	if (choice.equals("1")) 
    	{ 
    		System.out.print("keyword:  ");
    		String keyword = readInput();
    		
    		query = "SELECT f.symbol, f.name, f.description, f.category, c.price, c.p_date " +
          			"FROM mutualfund f join closingprice c on f.symbol = c.symbol " +
          			"WHERE c.p_date = get_last_trade_date AND REGEXP_LIKE(f.description, '^.*"+keyword+".*$')";
    	}
        else if (choice.equals("2"))
        {
        	System.out.print("first keyword:  ");
    		String keyword1 = readInput();
    		System.out.print("second keyword:  ");
    		String keyword2 = readInput();
          	
          	query = "SELECT f.symbol, f.name, f.description, f.category, c.price, c.p_date " +
          			"FROM mutualfund f join closingprice c on f.symbol = c.symbol " +
          			"WHERE (c.p_date = get_last_trade_date AND (REGEXP_LIKE(f.description, '^.*"+keyword1+".*"+keyword2+".*$') OR REGEXP_LIKE(f.description, '^.*"+keyword2+".*"+keyword1+".*$')))";
        }
        else { return -1; }
        
        //perform the search
    	try {
    		System.out.println(query);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            
            //print results
            int rownum = 1;
            System.out.println("Results:\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(1);
            	String nam = resultSet.getString(2);
            	String des = resultSet.getString(3);
            	String cat = resultSet.getString(4);
            	float pri = resultSet.getFloat(5);
            	Date dat = resultSet.getDate(6);
            	String x = ""+rownum+") "+sym+
            							":"+nam+
            							"\n\t"+des+
            							"\n\t"+cat+
            							"\n\t$"+pri+" : "+dat.toString();
            	System.out.println(x);
            	rownum = rownum + 1;
            }
            System.out.print("\n");
            resultSet.close();
            connection.commit();
    	}
    	catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Invest
    //returns:
    //	-1: invalid amount
    //	0: success
    private int invest()
    {
    	int status = 0;
    	
    	System.out.println("Invest.\nPlease enter amount to invest.");
    	System.out.print("Amount:  $");
    	String investString = readInput();
    	float investAmount;
    	try { investAmount = Float.parseFloat(investString); }
        catch (NumberFormatException e) { return -1; }
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "SELECT balance FROM customer WHERE login = '"+userLogin+"'";
            resultSet = statement.executeQuery(query);
            float initial = 0;
            if (resultSet.next()) {
            	initial = resultSet.getFloat(1); resultSet.close(); }
            
            float totalInvestment = initial + investAmount;
            
            query = "UPDATE customer set balance = "+totalInvestment+" where login = '"+userLogin+"'";
            statement.executeQuery(query);
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Sell Shares
    //	-2: invalid amount of shares
    //	-1: don't own that fund
    //	0: success
    private int sellShares()
    {
    	int status = 0;
    	
    	System.out.println("Sell Shares.\nPlease enter share symbol to sell.");
    	System.out.print("Symbol:   ");
    	String symbol = readInput();
    	System.out.print("Number of shares to sell:   ");
    	String sharesString = readInput();
    	int numShares;
    	try { numShares = Integer.parseInt(sharesString); }
        catch (NumberFormatException e) { return -2; }
        if (numShares < 1) { return -2; }
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            //check that user owns shares
            query = "SELECT shares FROM owns WHERE login = '"+userLogin+"' AND symbol = '"+symbol+"'";
            resultSet = statement.executeQuery(query);
            int initial = 0;
            if (resultSet.next()) {
            	initial = resultSet.getInt(1); resultSet.close(); }
            else { return -1; }
            if (initial < 1) { return -1; }
            
            //get transaction number
            query = "SELECT count(trans_id) FROM trxlog";
            resultSet = statement.executeQuery(query);
            int id = 0;
            if (resultSet.next()) {
            	id = resultSet.getInt(1); resultSet.close(); }
            else { return 1; }
            
            //get today's date
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date d = null;
            if (resultSet.next()) {
            	d = resultSet.getDate(1); resultSet.close(); }
            else { return 1; }
            
            //perform sale
            query = "ALTER TRIGGER InvestDeposit DISABLE";
            statement.executeQuery(query);
            query = "INSERT INTO trxlog " +
            		"values("+id+", '"+userLogin+"', '"+symbol+"', TO_DATE('"+d.toString()+"', 'yyyy-mm-dd'), 'sell', "+numShares+", 0, 0)";
            statement.executeQuery(query);
            query = "ALTER TRIGGER InvestDeposit ENABLE";
            statement.executeQuery(query);
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Buy Shares
    //	-3: invalid entry
    //	-2: not enough money
    //	-1: fund doesn't exist
    //	0: success
    private int buyShares()
    {
    	int status = 0;
    	
    	System.out.println("Buy Shares.\nPlease enter your choice.");
    	System.out.println("\t1: Buy number of shares");
    	System.out.println("\t2: Buy dollar amount of shares");
    	System.out.print("Choice:   ");
    	String choice = readInput();
    	
    	if (choice.equals("1")) 
    	{ 
    		System.out.println("Please enter the share symbol you would like to buy.");
    		System.out.print("Symbol:   ");
    		String symbol = readInput();
    		System.out.print("Number of Shares:   ");
    		String num = readInput();
    		int number = 0;
            try { number = Integer.parseInt(num); }
            catch (NumberFormatException e) { return -3;};
            if (number < 0) { return -3; }
    		status = buyNumberOfShares(symbol, number); 
    	}
    	else if (choice.equals("2"))
    	{
    		System.out.println("Please enter the share symbol you would like to buy.");
    		System.out.print("Symbol:   ");
    		String symbol = readInput();
    		System.out.print("Amount:   $");
    		String am = readInput();
    		float amount = 0;
            try { amount = Float.parseFloat(am); }
            catch (NumberFormatException e) { return -3;};
            if (amount < 0) { return -3; }
    		status = buyAmountOfShares(symbol, amount); 
    	}
    	else { return -3; }
    	
    	return status;
    }
    
    private int buyNumberOfShares(String symbol, int number)
    {
    	int status = 0;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            //get fund price
            query = "SELECT price FROM closingprice WHERE price = get_fund_price('"+symbol+"') AND symbol = '"+symbol+"'";
            resultSet = statement.executeQuery(query);
            float price = 0;
            if (resultSet.next()) {
            	price = resultSet.getFloat(1); resultSet.close(); }
            else { return -1; }
            if (price <= 0) { return -1; }
            
            float totalCost = price * number;
            
            //get transaction number
            query = "SELECT count(trans_id) FROM trxlog";
            resultSet = statement.executeQuery(query);
            int id = 0;
            if (resultSet.next()) {
            	id = resultSet.getInt(1); resultSet.close(); }
            else { return 1; }
            
            //get today's date
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date d = null;
            if (resultSet.next()) {
            	d = resultSet.getDate(1); resultSet.close(); }
            else { return 1; }
            
            //perform buy
            query = "SET CONSTRAINT valid_balance DEFERRED";
            statement.executeQuery(query);
            query = "INSERT INTO trxlog " +
            		"values("+id+", '"+userLogin+"', '"+symbol+"', TO_DATE('"+d.toString()+"', 'yyyy-mm-dd'), 'buy', -1, 0, "+totalCost+")";
            statement.executeQuery(query);
            connection.commit();
            
            //check that it happened
            query = "SELECT trans_id from trxlog WHERE trans_id = "+id;
            resultSet = statement.executeQuery(query);
            int thing = -1;
            if (resultSet.next()) {
            	thing = resultSet.getInt(1); resultSet.close(); }
            else { status =  -2; }
            if (thing < 0) { status = -2; }
            connection.commit();
            
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
        
        return status;
    }
    
    private int buyAmountOfShares(String symbol, float amount)
    {
    	int status = 0;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            //get transaction number
            query = "SELECT count(trans_id) FROM trxlog";
            resultSet = statement.executeQuery(query);
            int id = 0;
            if (resultSet.next()) {
            	id = resultSet.getInt(1); resultSet.close(); }
            else { return 1; }
            
            //get today's date
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date d = null;
            if (resultSet.next()) {
            	d = resultSet.getDate(1); resultSet.close(); }
            else { return 1; }
            
            //perform buy
            query = "SET CONSTRAINT valid_balance DEFERRED";
            statement.executeQuery(query);
            query = "INSERT INTO trxlog " +
            		"values("+id+", '"+userLogin+"', '"+symbol+"', TO_DATE('"+d.toString()+"', 'yyyy-mm-dd'), 'buy', -1, 0, "+amount+")";
         	statement.executeQuery(query);
            connection.commit();
            
            //check that it happened
            query = "SELECT trans_id from trxlog WHERE trans_id = "+id;
            resultSet = statement.executeQuery(query);
            int thing = -1;
            if (resultSet.next()) {
            	thing = resultSet.getInt(1); resultSet.close(); }
            else { status =  -2; }
            if (thing < 0) { status = -2; }
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
        
        return status;
    }
    
    
    //View Portfolio
    //	-1: invalid entry
    //	0: success
    private int viewPortfolio()
    {
    	int status = 0;
    	
    	//perform the search
    	try {
    		//gets current price for each stock and  current value
            System.out.println("Stocks owned:  Num Shares, Current Price  ->   Value\n");
    		query = "SELECT owns.login, owns.symbol, owns.shares, closingprice.price, (owns.shares * closingprice.price) as current_values " +
    				"FROM owns, mutualdate, closingprice " +
    				"WHERE closingprice.p_date = get_last_trade_date and closingprice.symbol = owns.symbol and owns.login = '"+userLogin+"'";
    		connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            ArrayList<String> uniqueSymbols = new ArrayList<String>();
            HashMap<String, Float> current_values = new HashMap<String, Float>();
            HashMap<String, Float> cost_values = new HashMap<String, Float>();
            HashMap<String, Float> sum_sales = new HashMap<String, Float>();
            
            //print results
            int rownum = 1;
            System.out.println("Stocks owned:  Num Shares, Current Price  ->   Value\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(2);
                if(uniqueSymbols.indexOf(sym) == -1)
                {
                    uniqueSymbols.add(sym);
                }                  
            	int shares = resultSet.getInt(3);
            	float price = resultSet.getFloat(4);
            	float value = resultSet.getFloat(5);
            	String x = ""+rownum+") "+sym+
            							":  "+shares+
            							", $"+price+
            							"  ->  "+value;
            	System.out.println(x);
            	rownum = rownum + 1;
                current_values.put(sym, value);
            }
            System.out.print("\n");
            resultSet.close();
            
            //gets Cost Value
            query = "SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount) as cost_values " +
    				"FROM trxlog " +
    				"WHERE trxlog.action = 'buy' and trxlog.login = '"+userLogin+"' " +
    				"GROUP BY  trxlog.login, trxlog.symbol";
    		connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            
            //print results
            rownum = 1;
            System.out.println("Cost Value for each Symbol\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(2);
                if(uniqueSymbols.indexOf(sym) == -1)
                {
                    uniqueSymbols.add(sym);
                }                  
            	float value = resultSet.getFloat(3);
            	String x = ""+rownum+") "+sym+
            							":  $"+value;
            	System.out.println(x);
            	rownum = rownum + 1;
                cost_values.put(sym, value); 
            }
            System.out.print("\n");
            resultSet.close();
            
            //gets Sum Sales
            query = "SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount)  as sum_sales " +
    				"FROM trxlog " +
    				"WHERE trxlog.action = 'sell' and trxlog.login = '"+userLogin+"' " +
    				"GROUP BY  trxlog.login, trxlog.symbol";
    		connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            
            //print results
            rownum = 1;
            System.out.println("Sum of Sales for each Symbol\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(2);
            	float value = resultSet.getFloat(3);
            	String x = ""+rownum+") "+sym+
            							":  $"+value;
            	System.out.println(x);
            	rownum = rownum + 1;
                sum_sales.put(sym, value); 
            }
            System.out.print("\n");
            resultSet.close();
            
            
            connection.commit();

            //Calculating the yield and displaying the results
            rownum = 1;
            System.out.println("" + "Symbol Yield\n");
            for(String s: uniqueSymbols)
            {
                float c = 0;        //cost_value
                float sa = 0;       //sum_sales 
                float cu = 0;       //current_value

                if(current_values.containsKey(s))
                {
                    cu = current_values.get(s);
                }

                if(cost_values.containsKey(s))
                {
                    c = cost_values.get(s);
                }

                if(sum_sales.containsKey(s))
                {
                    sa = sum_sales.get(s);
                }                

                System.out.println(""+rownum+") " + s + ":\t$" + (cu - (c - sa)));
                rownum++;
            }
            System.out.print("\n");            
    	}
    	catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Change Allocation Preferences
    //	-2: already changed
    //	-1: sum incorrect
    //	0: success
    private int changeAllocation()
    {
    	int status = 0;
    	String more = "y";
    	
    	System.out.println("Change Allocation Preferencess.\nPlease enter your fund preferences.");
    	
    	ArrayList<String> symbols = new ArrayList<String>();
    	ArrayList<Float> percentages = new ArrayList<Float>();
    	
    	int total = 0;
    	while (more.equals("y")) 
    	{ 
    		System.out.print("Symbol:   ");
    		String symbol = readInput();
    		System.out.print("Percentage (between 0 and 100):   ");
    		String pString = readInput();
    		float percentage = 0;
            try { 
            	percentage = Float.parseFloat(pString) / 100;
            	total = total + Integer.parseInt(pString);
            }
            catch (NumberFormatException e) { return -1; }
            if (percentage <= 0.0 || percentage > 100.0) { return -1; }
            
            symbols.add(symbol);
            percentages.add(new Float(percentage));
    		
    		System.out.println("Would you like to add another fund.");
    		System.out.print("(y/n):   ");
    		more = readInput();
    	}
    	if (total != 100) { return -1; }
    	status = formAllocation(symbols, percentages);
    	
    	return status;
    }
    
    private int formAllocation(ArrayList<String> symbols, ArrayList<Float> percentages)
    {
    	int status = 0;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            //get allocation number
            query = "SELECT count(allocation_no) FROM allocation";
            resultSet = statement.executeQuery(query);
            int id = 0;
            if (resultSet.next()) {
            	id = resultSet.getInt(1); resultSet.close(); }
            else { return 1; }
            
            //get today's date
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date d = null;
            if (resultSet.next()) {
            	d = resultSet.getDate(1); resultSet.close(); }
            else { return 1; }
            
            //disable trigger
            query = "ALTER TRIGGER EnsurePreferSum DISABLE";
            statement.executeQuery(query);
            
            try {Thread.sleep(3000);}
            catch (InterruptedException e) {}
            
            //insert allocation
            query = "INSERT INTO allocation " +
            		"values("+id+", '"+userLogin+"', TO_DATE('"+d.toString()+"', 'yyyy-mm-dd'))";
         	statement.executeQuery(query); 
            
            //insert prefers
            int indexInArray = 0;
            for (String sym : symbols)
            {
            	Float p = percentages.get(indexInArray);
            	query = "INSERT INTO prefers values("+id+", '"+sym+"', "+p.floatValue()+")";
         		statement.executeQuery(query); 
            	indexInArray = indexInArray + 1;
            }
            
            //enable trigger
            query = "ALTER TRIGGER EnsurePreferSum ENABLE";
            statement.executeQuery(query);
            connection.commit();
            
            //check that it happened
            query = "SELECT count(allocation_no) from prefers WHERE allocation_no = "+id;
            resultSet = statement.executeQuery(query);
            int thing = -1;
            if (resultSet.next()) {
            	thing = resultSet.getInt(1); System.out.println("num prefers: "+thing); resultSet.close(); }
            else { status =  -2; }
            if (thing < 1) { 
            	query = "DELETE from allocation WHERE allocation_no = "+id;
            	statement.executeQuery(query);
            	status = -2; 
            }
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
        
        return status;
    }
    
    
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\ Admin Functions \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\
    //-----------------------------------------------------------------------------------
    //Update Date
    //returns:
    //	-1: error
    //	0: success
    private int updateDate()
    {
    	int status = 0;
    	
    	System.out.println("\nUpdate Date.\nPlease enter new date in the following form \"DD-MON-YY\".  For example \"April 7, 2015\" would be \"07-APR-15\".");
    	System.out.print("New Date (\"DD-MON-YY\"):  ");
    	String dateString = readInput();
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "UPDATE mutualdate set c_date = TO_DATE('"+dateString+"', 'DD-MON-YY')";
            resultSet = statement.executeQuery(query);
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Register Customer
    //returns:
    //	-3: invalid balance
    //	-2: invalid email address
    //	-1: login already exists
    //	0: success
    private int registerCustomer()
    {
    	int status = 0;
    	
    	System.out.println("\nRegister New Customer.\nPlease enter customer information.");
    	System.out.print("login:  ");
    	String newLogin = readInput();
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "SELECT count(login) FROM customer WHERE login = '"+newLogin+"'";
            resultSet = statement.executeQuery(query);
            int matches = 0;
            if (resultSet.next()) {
            	matches = resultSet.getInt(1); resultSet.close(); }
            if (matches == 0)
            	status = getCustomerInfo(newLogin);
            else
            	status = -1;
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    private int getCustomerInfo(String newLogin)
    {
    	int status = 0;
    	
    	System.out.print("name:  ");
    	String name = readInput();
    	
    	System.out.print("email:  ");
    	String email = readInput();
    	Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    	Matcher m = p.matcher(email);
    	if (!m.matches())
    		return -2; //invalid email address
    	
    	System.out.print("address:  ");
    	String address = readInput();
    	
    	System.out.print("password:  ");
    	String password = readInput();
    	
    	System.out.print("balance:  ");
    	String balance = readInput();
    	double numBalance = 0;
        try { numBalance = Double.parseDouble(balance); }
        catch (NumberFormatException e) { return -3; } //invalid balance
        if (numBalance < 0)
        	return -3;
            
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "INSERT INTO customer values('"+newLogin+"', '"+name+"', '"+email+"', '"+address+"', '"+password+"', "+numBalance+")";
            statement.executeQuery(query);
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Add Mutual Fund
    //returns:
    //	-2: Invalid category
    //	-1: symbol already exists
    //	0: success
    private int addMutualFund()
    {
    	int status = 0;
    	
    	System.out.println("\nAdd Mutual Fund.\nPlease enter fund information.");
    	System.out.print("Symbol:  ");
    	String symbol = readInput();
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "SELECT count(symbol) FROM mutualfund WHERE symbol = '"+symbol+"'";
            resultSet = statement.executeQuery(query);
            int matches = 0;
            if (resultSet.next()) {
            	matches = resultSet.getInt(1); resultSet.close(); }
            if (matches == 0)
            	status = getFundInfo(symbol);
            else
            	status = -1;
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    private int getFundInfo(String symbol)
    {
    	int status = 0;
    	
    	System.out.print("name:  ");
    	String name = readInput();
    	name = name.replace(" ", "-");
    	
    	System.out.print("description:  ");
    	String description = readInput();
    	
    	System.out.print("category (fixed/bonds/stocks/mixed):  ");
    	String category = readInput();
    	if (!category.equals("fixed") && !category.equals("bonds") && !category.equals("stocks") && !category.equals("mixed"))
    		return -2;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date date = null;
            if (resultSet.next()) {
            	date = resultSet.getDate(1); resultSet.close(); }
            
            query = "INSERT INTO mutualfund values('"+symbol+"', '"+name+"', '"+description+"', '"+category+"', TO_DATE('"+date.toString()+"', 'yyyy-mm-dd'))";
            System.out.println(query);
            statement.executeQuery(query);
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Update Mutual Fund
    //	-2: Invalid balance amound
    //	-1: Mutual Fund doesn't exist
    //	0: success
    private int updateMutualFund()
    {
    	int status = 0;
    	
    	System.out.println("\nUpdate Mutual Fund.\nPlease enter fund updates.");
    	System.out.print("Symbol:  ");
    	String symbol = readInput();
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "SELECT count(symbol) FROM mutualfund WHERE symbol = '"+symbol+"'";
            resultSet = statement.executeQuery(query);
            int matches = 0;
            if (resultSet.next()) {
            	matches = resultSet.getInt(1); resultSet.close(); }
            if (matches < 1)
            	status = -1;
            else //fund exists
            {
            	query = "SELECT DISTINCT p_date FROM closingprice WHERE symbol = '"+symbol+"' AND p_date = (SELECT MAX(c_date) FROM mutualdate)";
            	resultSet = statement.executeQuery(query);
            	matches = 0;
            	if (resultSet.next()) { System.out.println("has next");
            		matches = resultSet.getInt(1); resultSet.close(); }
            	if (matches > 0)
            		status = updateQueryMutualFund(symbol);
            	else 
            		status = insertQueryMutualFund(symbol); 
            }
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    private int updateQueryMutualFund(String symbol)
    {
    	System.out.println("UPDATE");
    	int status = 0;
    	
    	System.out.print("price:  ");
    	String p = readInput();
    	double price = 0;
    	try { price = Double.parseDouble(p); }
        catch (NumberFormatException e) { return -2;};
        if (price < 0) return -2;
            
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
                
            query = "UPDATE closingprice set price = "+price+" WHERE symbol = '"+symbol+"' AND p_date = (SELECT MAX(c_date) FROM mutualdate)";
            resultSet = statement.executeQuery(query);
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    private int insertQueryMutualFund(String symbol)
    {
    	System.out.println("INSERT");
    	int status = 0;
    	
    	System.out.print("price:  ");
    	String p = readInput();
    	double price = 0;
    	try { price = Double.parseDouble(p); }
        catch (NumberFormatException e) { return -2;};
        if (price < 0) return -2;
            
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            query = "SELECT c_date FROM mutualdate";
            resultSet = statement.executeQuery(query);
            Date date = null;
            if (resultSet.next()) {
            	date = resultSet.getDate(1); resultSet.close(); }
                
            query = "INSERT INTO closingprice valuse('"+symbol+"', "+price+", TO_DATE('"+date.toString()+"', 'yyyy-mm-dd'))";
            resultSet = statement.executeQuery(query);
            
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Volume Stats
    //	-1: invalid entry
    //	0: success
    private int volumeStats()
    {
    	int status = 0;
    	System.out.println("Volume Statistics.  Top 'k' highest volume categories (highest count of shares sold), for the past 'x' months.");
    	System.out.print("'x':  ");
    	String monthString = readInput();
    	int x = 0;
        try { x = Integer.parseInt(monthString); }
        catch (NumberFormatException e) { System.out.println("Invalid entry."); return -1;}
        if (x < 1) return -1;
        
        System.out.print("'k':  ");
    	String kString = readInput();
    	int k = 0;
        try { k = Integer.parseInt(kString); }
        catch (NumberFormatException e) { System.out.println("Invalid entry."); return -1;}
        if (k < 1) return -1;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            query = "SELECT * FROM (" +
            			"SELECT mutualfund.category, sum(trxlog.num_shares) as highestVolume " +
            			"FROM trxlog, mutualdate, mutualfund " +
            			"WHERE trxlog.action = 'sell' and ADD_MONTHS(mutualdate.c_date, -"+x+") <= trxlog.t_date AND trxlog.symbol = mutualfund.symbol " +
            			"GROUP BY  mutualfund.category " +
            			"ORDER BY sum(trxlog.num_shares) DESC " +
            		") WHERE ROWNUM <= "+k;
            resultSet = statement.executeQuery(query);
            
            //print results
            int rownum = 1;
            System.out.println("Top "+k+" highest volume categories for the past "+x+" months:\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(1);
            	int numShares = resultSet.getInt(2);
            	System.out.println("\t"+rownum+")  "+sym+":  "+numShares+" shares");
            	rownum = rownum + 1;
            }
            System.out.print("\n");
            resultSet.close();
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    //Investor Stats
    //	-1: invalid entry
    //	0: success
    private int investorStats()
    {
    	int status = 0;
    	System.out.println("Investor Statistics.  Top 'k' investors (highest invested amount), for the past 'x' months.");
    	System.out.print("'x':  ");
    	String monthString = readInput();
    	int x = 0;
        try { x = Integer.parseInt(monthString); }
        catch (NumberFormatException e) { System.out.println("Invalid entry."); return -1;}
        if (x < 1) return -1;
        
        System.out.print("'k':  ");
    	String kString = readInput();
    	int k = 0;
        try { k = Integer.parseInt(kString); }
        catch (NumberFormatException e) { System.out.println("Invalid entry."); return -1;}
        if (k < 1) return -1;
    	
    	try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();
            
            query = "SELECT * FROM (" +
            			"SELECT trxlog.login, sum(trxlog.amount) as highestInvest " +
            			"FROM trxlog, mutualdate " +
            			"WHERE trxlog.action = 'buy' and ADD_MONTHS(mutualdate.c_date, -"+x+") <= trxlog.t_date " +
            			"GROUP BY  trxlog.login " +
            			"ORDER BY sum(trxlog.amount) DESC " +
            		") WHERE ROWNUM <= "+k;
            resultSet = statement.executeQuery(query);
            
            //print results
            int rownum = 1;
            System.out.println("Top "+k+" most investors (highest invested amount) for the past "+x+" months:\n");
            while (resultSet.next())
            {
            	String sym = resultSet.getString(1);
            	float numShares = resultSet.getFloat(2);
            	System.out.println("\t"+rownum+")  "+sym+":  $"+numShares+" invested");
            	rownum = rownum + 1;
            }
            System.out.print("\n");
            resultSet.close();
            connection.commit();
        }
        catch (Exception e){
            System.out.println("Machine Error: "+e);
            status = 1;
        }
        finally {
            try { if (statement != null) statement.close(); }
            catch (SQLException e) { System.out.println("Can't close Statement"); }
        }
    	
    	return status;
    }
    
    
    
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\ Other Functions \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\
    //-----------------------------------------------------------------------------------
    
    //Quits out of the application safely
    private void quitApplication()
    {
        System.out.println("Thank you for using the Better Future Investment application.  Goodbye!");
        try { connection.close(); }
        catch (Exception e) { System.out.println("Error closing connection"); }
        System.exit(1);
    }
    
    //Prints the options that a user can perform
    private void printUserOptions()
    {
        System.out.println("\t1: Browse Mutual Funds");
        System.out.println("\t2: Search Mutual Funds");
        System.out.println("\t3: Invest");
        System.out.println("\t4: Sell Shares");
        System.out.println("\t5: Buy Shares");
        System.out.println("\t6: Change Allocation Preference");
        System.out.println("\t7: View Portfolio");
        System.out.println("\t0: Exit");
    }
    
    //Prints the options that an admin can perform
    private void printAdminOptions()
    {
        System.out.println("\t1: Register New Customer");
        System.out.println("\t2: Update Share Quotes");
        System.out.println("\t3: Add New Mutual Fund");
        System.out.println("\t4: Update Date");
        System.out.println("\t5: View Volume Statistics");
        System.out.println("\t6: View Investor Statistics");
        System.out.println("\t0: Exit");
    }
    
    //Gets the user's input from the command line
    private String readInput()
    {
        String result = "";
        try
        {
            result = reader.readLine();
        }
        catch (IOException e)
        {
            System.out.println("IOException occurred.  Quiting Application.");
            quitApplication();
        }
        return result;
    }
    
}