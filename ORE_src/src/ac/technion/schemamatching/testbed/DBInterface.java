package ac.technion.schemamatching.testbed;
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
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

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
        myConn = myDB.dbConnect(1,"localhost:3306","schemamatching","temp","");       
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
	        myConn = db.dbConnect(1,"localhost:3306/","schemaMatching","temp",null);
	    }
	    
	    
	    
	}
    
    /**
     * Run an update query on the DB
     * @param sql
     * @return true is succeeded
     */
    public boolean runUpdateQuery(String sql)
    {
    	try {
			Statement st = myConn.createStatement();
			st.execute(sql);
			return true;
		} catch (SQLException e) {
			System.err.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
    	return false;
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
	
	/**
	 * Insert a single row of data to the specified table. Fields and Objects are used to maintain cross-database compatability
	 * @param values Hashmap of fields with their corresponding values.
	 * @param tableName
	 */
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
	
	/**
	 * Receive an array list of matches and 2 ontologies and upload them to an Ontobuilder Match Information object
	 * @param candidate Ontology object of candidate schema
	 * @param target Ontology object of target schema
	 * @param matchList [SMID,CandidateSchemaID,CandidateTermID,TargetSchemaID, TargetTermID,Confidence]
	 * @return Ontobuilder Match Information object with supplied matches
	 */
	public static MatchInformation createMIfromArrayList(Ontology candidate,Ontology target, ArrayList<String[]> matchList) {
		MatchInformation mi = new MatchInformation(candidate,target);
		ArrayList<Match> matches = new ArrayList<Match>();
		for (String[] match : matchList) 
		{
			Term c = candidate.getTermByID(Long.parseLong(match[2]));
			Term t =  target.getTermByID(Long.parseLong(match[4]));
			assert (c!=null && t!=null);
			matches.add(new Match(t,c,Double.parseDouble(match[5])));
		}
		mi.setMatches(matches );
		return mi;
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		DBInterface dbi = new DBInterface();
	}
}
