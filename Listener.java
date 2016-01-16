import java.net.*;
import java.io.*;

public class Listener implements Runnable {
	private ChatServer server;
	private DataInputStream input;
	private DataOutputStream output;
	private Socket clientSocket;
	private boolean serverKilled = false;
	private String username;
	boolean finished = false;
	
	public Listener(ChatServer cServer, Socket socket) {
		server = cServer;
		clientSocket = socket;
	}
	
	public void run() {
	
		try {
			input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			username = input.readUTF();
					
			if (username.length() == 0) { username = "Anonymous" + server.getAnonID(); }
			
			server.welcome(username);
			
		while (!finished) {
			
			try {
				String message = input.readUTF();
				
				if (message.equals("killServer")) { serverKilled = server.terminateServer(username); }
				finished = (message.equals("exit") || serverKilled); 
				
				if(!finished) { server.addMessage(message, username); }
				
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
	
	public void notify(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException e) {
			//Do nothing
		}
	}
	
	public String getName() {
		return username;
	}
	
	//Closes the connection to the client
	public void close() throws IOException {
		finished = true;
		if (input != null) { input.close(); }
		if (output != null) { output.close(); }
		if (clientSocket != null) { clientSocket.close(); }
		server.removeClient(this);
	}
}