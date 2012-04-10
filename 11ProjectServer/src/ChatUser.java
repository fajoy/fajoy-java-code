import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
public class ChatUser extends StreamHandler{
	public ChatRoom room=null;
	public Socket sock=null;
	public String userName=null;
	public boolean isKick=false;
	public boolean isLogin=false;
	public StreamHandler input_log=null;
	public StreamHandler output_log=null;
	public ChatUser(ChatRoom room,Socket socket) throws IOException{
		super(socket.getInputStream(),socket.getOutputStream());
		this.room=room;
		this.sock=socket;
	}
	public void beginLogin(){		
		setReadLineHander(entInputUserName);
		beginAsyncReadline();
	}
	
	public void dumpPost(){
		/*
		for(Object obj:room.users.values().toArray()){
			ChatUser user=(ChatUser)obj;
			*/
			for(Object obj2:room.posts.values().toArray()){
				ChatPost post=(ChatPost)obj2;
				this.writeLine(String.format("/post %s %d %s",post.userName, post.msgid,post.value));
			}
		//}
		this.flush();
	}
	 private ReadLineHandler<StreamHandler> entInputUserName=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				line=line.replace(" ", "");
				line=line.replace("/", "");
					if(line.isEmpty()){
						user.writeLine("/msg Error: No username is input.");
						user.writeLine("/msg Username:");
						user.flush();
						return;
					}
						
