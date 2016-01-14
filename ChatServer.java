import java.net.*;
import java.io.*;

/**
 * A simple chat server with a CLI, capable of receiving messages from a single ChatClient instance
 */
public class ChatServer {
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private DataInputStream input;
	
	final static int DEFAULT_PORT = 3636; //Port used if no port number supplied
	
	//Chain constructors
	public ChatServer() {
		this(DEFAULT_PORT);
	}
	
	public ChatServer(int portNumber) {
		
		try {
			System.out.println("Attempting to create a Chat Server on port " + portNumber + "...");
			
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server now running: " + serverSocket);
			
			System.out.println("Waiting for client...");
			clientSocket = serverSocket.accept();
			System.out.println("Client found! " + clientSocket);
			
			input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			
			boolean finished = false;
			while (!finished) {
				
				try {
					String message = input.readUTF();
					System.out.println(message);
					finished = (message.equalsIgnoreCase("exit")); 
				} catch (IOException e) {
					System.out.println("IOException while waiting for messages: " + e);
					finished = true;
				}
			}
			close();
		} catch (IOException e) {
			System.out.println("Server encountered IOException: " + e);
		}
	}
	
	//Closes the connection to the client
	public void close() throws IOException {
		if (clientSocket != null) { clientSocket.close(); }
		if (input != null) { input.close(); }
	}
	
	public static void main(String args[]) {
		ChatServer server = null;
		if (args.length == 0) {
			System.out.println("No provided port number: using default port...");
			server = new ChatServer();
		} else {
			server = new ChatServer(Integer.parseInt(args[0]));
		}
	}
}