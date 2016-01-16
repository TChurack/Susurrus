import java.util.*;

public class MessagePrinter implements Runnable {
		private LinkedList<String> messageQueue = new LinkedList<>();	
		private boolean run = true;
		private ChatServer parent;
		
		public MessagePrinter(ChatServer server) {
			parent = server;
		}
		
		public void addMessage(String message) {
			messageQueue.add(message);
		}
		
		public void close() {
			run = false;
		}
		
		public void run() {
			while(run) {
				while (!messageQueue.isEmpty()) {
					String message = messageQueue.poll();
					System.out.println(message);
					for (Listener sub : parent.getSubs()) { sub.notify(message); }
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//Do nothing
				}
			}
		}
	}