import java.io.*;
import java.sql.*;
import java.util.regex.*;

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
            	System.out.print("Choice (0-9): ");
            	
            	//get selection
            	int selection = 0;
            	try { selection = Integer.parseInt(readInput()); }
            	catch (NumberFormatException e) { System.out.println("Invalid entry.  Quitting App."); System.exit(1);};
            
            	if (selection == 0) { quitApplication(); }
            	else if (selection == 1) //Browse Mutual Funds
            	{
            		
            	}
            	else if (selection == 2) //Search Mutual Funds
            	{
            		
            	}
            	else if (selection == 3) //Invest
            	{
            		
            	}
            	else if (selection == 4) //Sell Shares
            	{
            		
            	}
            	else if (selection == 5) //Buy Shares
            	{
            		
            	}
            	else if (selection == 6) //Change Allocation Preference
            	{
            		
            	}
            	else if (selection == 7) //View Portfolio
            	{
            		
            	}
            	else { System.out.println("Invalid selection. Quitting application."); quitApplication(); }
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
            	catch (NumberFormatException e) { System.out.println("Invalid entry.  Quitting App."); System.exit(1);};
            
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
            	else if (selection == 5)
            	{
            		//View Volume Statistics
            	}
            	else if (selection == 6)
            	{
            		//View Investor Statistics
            	}
            	else {System.out.println("Invalid selection. Quitting application."); quitApplication();  }
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
        System.out.println("\t8: View Volume Statistics");
        System.out.println("\t9: View Investor Statistics");
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
    
    //-----------------------------------------------------------------------------------
    
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
    
    //-----------------------------------------------------------------------------------
    
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
    
    //-----------------------------------------------------------------------------------
    
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
            		status = updateWueryMutualFund(symbol);
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
    
    private int updateWueryMutualFund(String symbol)
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
    
}