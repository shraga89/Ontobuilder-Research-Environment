package ac.technion.schemamatching.Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SchemamatchingServer implements Runnable
{
	static final int InfiniteClients = -1;
	private final int _port;
	private final int _maxNumberOfClients;
	private ServerSocket _server;
	private final Map<UUID, Thread> _clientsMapThread;
	int _numberOfClients;
	
	String MainFolder = "C:\\ORE\\OREOutput\\";
	
	int NextPortForClient = 2500;
	
	public SchemamatchingServer(int port , int maxNumberOfClients)
	{
		_port = port;
		_maxNumberOfClients = maxNumberOfClients;
		_numberOfClients = 0;
		_clientsMapThread = new HashMap<>();
	}

	public SchemamatchingServer(int port)
	{
		this(port,InfiniteClients);
	}
	
	
	
	public void OpenConncetion()
	{
		try 
		{
			_server = new ServerSocket(_port);
		} 
		catch (IOException e) 
		{
			System.out.println("An error occured when we try to open server conncetion.");//IF AN ERROR OCCURED THEN PRINT IT
			e.printStackTrace();
			return;
		} 
		System.out.println("Waiting for clients...");
	}

	@Override
	public void run() 
	{
		while (true)//WHILE THE PROGRAM IS RUNNING
		{												
			Socket s;
			try {
				s = _server.accept();
				if(_numberOfClients == _maxNumberOfClients)
				{
					System.out.println("Client try to connected from " + s.getLocalAddress().getHostName());
					System.out.println("Server Reject the request Because the restriction of numbers of clients" );
				}
				
				UUID clientId = UUID.randomUUID();
				CreateClientFolder( clientId);
				SchemamatchingClient client = new SchemamatchingClient( clientId, BuildClientFolderName(clientId),NextPortForClient,s,this);//CREATE A NEW CLIENT OBJECT
				Thread t = new Thread(client);//MAKE A NEW THREAD
				t.start();//START THE THREAD
				
				_clientsMapThread.put(clientId , t);
				_numberOfClients++;
				NextPortForClient++;
					
				System.out.println("Client connected from " + s.getLocalAddress().getHostName() + " , Number of current clients is : " + _numberOfClients);	//	TELL THEM THAT THE CLIENT CONNECTED
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//ACCEPT SOCKETS(CLIENTS) TRYING TO CONNECT
					
		}
		
	}
	
	
	public void ClientDisconnected(UUID clientId)
	{
		_clientsMapThread.remove(clientId);
		_numberOfClients--;
		System.out.println("Client Disconnected , Number of current clients is : " + _numberOfClients);
		DeleteClientFolder( clientId);
	}
	
	private void CreateClientFolder(UUID clientId)
	{
		String dirName = BuildClientFolderName( clientId);
		File directory = new File(dirName);
		if(directory.exists())
		{
			return;
		}
		if (!directory.mkdirs())
			System.err.println("Could not create client folder");
	}
	private void DeleteClientFolder(UUID clientId)
	{
		String dirName = BuildClientFolderName( clientId);
		File directory = new File(dirName);
		if(!directory.exists())
		{
			return;
		}
		delete(directory);
	}
	private String BuildClientFolderName(UUID clientId)
	{
		return MainFolder + clientId;
	}
	

	public static void delete(File file)
	{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(Objects.requireNonNull(file.list()).length==0){
	    			if (file.delete())
	    		   		System.out.println("Directory is deleted : " + file.getAbsolutePath());
	    			
	    		}else{
	    			
	    		   //list all the directory contents
	        	   String[] files = file.list();

					assert files != null;
					for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	        		 
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	        		
	        	   //check the directory again, if empty then delete it
	        	   if(Objects.requireNonNull(file.list()).length==0){
	           	     if (file.delete())
	        	     System.out.println("Directory is deleted : " 
	                                                  + file.getAbsolutePath());
	        	   }
	    		}
	    		
	    	}else{
	    		//if file, then delete it
	    		if (file.delete())
	    		System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	}
		
}
