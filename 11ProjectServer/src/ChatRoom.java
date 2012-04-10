import java.io.*;
import java.net.Socket;
import java.util.*;


public class ChatRoom extends ServerCore{
	public Map<String, ChatUser>users=new HashMap<String, ChatUser>();
	public Map<Integer,ChatPost>posts=new HashMap<Integer, ChatPost>();
	public StreamHandler sys=null;
	public StreamHandler con_log=null;
	public int flowMsgID=0;
	public ChatRoom(int port) throws IOException {
		super(port);
		sys=new StreamHandler(System.in, System.out);
	}

	public void beginAccept(){
		FileOutputStream os;
		try {
			os = new FileOutputStream("./connect_log.txt",false);
			try {
				con_log=new StreamHandler(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while(true){
			try {
				Socket socket=accept();
				con_log.writeLine(String.format("%s/%d\t%d",socket.getInetAddress().getHostAddress(),socket.getPort(),++this.flowLogID));
				con_log.flush();
				ChatUser user=new ChatUser(this,socket);
				addClient(user, socket);
				user.beginLogin();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public ChatUser getUser(String userName){
		return this.users.get(userName);
	}
	public synchronized  void castWrite(ChatUser[] users,String arg0){
		for (ChatUser user : users) {
			if(user.isLogin){
				user.write(arg0);
				user.flush();
			}
		}
	}
	public synchronized  void castWriteLine(ChatUser[] users,String arg0){
		this.castWrite(users,String.format("%s\n", arg0));
	}
	public synchronized  void castWriteLine(String arg0){
		ChatUser[] set=new ChatUser[this.clients.size()];
		this.clients.keySet().toArray(set);
		this.castWriteLine(set,arg0);
	}


}
