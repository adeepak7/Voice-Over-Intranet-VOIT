public class ClientThread {
	public  Runnable thread;
	public boolean mark;
	public  User user;
	public ClientThread( Runnable th, Boolean live){
		mark = live;
		thread = th;
	}
}
