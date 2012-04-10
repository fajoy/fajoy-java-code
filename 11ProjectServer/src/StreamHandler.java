import java.io.*;
import java.net.Socket;
import java.util.*;


public class StreamHandler implements Runnable{
	protected BufferedReader reader=null;
	protected BufferedWriter writer=null;
	private Thread readThread = null;
	private List<ReadLineHandler<StreamHandler>> eventReadLine = new ArrayList<ReadLineHandler<StreamHandler>>();
	protected boolean _isReading = false;
	public int readLength=0;
	protected StreamHandler () {}
	public StreamHandler(Socket sock) throws IOException {
		setReader(sock.getInputStream());
		setWriter(sock.getOutputStream());
	}
	public StreamHandler(InputStream is,OutputStream os) throws IOException {
		setReader(is);
		setWriter(os);
	}
	public StreamHandler(InputStream is) throws IOException {
		setReader(is);
	}
	public StreamHandler(OutputStream os) throws IOException {
		setWriter(os);
	}
	private void setReader(InputStream is) throws IOException {
		reader = new BufferedReader(new InputStreamReader(is));
	}
	private void setWriter(OutputStream os) throws IOException {
		writer = new BufferedWriter(new OutputStreamWriter(os));
	}
	public void beginAsyncReadline() {
		if (readThread == null) {
			_isReading = true;
			readThread = new Thread(this);
			readThread.start();
		}
	}

	public void write(String arg0) {
		try {
			writer.write(arg0);
		} catch (IOException e) {
			writeError(e);
		}
	}
	public void writeLine(String arg0) {
			write(String.format("%s\n",arg0));
	}
	public void endAsyncReadline() {
		_isReading = false;
	}

	protected void setReadLineHander(ReadLineHandler<StreamHandler> handler) {
		eventReadLine.add(handler);
	}
	protected void clearReadLineHander() {
		eventReadLine.clear();
	}
	private void readLineHandle() {
		String line;
		while (_isReading) {
			line = this.readLine();
			if(line==null)
				break;
			for (Object obj: eventReadLine.toArray()) {
				ReadLineHandler<StreamHandler> ent=(ReadLineHandler<StreamHandler>)obj;
				ent.action(this, line);
			}
		}
		_isReading=false;
	}

	public String readLine() {
		String line = null;
		try {
			line = reader.readLine();
			readLength+=line.length()+1;
		} catch (IOException e) {
			_isReading = false;
			throwsHandler(e);
		} catch (java.lang.NullPointerException e) {
			_isReading = false;
		}
		return line;
	}

	private void throwsHandler(IOException e) {
		readLineError(e);
		// throw e;
		// e.printStackTrace();
	}
	protected void readLineError(IOException e) {
		System.out.println("ReadLine Error");
		// throw e;
		// e.printStackTrace();
	}
	protected void writeError(IOException e) {
		System.out.println("Write Error");
		// throw e;
		// e.printStackTrace();
	}
	
	
	public void run() {
			readLineHandle();
			readThread=null;
	}
	
	public void close(){
		try {
			if(writer!=null)
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(reader!=null)
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void flush(){
		try {
			if(writer!=null)
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
