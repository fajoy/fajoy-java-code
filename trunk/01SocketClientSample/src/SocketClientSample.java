import java.io.BufferedReader;        // 引用串流功能 
import java.io.IOException;            // 引用IO例外功能
import java.io.InputStreamReader;    // 引用輸入串流讀取功能 
import java.net.InetAddress;        // 引用網路IP位址功能
import java.net.Socket;    

public class SocketClientSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        // 用來存放伺服器IP位址的變數
        InetAddress serverIp;
 
        // 嘗試連接Server
        try {
            // 設定IP
            serverIp = InetAddress.getByName("localhost");
 
            // 設定port
            int serverPort=5050;
 
            // 初始socket連接
            Socket clientSocket=new Socket(serverIp,serverPort);
 
            // 接收來自Server的訊息 
            BufferedReader  br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
 
            // 顯示收到的訊息
            System.out.println(br.readLine());
 
            // 關閉連線
            clientSocket.close(); 
        } catch (IOException e) {
            // 出錯後顯示錯誤訊息
        	 System.out.println("Connect error.");
        }
	}

}
