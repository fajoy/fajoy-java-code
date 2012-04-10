import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreamHandler {
	private BufferedReader reader=null;
	private BufferedWriter writer=null;
	private Thread readThread = null;
	private List<ReadLineHandler<StreamHandler>> eventReadLine = new ArrayList<ReadLineHandler<StreamHandler>>();
	private boolean _isReading = false;

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
			readThread = new Thread(readRunnable);
			readThread.start();
		}
	}

	public void write(String arg0) {
		try {
			writer.write(arg0);
		} catch (IOException e) {
			throwsHandler(e);
		}
	}

	public void endAsyncReadline() {
		_isReading = false;
	}

	protected void setReadLineHander(ReadLineHandler<StreamHandler> handler) {
		eventReadLine.add(handler);
	}

	private void readLineHandle() {
		String line;
		while (_isReading) {
			line = this.readLine();
			if(line==null)
				break;
			for (Iterator<ReadLineHandler<StreamHandler>> iterator = eventReadLine
					.iterator(); iterator.hasNext();) {
				ReadLineHandler<StreamHandler> ent = iterator.next();
				ent.action(this, line);
			}
		}
		_isReading=false;
	}

	public String readLine() {
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throwsHandler(e);
		}
		return line;
	}

	protected void throwsHandler(IOException e) {
		_isReading = false;
		System.out.println("Socket Close");
		// throw e;
		// e.printStackTrace();
	}

	private Runnable readRunnable = new Runnable() {
		@Override
		public void run() {
			readLineHandle();
		}
	};
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
