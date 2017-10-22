import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Instance of this class handles all the requests from clients regarding information of other clients.<br/>
 * </p>
 * <p>Along with that it also handles ping messages from all the clients and updates list of all <br/>
 * the clients who are online after each 30 secs.</p>
 */
public class PingHandler implements Runnable{
	private ServerSocket serverSock;
	private  int pingListenPort = 11111;
	private HashMap<Long,ClientThread> activeClients;
	private static  Connection con;
	
	public PingHandler() throws Exception {
		
		con = getConnection();
		serverSock = new ServerSocket( pingListenPort );
		activeClients = new HashMap <>(  );
		//This thread listens to port for ping requests.
		Thread acThread = new Thread( this );
		acThread.start();
		
		//Check Thread starts
		//This thread runs after each 20 sec
		Thread checkThread = new Thread( new Runnable( ) {
			@Override
			public void run() {
				
				while ( true ){
					
					//Check active clients after 30 sec
					synchronized ( activeClients ){
						//Temporary map used to store info of clients who are "NOW" online
						HashMap<Long,ClientThread> temp = new HashMap <>( );
						
						for (Map.Entry<Long,ClientThread> entry:activeClients.entrySet()){
							
							Long mobile_no= entry.getKey();
							ClientThread clienttThread = entry.getValue();
							if(clienttThread.mark){
								temp.put( mobile_no,clienttThread );
							}
							else{
								//Update ***** database(set to false) and set thread to null
								clienttThread.thread = null;
								try {
									updateStatus( clienttThread.user,false );
								}
								catch ( Exception e ){
									System.out.println("ERROR: while updating status at 20sec rounds");
									e.printStackTrace();
								}
							}
						}
						
						//get the new list
						activeClients = temp;
					}
					
					try { Thread.sleep( 30000 );}
					catch( InterruptedException e ){e.printStackTrace( );}
				}
				
			}
		} );
		
		checkThread.start();
		//Check Thread ends
		
	}
	@Override
	public void run() {
		
			while ( true ) {
				try {
					Socket client = null;
					ObjectOutputStream dos = null;
					ObjectInputStream dis = null;
					client = serverSock.accept( );
					System.out.println( "FOUND SUCCESS:" + client.getLocalAddress( ) );
					//Establish streams
					try {
						
						dos = new ObjectOutputStream( client.getOutputStream( ) );
						InputStream inputStream = client.getInputStream( );
						dis = new ObjectInputStream( inputStream );
						Thread.sleep( 1000 );
						
					}
					catch ( Exception e ){
						if(dos!=null)dos.close();
						if(dis!=null)dis.close();
						System.out.println( "Exception while establishing streams...");
						e.printStackTrace();
						continue;
					}
					User user = ( User ) dis.readObject( );
					System.out.println( "Object read by server..." );
					dis.close( );
					dos.close( );
					client.close( );
					//check for special request
					if ( user.address == "XXXX" ) {
						System.out.println("Requested Accepted:" );
						Socket finalClient = client;
						Thread replyQuery = new Thread( new Runnable( ) {
							@Override
							public void run() {
								try {
									User receiver = isAvailable( user.getMobileNum( ) );
									//Create connection back to requester
									Socket sc = new Socket( finalClient.getInetAddress( ), user.ports[ 1 ] );
									;
									
									ObjectOutputStream oos = new ObjectOutputStream( sc.getOutputStream( ) );
									oos.writeObject( receiver );
									oos.close( );
									sc.close( );
								} catch ( Exception e ) {
									e.printStackTrace( );
								}
								
							}
						} );
						
						replyQuery.start( );
						replyQuery.join( );
						client.close( );
						continue;
					}
					
					//Close the streams as we have sufficient info.
					System.out.println( "GLORY:User Thread Started" );
					//Run thread for each client
					Thread waitThread = new Thread( new Runnable( ) {
						@Override
						public void run() {
							//Start thread for 10 sec ,add object to map
							ClientThread cliThread = new ClientThread( this, true );
							cliThread.user = user;
							synchronized ( activeClients ) {
								activeClients.put( Long.parseLong( user.getMobileNum( ) ), cliThread );
							}
							
							//sleep for 10 sec
							try {
								Thread.sleep( 20000 );
							} catch ( InterruptedException e ) {
								e.printStackTrace( );
							}
							
							//After wake up set mark to false(timed out)
							cliThread.mark = false;
						}
					} );
					
					
					for ( HashMap.Entry en : activeClients.entrySet( )
							) {
						Long key = ( Long ) en.getKey( );
						System.out.println( key );
					}
					
					Long key = Long.parseLong( user.getMobileNum( ) );
					if ( activeClients.containsKey( key ) ) {
						ClientThread oldThread = activeClients.get( key );
						oldThread.thread = null;
						oldThread.thread = waitThread;
						activeClients.put( key, oldThread );
					} else {
						//Newly arrived user add it to list and ****** update database(set to true )
						if ( !isRegistered( user ) ) {
							addUser( user );
						} else {
							//set mark to true
							System.out.println( "From here" );
							updateStatus( user, true );
						}
						synchronized ( activeClients ) {
							System.out.println( "ADDED TO ACTIVE CLIENTS" );
							activeClients.put( key, new ClientThread( waitThread, true ) );
						}
					}
					waitThread.start( );
				}
				catch (Exception e){
					
					e.printStackTrace();
				}
		}
	}
	