					if(room.getUser(line)!=null){
						String err=String.format("/msg Error: The user '%s' is already online. Please change a name.", line);
						user.writeLine(err);
						user.writeLine("/msg Username:");
						user.flush();
					}else{
						user.userName=line;
						room.castWriteLine(String.format("/msg %s is connecting to the chat server.",user.userName));
						user.isLogin=true;
						try {
							output_log=new StreamHandler(new FileOutputStream(String.format("./output_%s.txt",user.userName),true));
						} catch (Exception e) {	}
						try {
							input_log=new StreamHandler(new FileOutputStream(String.format("./input_%s.txt",user.userName),true));
						} catch (Exception e) {	}
						
						room.users.put(line, user);
						user.writeLine("/msg *******************************************");
						user.writeLine(String.format("/msg ** <%s>, welcome to the chat system.", user.userName));
						user.writeLine("/msg *******************************************");
						user.flush();
						dumpPost();
						user.clearReadLineHander();
						user.setReadLineHander(entInputYell);
						user.setReadLineHander(entInputTell);
						user.setReadLineHander(entInputWho);
						user.setReadLineHander(entInputPost);
						user.setReadLineHander(entInputRemove);
						user.setReadLineHander(entInputKick);
						user.setReadLineHander(entInputLeave);
					}
			}
	 };
	 
	 private ReadLineHandler<StreamHandler> entInputYell=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/yell ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
				String msg=line.substring(cmd.length());
				room.castWriteLine(String.format("/msg %s yelled:%s",user.userName, msg));
				//showMsg(line.substring(cmd.length()));
			}
	 };
	
	 private ReadLineHandler<StreamHandler> entInputTell=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/tell ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
				String msgline=line.substring(cmd.length());
				int msgi=msgline.indexOf(" ");
				if(msgi<=0){
					String err=String.format("/msg Error: No target was given.");
					user.writeLine(err);
					user.flush();
					return;
				}
				String username=msgline.substring(0,msgi);
				ChatUser u=room.getUser(username);
				if(u==null){
					String err=String.format("/msg Error: The user '%s' is not online.", username);
					user.writeLine(err);
					user.flush();
					return;
				}
				
				String msg=line.substring(msgi+1);
				u.writeLine(String.format("/msg %s told %s: %s",user.userName,u.userName, msg));
				u.flush();
				//showMsg(line.substring(cmd.length()));
			}
	 };
	 private ReadLineHandler<StreamHandler> entInputWho=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/who";
				if(cmd.length()>line.length())
					return;
				int msgi=line.indexOf(" ");
				if(msgi==-1)
					msgi=line.length();
				if(!line.substring(0,msgi).equals(cmd))
					return;
				user.writeLine("/msg Name\tIP/port");
				for(Object obj : room.clients.toArray()){
					ChatUser u=(ChatUser)obj;
					if(u.isKick)
						continue;
					if(u.userName==null){
						user.writeLine(String.format("/msg %s:\t%s/%d","(Unknown)",u.sock.getInetAddress().getHostAddress(),u.sock.getPort()));
						continue;
					}
					if(u==user)
						user.writeLine(String.format("/msg %s:\t%s/%d\t<-- myself",u.userName,u.sock.getInetAddress().getHostAddress(),u.sock.getPort()));
					else
						user.writeLine(String.format("/msg %s:\t%s/%d",u.userName,u.sock.getInetAddress().getHostAddress(),u.sock.getPort()));
				}
				user.flush();
				//showMsg(line.substring(cmd.length()));
			}
	 };
	
	 private ReadLineHandler<StreamHandler> entInputRemove=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/remove ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
								
				String msgline=line.substring(cmd.length());
				if(msgline.length()==0){
					user.writeLine("/msg Error: No msg id.");
					user.flush();
					return;
				}
				
				int msgi=msgline.indexOf(" ");
				if(msgi>=0){
					msgline=msgline.substring(0,msgi);
				}
					
				int postid;
				try {
					postid=Integer.parseInt(msgline);
				} catch (Exception e) {
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return;
				}
					
				ChatPost post=room.posts.get(postid);
				
				if(post==null){
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return;
				}
					
				
				
				room.posts.remove(postid);
				room.castWriteLine(String.format("/remove %s %d",user.userName, post.msgid));
				//showMsg(line.substring(cmd.length()));
			}
	 };
	 
	 private ReadLineHandler<StreamHandler> entInputPost=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/post ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				

				String msg=line.substring(cmd.length());
				ChatPost post=new ChatPost(user.userName, ++room.flowMsgID, msg);
				room.posts.put(post.msgid, post);
				
				room.castWriteLine(String.format("/post %s %d %s",user.userName, post.msgid,post.value));
				//showMsg(line.substring(cmd.length()));
			}
	 };
	 private ReadLineHandler<StreamHandler> entInputKick=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/kick ";
				if(cmd.length()>line.length())
					return;
				if(!line.substring(0,cmd.length()).equals(cmd))
					return;
				
				String msgline=line.substring(cmd.length());
				int msgi=msgline.indexOf(" ");
				if(msgi<0){
					msgi=msgline.length();
				}
				String username=msgline.substring(0,msgi);
				ChatUser u=room.getUser(username);
				if(u==null){
					String err=String.format("/msg Error: The user '%s' is not online.", username);
					user.writeLine(err);
					user.flush();
					return;
				}
				String cast=String.format("/kick %s", u.userName);
				room.castWriteLine(cast);
				u.clearReadLineHander();
				u.isKick=true;
				room.users.remove(u.userName);
				u.setReadLineHander(entInputLeave);
				//showMsg(line.substring(cmd.length()));
			}
	 };
	 
	 private ReadLineHandler<StreamHandler> entInputLeave=new ReadLineHandler<StreamHandler>() {		
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user=(ChatUser)sender;
				String cmd="/leave";
				if(cmd.length()>line.length())
					return;
				int msgi=line.indexOf(" ");
				if(msgi==-1)
					msgi=line.length();
				if(!line.substring(0,msgi).equals(cmd))
					return;
				user.close();
				//showMsg(line.substring(cmd.length()));
			}
	 };
	 private void showMsg(String msg){
		 System.out.println(msg);
	 }
	
	 @Override
	 protected void writeError(IOException e) {
		 
	 }
	 @Override
	protected void readLineError(IOException e) {
		//super.readLineError(e);
		room.clients.remove(this);
		room.users.remove(this.userName);
		String msg=String.format("%s:%d is close", this.sock.getInetAddress().getHostAddress(),this.sock.getPort());
		room.sys.writeLine(msg);
		room.sys.flush();
	}
	 @Override
	public void write(String arg0) {
		super.write(arg0);
		if(isLogin){
		output_log.write(arg0);
		output_log.flush();
		}
	}
	 @Override
	 public String readLine() {
		 String str= super.readLine();
		 if(isLogin){
			input_log.writeLine(str);
			input_log.flush();
		 }
		return str;
	 }
		
}
