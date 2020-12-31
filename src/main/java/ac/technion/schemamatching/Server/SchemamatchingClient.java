package ac.technion.schemamatching.Server;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class SchemamatchingClient implements Runnable {

	private Socket _socket;//SOCKET INSTANCE VARIABLE
	protected OreClientIcd icd;
	private SchemamatchingSerevr _mainServer;
	private UUID _clientId;
	private Scanner in;
	
	public String ClientFolder;
	public int FileTransfrPort;
	
	public Socket getSocket() {
		return _socket;
	}


	private void setSocket(Socket _socket) {
		this._socket = _socket;
	}
	
	public SchemamatchingClient(UUID clientId , String clientFolder , int fileTransfrPort, Socket s , SchemamatchingSerevr mainServer)
	{
		setSocket(s);//INSTANTIATE THE SOCKET
		icd = new OreClientIcd();
		_mainServer = mainServer;
		_clientId = clientId;
		ClientFolder = clientFolder;
		FileTransfrPort = fileTransfrPort;
	}
	
	
	@Override
	public void run() //(IMPLEMENTED FROM THE RUNNABLE INTERFACE)
	{
		try //HAVE TO HAVE THIS FOR THE in AND out VARIABLES
		{
			InputStream inputStream = getSocket().getInputStream();
			in = new Scanner(inputStream);
			PrintWriter out = new PrintWriter(getSocket().getOutputStream());//GET THE SOCKETS OUTPUT STREAM (THE STREAM YOU WILL SEND INFORMATION TO THEM FROM)
			int readByte;
			
			while ( true )//WHILE THE PROGRAM IS RUNNING
			{		
				readByte = inputStream.read();
				if(readByte == -1)
				{
					ClientDisconnected();
					return;
				}	
				String input;
				input = in.nextLine();
				input = String.valueOf((char) (readByte)) + input;
				System.out.println("Client Said: " + input);//PRINT IT OUT TO THE SCREEN
				String listString = icd.HandleRequest(this, input);
				String formatted = String.format("%09d", listString.length());
				System.out.println("formatted : " + formatted);
				out.print(formatted + listString);//RESEND IT TO THE CLIENT
				out.flush();//FLUSH THE STREAM
			}
		} //.replace("\n", "").replace("\r", "")
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY THERE WONT BE AN ERROR BUT ITS GOOD TO CATCH
			_mainServer.ClientDisconnected(_clientId);
			return;
		}	
    }
	
	private void ClientDisconnected()
	{
		_mainServer.ClientDisconnected(_clientId);
	}
	

    

	
		
	
}

/*    
 * 
 * 	ServerSocket welcomeSocket = null;
    Socket connectionSocket = null;
    BufferedOutputStream outToClient = null;
    
    private void SendFile(String fileToSend)
    {
    	 if (outToClient != null) 
         {
             File myFile = new File( fileToSend );
             byte[] mybytearray = new byte[(int) myFile.length()];

             FileInputStream fis = null;

             try {
                 fis = new FileInputStream(myFile);
             } catch (FileNotFoundException ex) {
                 // Do exception handling
             }
             BufferedInputStream bis = new BufferedInputStream(fis);

             try {
                 bis.read(mybytearray, 0, mybytearray.length);
                 outToClient.write(mybytearray, 0, mybytearray.length);
                 outToClient.flush();
                 return;
             } 
             catch (IOException ex) 
             {
                 // Do exception handling
             }
         }
    }
 * private void SendFiles()
    {
       File clientDirectory = new File(ClientFolder);
 	   String files[] = clientDirectory.list();
 	   for (String fileToSend : files) 
 	   {
 		  SendFile(fileToSend);
 	   }
    }
    
	private void OutputFileServerConnected()
	{
		try 
	    {
	        welcomeSocket = new ServerSocket(FileTransfrPort);
	        connectionSocket = welcomeSocket.accept();
	        outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
	    } 
	    catch (IOException ex) 
	    {
	    	// Do exception handling
	    }
	}*/
