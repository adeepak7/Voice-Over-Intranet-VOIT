import java.io.*;
import java.net.*;
public class Ping {
    String server_IP ;
    int server_port ;
    Socket ping_socket ;
    Socket ping_response ;
    Socket control_send;
    Socket control_receive;
    User user;
    User receiver;

    Ping(User user , InetAddress server_IP, int server_port , ServerSocket [] serverSockets)
    {
        try {
            this.user = user;
            this.server_IP = server_IP.getHostAddress();
            this.server_port = server_port;
            serverSockets[0].close();
           // System.out.println(server_IP+" "+ server_port +" "+ user.getAddress()+" "+user.getPorts()[0]);
            ping_socket = new Socket(server_IP, server_port,InetAddress.getByName( user.getAddress() ),user.getPorts()[0]);
            serverSockets[1].close();
	        //Change address of user
            ping_response = new Socket(server_IP, server_port ,  InetAddress.getByName(user.getAddress()),user.getPorts()[1]);
	        System.out.println("Google");
	        pingUpdate();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    // Calling method
    public void getUserIP(String number,ServerSocket [] serverSockets)
    {
        try {
        Thread callReq = new Thread() {
            public void run() {
                try{
                    //synchronized ( ping_socket )
                    {
                    	//Modified here by OJ
                    /*
                    Instead of writing number we'll write User object and set its ip-address to XXXX
                     */
                        
	                    System.out.println("SERVER PORT:"+ server_port );
	                    ping_socket = new Socket(server_IP, server_port);
	                    System.out.println("Connected to Server:" );
	                    ObjectInputStream info_objectInputStream = new ObjectInputStream( ping_socket.getInputStream( ) );
	                    System.out.println("Done 1" );
	                    ObjectOutputStream info_objectOutputStream = new ObjectOutputStream( ping_socket.getOutputStream( ) );
	                    
	                    System.out.println("Done 2" );
	
	                    User recieverInfo = new User( );
	                    recieverInfo.setMobileNum( number );
	                    recieverInfo.setAddress( "XXXX" );
	                    recieverInfo.ports = user.ports;
	                    info_objectOutputStream.writeObject( recieverInfo );
	                   // receiver = (User)info_objectInputStream.readObject();
	                    //In case the process doesn't complete and user has gone for call
	                    info_objectOutputStream.close();
	                    info_objectInputStream.close();
                    }
                }
                catch (Exception exception) {
                	exception.printStackTrace();
                }
            }
        };

        callReq.start();
        callReq.join();
        serverSockets[2].close();
	        
	   
        //control_send = new Socket(receiver.getAddress(), receiver.getPorts()[3] ,  InetAddress.getByName(user.getAddress()),user.getPorts()[2]);
        serverSockets[3].close();
       // control_receive = new Socket(receiver.getAddress(), receiver.getPorts()[2] ,  InetAddress.getByName(user.getAddress()),user.getPorts()[3]);
        serverSockets[4].close();
        serverSockets[5].close();

        }
        catch (Exception exception){
        	exception.printStackTrace();
            System.out.println(exception);
        }
    }
	
	//Pinging method
	public void pingUpdate()
	{
		try {
			
			Thread PingUpdate = new Thread() {
				public void run() {
					
					while(true) {
						try {
							//synchronized ( ping_socket )
							{   ping_socket.close();
								ping_socket = new Socket( server_IP, server_port );
								ObjectInputStream ping_objectInputStream = new ObjectInputStream( ping_socket.getInputStream() );
								ObjectOutputStream ping_objectOutputStream = new ObjectOutputStream( ping_socket.getOutputStream( ) );
								ping_objectOutputStream.writeObject( user );
								ping_objectOutputStream.close( );
								ping_objectInputStream.close();
								ping_socket.close( );
								System.out.println("OBJECT Written" );
							}
							sleep(10000);
						} catch (Exception exception) {exception.printStackTrace();}
					}
				}
			};
			PingUpdate.start();
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
	}
    //ping rsp method
    public void callUser()
    {   try {

        ObjectOutputStream call_objectOutputStream = new ObjectOutputStream(control_send.getOutputStream());
        ObjectInputStream call_objectInputStream = new ObjectInputStream(control_receive.getInputStream());

        Thread callAccept = new Thread(){
            public void run()
            {
                try{
                    receiver = (User) call_objectInputStream.readObject();
	                if(receiver == null){
		                System.out.println("User not found...." );
	                }
	                else{
		                System.out.println("User Found : Name->"+receiver.getUsername() );
	                }
                    if(receiver!=null)
                    {
                        //capture & receive
                        Thread send = new Thread()
                        {
                            public void run(){
                                try {
                                    new CaptureAndSendUDP( InetAddress.getByName( receiver.getAddress()),receiver.getPorts()[5]);
                                } catch ( UnknownHostException e ) {
                                    e.printStackTrace( );
                                }
                            }
                        };
                        Thread receive = new Thread()
                        {
                            public void run(){new ReceiveAndPlayUDP(user.getPorts()[5]);}
                        };
                        receive.start();
                        send.start();
                    }
                    else
                    {
                        System.out.println("Call not accepted");
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        };

        Thread callRequest = new Thread() {
            public void run() {
                try{
                    call_objectOutputStream.writeObject(user);
                    sleep(15000);
                    callAccept.stop();
                    call_objectInputStream.close();
                    call_objectOutputStream.close();
                }
                catch (Exception exception) {
                	exception.printStackTrace();
                    System.out.println(exception);
                }
            }
        };

        callAccept.start();
        callRequest.start();
    }
    catch (Exception exception){
    	exception.printStackTrace();
        System.out.println(exception);
    }
    }

    public void call (String number , ServerSocket [] serverSockets)
    {
        getUserIP(number,serverSockets);
	    System.out.println("Reieved Info:"+receiver.getUsername() );
	    //callUser();
    }
   

}
