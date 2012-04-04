
import java.io.BufferedWriter;        // 引用串流功能
import java.io.IOException;            // 引用例外功能
import java.io.OutputStreamWriter;    // 引用輸出串流功能
import java.net.ServerSocket;         // 引用伺服器socket
import java.net.Socket;                // 引用Socket網路功能

public class SocketServerSample {
    // 宣告一個靜態的server socket
    private static ServerSocket serverSocket;
 
    // 程式進入點
    public static void main(String[] args) {
 
        // 設定port
        int port=5050;
 
        // 嘗試Listen一個連線
        try {
 
            // 初始化Server Socket 
            serverSocket =new ServerSocket(port);
 
            // 輸出"伺服器已啟動"
            System.out.println("Server is start.");
 
            // 接受來自客戶端的連線
            Socket socket=serverSocket.accept();
 
            // 初始化輸出網路串流
            BufferedWriter bw=    new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
 
            // 傳送訊息到客戶端
            bw.write("Hello! This is sever msg.\n");
 
            // 立即送出
            bw.flush();
        } catch (IOException e) {
            // 如果失敗則顯示"Socket Error"
            System.out.println("Socket ERROR");
        }
 
        // 顯示結束連線
        System.out.println("Socket is End");
    }

}