	private  static  void updateStatus(User user,boolean status) throws Exception {
		
		String s="'N'";
		if(status)
			s="'Y'";
		String query = "update userinfo\n" +
				               "set isavailable = "+s+"\n" +
				               " where mobile_no = ?" ;
		System.out.println("In update status");
		PreparedStatement stmt = con.prepareStatement( query );
		stmt.setString( 1,user.getMobileNum() );
		stmt.executeUpdate();
	}
	
	private  static  void addUser(User user) throws Exception {
		String query = "insert into userinfo\n" + "values (?,?,?,'Y',?,?,?,?,?,?)";
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString( 1,user.getMobileNum() );
		stmt.setString( 2,user.getUsername() );
		stmt.setString( 3,user.getAddress() );
		for ( int i = 0 ; i < 6 ; i++ ) {
			stmt.setInt( 4+i,user.getPorts()[i] );
		}
		stmt.executeQuery();
	}
	
	private static boolean isRegistered(User new_user) throws ClassNotFoundException, SQLException {
		
		Statement query = con.createStatement();
		ResultSet rset = query.executeQuery( "select mobile_no,isavailable from userinfo" );
		while ( rset.next() ){
			String mob_no = rset.getString( 1 );
			if(new_user.getMobileNum() != null){
				if(new_user.getMobileNum().equals( mob_no )){
					return  true;
				}
			}
		}
		return false;
	}
	
	private static User isAvailable(String number) throws SQLException, ClassNotFoundException {
		Statement query = con.createStatement();
		ResultSet rset = query.executeQuery( "select * from userinfo\n where mobile_no = "
				                                     + number);
		while ( rset.next() ){
			String status = rset.getString( 4 );
			if(status.equals( "N" )){
				return null;
			}
			else {
				User request = new User(  );
				request.mobileNum = number;
				request.username = rset.getString( 2 );
				request.address = rset.getString( 3 );
				request.ports = new int[6];
				for ( int i = 0 ; i <  6; i++ ) {
					request.ports[i] =  rset.getInt( i+ 5);
				}
				return request;
			}
		}
		return  null;
	}
	
	private static  Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName( "oracle.jdbc.OracleDriver" );
		Connection con = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:XE","server","server" );
		return  con;
	}
	
	public static void main( String[] args ) throws Exception {
		
		new PingHandler();
		User user = new User(  );
		user.username="omjego";
		user.mobileNum="8855906207";
		user.ports = new int[6];
		for ( int i = 0 ; i <  6; i++ ) {
			user.ports[i] = i;
		}
		user.address = "127.0.0.1";
		//addUser( user );
		//updateStatus( user );
		if(isRegistered(  user)){
			System.out.println("FOUND BABY" );
		}
		else{
			System.out.println("NOT FOUND" );
		}
		
	}
}

