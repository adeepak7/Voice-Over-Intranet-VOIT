import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestHandler implements  Runnable{
	private  int  requestPort = 11112;
	private ServerSocket lisSocket ;
	RequestHandler() throws IOException {
		lisSocket = new ServerSocket( requestPort );
		
		//create listen thread
		Thread listenThread = new Thread( this );
		listenThread.start();
		
	}
	public static void main( String[] args ) {
	}
	
	@Override
	public void run()
	{
		try
		{
			while ( true )
			{
				Socket client = lisSocket.accept();
				ObjectInputStream ois = new ObjectInputStream( client.getInputStream() );
				User request = (User) ois.readObject();
				
			}
		}
		catch ( Exception e ){
			
			System.out.println("ERROR: in RequestHandler" );
			e.printStackTrace();
		}
		
	}
}
