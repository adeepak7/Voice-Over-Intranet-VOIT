import java.net.InetAddress;
import java.net.ServerSocket;

public class MainFrame {
    static User user ;
    static InetAddress server_IP;
    static int server_port;
    static Ping ping;
    public static void main(String[] args) {
        try {
            ServerSocket [] serverSockets = new ServerSocket[6];
	       
            // read username , number , server_name and server_port from file
            user = new User(  );
            user.setMobileNum( "9881580510" );
	        user.setAddress("127.0.0.1");
	        user.ports = new int[6];
	        for ( int i = 0 ; i < 6 ; i++ ) {
		        user.ports[i] = 10000 + i;
	            serverSockets[i] = new ServerSocket( 10000 +i );
	         }
            server_IP = InetAddress.getByName( "localhost" );
            server_port = 11111;

            
            ping = new Ping(user , server_IP , server_port , serverSockets);
	       
	        //Call
	      //  System.out.println("Calling");
	        //ping.call("9881580510",serverSockets)
        }catch (Exception exception){
        	exception.printStackTrace();
        }
    }
}
