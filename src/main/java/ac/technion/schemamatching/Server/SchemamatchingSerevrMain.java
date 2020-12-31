package ac.technion.schemamatching.Server;

public class SchemamatchingSerevrMain {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		SchemamatchingSerevr server = new SchemamatchingSerevr(1234);
		server.OpenConncetion();
		Thread t = new Thread(server);
		t.start();
		
		
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
