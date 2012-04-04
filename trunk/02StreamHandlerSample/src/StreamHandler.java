import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreamHandler {
	private BufferedReader reader;
	private BufferedWriter writer;
	private List<ReadLineHandler<StreamHandler>> eventReadLine = new ArrayList<ReadLineHandler<StreamHandler>>();
	private boolean _isReading = false;
	private Thread readThread = null;

	public StreamHandler(Socket sock) throws IOException {
		reader = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(
				sock.getOutputStream()));
	}

	public void beginAsyncReadline() {
		if(readThread==null){
		_isReading = true;
		readThread=new Thread(readRunnable);
		readThread.start();
		}
	}

	public void write(String arg0)  {
		try {
			writer.write(arg0);
		}catch (IOException e) {
			throwsHandler(e);
		}
	}
	public void endAsyncReadline() {
		_isReading = false;
	}
	protected void setReadLineHander(ReadLineHandler<StreamHandler> handler){
		eventReadLine.add(handler);
	}
	protected void readLineHandle()   {
		String line;
		try {
			while (_isReading) {
				line = reader.readLine();
				for (Iterator<ReadLineHandler<StreamHandler>> iterator = eventReadLine.iterator(); iterator.hasNext();) {
					ReadLineHandler<StreamHandler> ent = iterator.next();
					ent.action(this, line);
				}
			}
		} catch (IOException e) {
			throwsHandler(e);
		}
	}
	protected void throwsHandler(IOException e){
		_isReading=false;
		//e.printStackTrace();
	}
	private Runnable readRunnable = new Runnable() {
		@Override
		public void run() {
				readLineHandle();
		}
	};

}
