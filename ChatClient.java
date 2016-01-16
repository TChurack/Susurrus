import java.net.*;
import java.io.*;

/**
 * A simple chat client capable of connecting to a ChatServer and sending messages.
 */
public class ChatClient {
	private Socket socket;
	private Console consoleIn = System.console();
	private DataOutputStream output;
	private ClientListener listener;
	
	public ChatClient(String serverName, int serverPort) {
		System.out.println("Connecting to Server...");
		try {
			socket = new Socket(serverName, serverPort);
			
			System.out.println("Successfully connected!");
			start();
		} catch (UnknownHostException e) {
			System.out.println("Could not find host: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Encountered IOException: " + e.getMessage());
		}
		
		listener= new ClientListener(this, socket);
		new Thread(listener).start();
		
		System.out.println("Please enter a username:");
		String username = consoleIn.readLine();
		
		try {
			output.writeUTF(username);
			output.flush();
		} catch (IOException e) {
			System.out.println("Error while sending message: " + e.getMessage());
		}
		
		String message = "";
		while(!message.equalsIgnoreCase("exit")) {
			try {
				message = consoleIn.readLine();
				output.writeUTF(message);
				output.flush();
			} catch (IOException e) {
				System.out.println("Error while sending message: " + e.getMessage());
			}
		}
	}
	
	public void addMessage(String message) {
		System.out.println(message);
	}
	
	//Starts input/output
	public void start() throws IOException {
		consoleIn = System.console();
		output = new DataOutputStream(socket.getOutputStream());
	}
	
	//Severs the connection to server
	public void stop() {
		try {
			if (output != null) { output.close(); }
			if (socket != null) { socket.close(); }
			if (listener != null) { listener.close(); }
		} catch (IOException e) {
			System.out.println("Error severing connection to server!.."); //Synophonic pun!
		}
	}
	
	public static void main(String args[]) {
		ChatClient client;
		if (args.length != 2) {
			System.out.println("Missing argument! Please pass the name of the host and the port number");
		} else {
			client = new ChatClient(args[0], Integer.parseInt(args[1]));
		}
	}
}