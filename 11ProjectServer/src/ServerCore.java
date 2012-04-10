import java.io.IOException;
import java.net.*;
import java.util.*;
public class ServerCore {
	public Map<StreamHandler,Socket> clients=new HashMap<StreamHandler,Socket>();
	private ServerSocket serverSocket=null;
	public int flowLogID=0;
	public ServerCore (int port) throws IOException {
	      serverSocket =new ServerSocket(port);
	    
	}
	
	public Socket accept() throws IOException{
		return serverSocket.accept();
	}

	protected void addClient(StreamHandler streamHandler,Socket socket){
		clients.put(streamHandler,socket);
	}
	public Socket getSocket(StreamHandler sh){
		return clients.get(sh);
	}

}
