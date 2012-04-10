import java.io.*;
import java.net.Socket;
import java.util.*;


public class ChatRoomClient extends StreamHandler {
	//public Map<String, UserModel>users=new HashMap<String, UserModel>();
	public Map<Integer,ChatPost> posts=new HashMap<Integer, ChatPost>();
	public StreamHandler sys=null;
	public boolean isLeave=false;
	private Socket sock=null;
	public ChatRoomClient (Socket sock) throws IOException {
		super(sock);
		this.sock=sock;
		this.sys=new StreamHandler(System.in, System.out);
	}
	public static ChatRoomClient connect(String serverHost, int port) throws IOException {
		Socket sock=new Socket(serverHost, port);
		ChatRoomClient s=new ChatRoomClient(sock);
		return s;
	}
	
	public void beginLogin(){
		sys.writeLine("Username:");
		sys.flush();
		//this.setReadLineHander(entDebug);
		this.setReadLineHander(entShowMsg);
		this.setReadLineHander(entRequestPost);
		this.setReadLineHander(entRequestRemove);
		
		this.beginAsyncReadline();
		while(_isReading){
        String line=sys.readLine();
		String cmd="/connect ";
		if(cmd.length()<line.length()){
 		 if(line.substring(0,cmd.length()).equals(cmd)){
 			 	try{
				String msgline=line.substring(cmd.length());
				int msgi=msgline.indexOf(" ");
				String strHost=msgline.substring(0,msgi);
				String strPort=msgline.substring(msgi+1);
				int port=Integer.parseInt(strPort);
				
				if(ChatClient.connect(strHost, port)){
					this.writeLine("/leave");
					this.flush();
					this.sock.close();
					break;
				}
				}catch(Exception e){
					sys.writeLine("Connect string format error.");
					sys.flush();
					continue;
				}
 		 }
		}
		 
			 
		 if(line.equals("/showpost")){
			 showPost();
		 	continue;
		 }
		 this.writeLine(line);
		 this.flush();
		 if(isLeave){
			 sys.writeLine("Connect Over.");
			 sys.flush();
			 break;
		 }
		}
		isLeave=true;
	}
	public void showPost(){
			for(Object obj2:posts.values().toArray()){
				ChatPost post=(ChatPost)obj2;
				sys.writeLine(String.format("%s posted message '%d' in String: %s",post.userName,post.msgid,post.value));		
			}
		sys.flush();
		
	}
	/*
	public ChatPost getUserPost(String userName,int msgid){
		UserModel user= this.users.get(userName);
		if(user==null)
			return null;
		return user.posts.get(msgid);
	}
	*/
	public void setUserPost(String userName,int msgid,String value){
		ChatPost post=new ChatPost(userName, msgid, value);
		/*
		UserModel user=users.get(userName);
		if(user==null){
			user=new UserModel(userName);
			users.put(userName, user);
		}*/
		posts.put(msgid, post);
		sys.writeLine(String.format("%s posted message '%d' in String: %s",post.userName,post.msgid,post.value));
		sys.flush();
	}
	 private ReadLineHandler<StreamHandler> entDebug=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				//ChatRoomClient user=(ChatRoomClient)sender;
				//System.out.println(line);
				showMsg(line);
			}
	 };
	 private ReadLineHandler<StreamHandler> entShowMsg=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				//ChatRoomClient user=(ChatRoomClient)sender;
				//System.out.println(line);
				String cmd="/msg ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				String msg=line.substring(cmd.length());
				showMsg(msg);
			}
	 };
	 
	 private ReadLineHandler<StreamHandler> entRequestPost=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				//ChatRoomClient user=(ChatRoomClient)sender;
				//System.out.println(line);
				String cmd="/post ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
				String msgline=line.substring(cmd.length());
				int msgi=msgline.indexOf(" ");
				String userName=msgline.substring(0,msgi);
				msgline=msgline.substring(msgi+1);
				msgi=msgline.indexOf(" ");
				int msgid=Integer.parseInt(msgline.substring(0,msgi));
				String value=msgline.substring(msgi+1);
				setUserPost(userName, msgid, value);
			}
	 };
	 
	 private ReadLineHandler<StreamHandler> entRequestRemove=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				//ChatRoomClient user=(ChatRoomClient)sender;
				//System.out.println(line);
				String cmd="/remove ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
				String msgline=line.substring(cmd.length());
				int msgi=msgline.indexOf(" ");
				String userName=msgline.substring(0,msgi);
				msgline=msgline.substring(msgi+1);
				int msgid=Integer.parseInt(msgline);
				ChatPost post=posts.get(msgid);
				posts.remove(msgid);
				sys.writeLine(String.format("%s remove message '%d': %s",userName,post.msgid,post.value));
				sys.flush();
			}
	 };
	 private void showMsg(String msg){
		 sys.writeLine(msg);
		 sys.flush();
	 }
	 @Override
	 protected void readLineError(IOException e) {
			//super.readLineError(e);
			isLeave=true;
	}
	@Override
	protected void writeError(IOException e) {
				//super.readLineError(e);
			isLeave=true;
	}	

}
