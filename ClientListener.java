import java.net.*;
import java.io.*;

public class ClientListener implements Runnable {
	private ChatClient server;
	private DataInputStream input;
	private Socket inSocket;
	
	boolean finished = false;
	
	public ClientListener(ChatClient cServer, Socket socket) {
		server = cServer;
		inSocket = socket;
	}
	
	public void run() {
	
		try {
			input = new DataInputStream(new BufferedInputStream(inSocket.getInputStream()));
			
		while (!finished) {
			
			try {
				String message = input.readUTF();
				
				server.addMessage(message);
				
			} catch (IOException e) {
				System.out.println("IOException while reading in messages: " + e);
				finished = true;
			}
		}
		close();
		} catch (IOException  e) {
			System.out.println("IOException while waiting for messages: " + e);
		}
	}
	
	//Closes the connection
	public void close() throws IOException {
		finished = true;
		if (input != null) { input.close(); }
		if (inSocket != null) { inSocket.close(); }
	}
}