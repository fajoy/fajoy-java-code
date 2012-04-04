import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class StreamHandlerSample {

	public static void main(String[] args) throws UnknownHostException, IOException {      
          // 設定port
          int serverPort=5050;

          // 初始socket連接
          Socket clientSocket=new Socket("localhost",serverPort);
          StreamHandler sh =new StreamHandler(clientSocket);
          sh.setReadLineHander(new ReadLineHandler<StreamHandler>() {
			
			@Override
			public void action(StreamHandler sender, String line) {
				System.out.print(line);
				
			}
          });
          sh.beginAsyncReadline();

	}

}
