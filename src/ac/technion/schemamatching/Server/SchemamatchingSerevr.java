package ac.technion.schemamatching.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SchemamatchingSerevr implements Runnable
{
	static final int InfiniteClients = -1;
	private int _port;
	private int _maxNumberOfClients;
	private ServerSocket _server;
	
	int _numberOfClients;
	List<Thread> _clientsListThread ;
	
	public SchemamatchingSerevr(int port , int maxNumberOfClients)
	{
		_port = port;
		_maxNumberOfClients = maxNumberOfClients;
		_numberOfClients = 0;
		_clientsListThread = new ArrayList<Thread>();
	}

	public SchemamatchingSerevr(int port) 
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
				
				System.out.println("Client connected from " + s.getLocalAddress().getHostName());	//	TELL THEM THAT THE CLIENT CONNECTED
				SchemamatchingClient client = new SchemamatchingClient(s);//CREATE A NEW CLIENT OBJECT
				Thread t = new Thread(client);//MAKE A NEW THREAD
				t.start();//START THE THREAD
				_clientsListThread.add(t);
				_numberOfClients++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//ACCEPT SOCKETS(CLIENTS) TRYING TO CONNECT
					
		}
		
	}
	
}
