package smb_service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JTree;
import com.jgraph.JGraph;
/**
 * <p>Title: Database interface class for Schema Matching Experiments</p>
 *
 * <p>Description: SQL Server Documenting class for Schema Matching Experiments</p>
 *
 * <p>Copyright: None</p>
 *
 * <p>Company: Technion IE&M </p>
 *
 * @author Tomer Sagi
 * @version 1.0
 */

public class DBInterface
{
	private DB myDB;
    private Connection myConn;
    private int exp_no;
    public int getExp_no() {
		return exp_no;
	}
    
    /**
     * Constructor of Documenter Class. Opens DB connection on local computer
     */
    public DBInterface()
    {
    	myDB = new DB();
        myConn = myDB.dbConnect(1,"localhost:3306/","schemamatching","root","burlap13");       
    }
    
    /**
     * Parameterized constructor of Documenter Class. Opens DB connection.
     * @param dbmstype : 1=mysql, 2=mssql
     * @param host : Connection string to db in format [ip]:[port]/[db_name] for example: localhost:1433/SchemaMatching
     * @param username : DB user name, for example: sa
     * @param pwd : password of user name, for example: admin123456
     */
    public DBInterface(int dbmstype, String host,String dbName, String username, String pwd)
    {
    	myDB = new DB();
        myConn = myDB.dbConnect(dbmstype,host,dbName ,username ,pwd);
    }
    
    /**
     * Release documenter database
     */
    public void disconnect()
    {
    	try {
			myConn.close();
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    class DB
	{
	    public DB() {}
	    
	    /**
	     * Connects to a database using the relevant connector
	     * @param dbmstype 1=mysql, 2=mssql
	     * @param db_connect_string
	     * @param db_userid
	     * @param db_password
	     * @return
	     */
	    public Connection dbConnect(int dbmstype, String host, String dbName, String db_userid, String db_password)
	    {
	    	String db_connect_string;
	        try
	        {
	        	switch (dbmstype)
	        	{
	        	case 1: Class.forName("com.mysql.jdbc.Driver");
	        			db_connect_string = "jdbc:mysql://" + host + "/" + dbName ;
	        			break;
	        	case 2: Class.forName("net.sourceforge.jtds.jdbc.Driver");
	        			db_connect_string = "jdbc:jtds:sqlserver://" + host  + "/" + dbName ;
	        			break;
	        	default: java.lang.IllegalArgumentException e = new IllegalArgumentException("unsupported DBMS type");
	        			 throw(e);
	        	}
	        	
	            Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
	            System.out.println("connected");
	            return conn;
	            
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
			return null;
	    }
	    
	   	    
	    public void main(String[] args) 
	    {
	        DB db = new DB();
	        myConn = db.dbConnect(2,"jdbc:jtds:sqlserver://localhost:1433/","SchemaMatching","sa","admin123456");
	    }
	    
	    
	    
	}


	/**
	 * Runs a select query on the db and returns result in a list of string arrays
	 * @param sql : SQL statement to be run
	 * @param numFields : number of fields in result set
	 * @return ArrayList of string arrays each representing a result row
	 */
	public ArrayList<String[]> runSelectQuery(String sql,int numFields) {
		ArrayList<String[]> res = new ArrayList<String[]>();
		try {
			Statement st = myConn.createStatement();
			st.execute(sql);
			ResultSet rs = st.getResultSet();
			while (rs.next())
			{
				String[] ln = new String[numFields];
				for (int i=0;i<numFields;i++) ln[i] = rs.getString(i+1);
				res.add(ln);
			}
		} catch (SQLException e) {
			System.err.print(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
		return res;
	}
	
	public void insertSingleRow(HashMap<Field,Object> values,String tableName)
	{
		StringBuffer fields = new StringBuffer() ;
		StringBuffer valueString = new StringBuffer() ;
		HashMap<String,Integer> fieldIndex = new HashMap<String,Integer>();
		int lastIndex =0;
		Iterator<Field> iterator = values.keySet().iterator();   
	    while (iterator.hasNext()) 
	    {
	    	Field f = iterator.next();
	    	if (fields.length() != 0)
	    	{
	    		fields.append(",");
	    		valueString.append(",");
	    	}
	    	fields.append(f.name);
	    	valueString.append("?");
	    	lastIndex++;
	    	fieldIndex.put(f.name,lastIndex);
	    }
	    String sql = "INSERT INTO " + tableName + "(" + fields.toString() + ") VALUES (" + valueString + ");";

	        try 
	        {
		        PreparedStatement pstmt = myConn.prepareStatement(sql);
		        iterator = values.keySet().iterator();   
			    while (iterator.hasNext()) 
			    {
			    	Field f = iterator.next();
			    	//use the right statement by field type
			        switch(f.type) 
			        {
			        case BOOLEAN:
			        	pstmt.setBoolean(fieldIndex.get(f.name), (Boolean) values.get(f));
			        	break;
			        case BYTE:
			        	pstmt.setByte(fieldIndex.get(f.name), (Byte) values.get(f));
			    	   	break;
			        case SHORT:
			        	pstmt.setShort(fieldIndex.get(f.name), (Short) values.get(f));
			    	   	break;
			        case INT:
			        	pstmt.setInt(fieldIndex.get(f.name), (Integer) values.get(f));
			    	   	break;
			        case LONG:
			        	pstmt.setLong(fieldIndex.get(f.name), (Long) values.get(f));
			    	   	break;
			        case FLOAT:
			        	pstmt.setFloat(fieldIndex.get(f.name), (Float) values.get(f));
			    	   	break;
			        case DOUBLE:
			        	pstmt.setDouble(fieldIndex.get(f.name), (Double) values.get(f));
			    	   	break;
			        case BIGDECIMAL:
			        	pstmt.setBigDecimal(fieldIndex.get(f.name), (BigDecimal) values.get(f));
			    	   	break;
			        case STRING:
			        	pstmt.setString(fieldIndex.get(f.name), (String) values.get(f));
			    	   	break;
			        case DATE:
			        	pstmt.setDate(fieldIndex.get(f.name), (Date) values.get(f));
			    	   	break;
			        case TIME:
			        	pstmt.setTime(fieldIndex.get(f.name), (Time) values.get(f));
			    	   	break;
			        case FILE:
			        	File file = (File) values.get(f);
			        	FileInputStream is = new FileInputStream(file);
			        	pstmt.setBinaryStream(fieldIndex.get(f.name), is, (int)file.length());
			    	   	break;
			    	
			        }
		    	
			    } 
	        
			   // Insert the row
			   pstmt.executeUpdate();
	    } catch (SQLException e) {
	    	System.err.print(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(0);
	    } catch (FileNotFoundException e) {
			System.err.print(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * Runs a delete query on the db
	 * @param sql : SQL statement to be run
	 * @return success / failure
	 */
	public boolean runDeleteQuery(String sql) 
	{
		try 
		{
			Statement st = myConn.createStatement();
			return st.execute(sql);
		} 
		catch (SQLException e) 
		{
	    	System.err.print(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(0);
	    }
		return false;
	}
}
