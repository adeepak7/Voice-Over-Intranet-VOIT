import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class User implements Comparable<User> ,Serializable{
	String username;
	String mobileNum;
	String address;
	int ports[];
	
	public User( String username, String mobileNum, String address, int[] ports ) {
		this.username = username;
		this.mobileNum = mobileNum;
		this.address = address;
		this.ports = ports;
	}
	
	public User() {
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername( String username ) {
		this.username = username;
	}
	
	public String getMobileNum() {
		return mobileNum;
	}
	
	public void setMobileNum( String mobileNum ) {
		this.mobileNum = mobileNum;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress( String address ) {
		this.address = address;
	}
	
	public int[] getPorts() {
		return ports;
	}
	
	@Override
	public int compareTo( @NotNull User o ) {
		return mobileNum.compareTo( o.mobileNum );
		
	}
}
