package ac.technion.schemamatching.Server;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import technion.iem.schemamatching.dbutils.DBInterface;

public class SchemamatchingClient implements Runnable {

	private Socket _socket;//SOCKET INSTANCE VARIABLE
	protected DBInterface db;
	
	public SchemamatchingClient(Socket s)
	{
		_socket = s;//INSTANTIATE THE SOCKET
	}
	
	public void init()
	{
		
		Properties pMap = new Properties();
		try {
			pMap.load(new FileInputStream("oreConfig/ob_interface.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println((String)pMap.get("dbmstype") + " " + (String)pMap.get("host") + " " + (String)pMap.get("dbname") + " " + (String)pMap.get("username") + " " + (String)pMap.get("pwd"));
	    db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
		
		
	}
	
	@Override
	public void run() //(IMPLEMENTED FROM THE RUNNABLE INTERFACE)
	{
		try //HAVE TO HAVE THIS FOR THE in AND out VARIABLES
		{
			Scanner in = new Scanner(_socket.getInputStream());//GET THE SOCKETS INPUT STREAM (THE STREAM THAT YOU WILL GET WHAT THEY TYPE FROM)
			PrintWriter out = new PrintWriter(_socket.getOutputStream());//GET THE SOCKETS OUTPUT STREAM (THE STREAM YOU WILL SEND INFORMATION TO THEM FROM)
			init();
			
			while ( _socket.isConnected() )//WHILE THE PROGRAM IS RUNNING
			{		
				
				if (in.hasNext())
				{
					String input = in.nextLine();//IF THERE IS INPUT THEN MAKE A NEW VARIABLE input AND READ WHAT THEY TYPED
					System.out.println("Client Said: " + input);//PRINT IT OUT TO THE SCREEN
					
					System.out.println("Try to read from db");
					ArrayList<String[]> list =  db.runSelectQuery("SELECT * FROM datasets", 6);
					System.out.println("finish read from db");
					System.out.println("list size = " + list.size());
					String listString = "";

					for (String[] s : list)
					{
						listString += s[1] + ",";
					}
					
					out.println("Server  Said : " + listString);//RESEND IT TO THE CLIENT
					out.flush();//FLUSH THE STREAM
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY THERE WONT BE AN ERROR BUT ITS GOOD TO CATCH
		}	
	
	       
     
        
    }

}
