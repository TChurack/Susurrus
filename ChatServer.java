import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * A simple chat server with a CLI, capable of receiving messages from a single ChatClient instance
 */
public class ChatServer {
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private MessagePrinter printer;
	
	private LinkedList<Listener> clients = new LinkedList<>();
	private int nextAnonID = 1;
	
	final static int DEFAULT_PORT = 3636; //Port used if no port number supplied
	final static String ADMIN = "Boss";
	
	private boolean killServer = false;
	
	//Chain constructors
	public ChatServer() {
		this(DEFAULT_PORT);
	}
	
	public ChatServer(int portNumber) {
		printer = new MessagePrinter(this);
		new Thread(printer).start();
		
		try {
			System.out.println("Attempting to create a Chat Server on port " + portNumber + "...");
			
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server now running: " + serverSocket);
			
	
			System.out.println("Waiting for clients...");	
			
			while (!killServer) {

				clientSocket = serverSocket.accept();
				
				Listener listen = new Listener(this, clientSocket);
				new Thread(listen).start();
				
				clients.add(listen);
			}
			
			terminateServer(ADMIN);
			
		} catch (IOException e) {
			System.out.println("Server encountered IOException: " + e);
		}
	}
	
	public boolean authorisedToKill(String username) {
		return username.equals(ADMIN);
	}
	
	public void removeClient(Listener client) {
		printer.addMessage(client.getName() + " has left!");
		clients.remove(client);
	}
	
	public void addMessage(String message, String username) {
		printer.addMessage(username + " (" + (new SimpleDateFormat("HH:mm").format(new Date())) + "): " + message);
	}
	
	public void welcome(String username) {
		printer.addMessage("Welcome " + username +"!");
	}
	
	public LinkedList<Listener> getSubs() {
		return clients;
	}
	
	public boolean terminateServer(String username) {
		boolean allowed = authorisedToKill(username);
		if (allowed) {
			killServer = true;
			while (!clients.isEmpty()){
				try {
					clients.poll().close();
				} catch (IOException e) {
					System.out.println("Error closing connection to client: " + e.getMessage());
				}
			}
			printer.close();
		}
		return allowed;
	}
	
	public int getAnonID() {
		return nextAnonID++;
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