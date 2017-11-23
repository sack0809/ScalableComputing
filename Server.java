import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;



/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	//private ArrayList<ClientThread> al;
	
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private static int port;
	private static String IP;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	private static ServerSocket serverSocket;
	//HashMap <ChatRoomTest, Integer> hmap;
	private String Address;
    public static ArrayList <testChatRoom> ListRooms = new ArrayList<testChatRoom>();
	private int roomRef=0;
    /*public Server(int port ) {
		Server.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
    }*/
	
	

	/*public void start() {
		keepGoing = true;
	   create socket server and wait for connection requests 
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			display("Server has been initialized with port"+port);
			serverHandler=new ServerHandler();
			serverHandler.isOnline();
			serverHandler.createChatRoom();
			display("Server waiting for Clients on port " + port + ".");
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				
           
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
				
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.isr.close();
					tc.osw.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		*/// something went bad
		/*catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}	*/	
   
	
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
		
		}
	
	/*
	 *  to broadcast a message to all Clients
	 */
	/*private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		System.out.print(messageLf);
		
		// display message on console or GUI
		if(sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf);     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writemsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
*/
	/*// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}*/
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) throws IOException {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		/*switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}*/
		// create a server object and start it
		ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("Server has been initialized with port"+portNumber);
		testChatRoom Main=new testChatRoom("Main");
		//testChatRoom Room1=new testChatRoom("Room1");
		  addRoom(Main);
		  
		  //addRoom(Room1);
		  ///System.out.println("Entering WHILE LOOP for making connection");
		  while(true) {
			  
			  //We create a new instance by passing the socket and the main room
			  Socket socket = serverSocket.accept(); 
		//Server server = new Server(portNumber);
		//server.start();
			  //System.out.println("Client Thread Has been Called");
			  new Thread(new ClientThread(socket, Main)).start();
			  
		  }
	}
	
	public static void close()
	{
		try {
			if (null != serverSocket) {
				serverSocket.close();
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
		Thread.currentThread().interrupt();
	}

	public   static void addRoom(testChatRoom s) {
	      if (getRooms (s.getName ()) == null) {
	            ListRooms.add(s);
	      //System.out.println (s.getName ()+" "+ "chatroom  created" );
	      }
	 }      
	         
	 public static testChatRoom getRooms(String name) {
	        for (testChatRoom s: ListRooms) {
	            if (s.getName (). equalsIgnoreCase (name)) {
	                return s;
	            }
	        }
	        return null;
	    }
	 public   testChatRoom [] getRoom() {
	    	testChatRoom [] s = new testChatRoom [ListRooms.size ()];
	    for (int i = 0; i <ListRooms.size (); i ++) {
	    s [i] = ListRooms.get (i);
	   }
	   return s;
	    	}
	 
	 public   static int getRoomRef(testChatRoom s) {
		int roomRef=0;
		 for (int i = 0; i <ListRooms.size (); i ++) {
	            if (ListRooms.get (i) .getName (). equalsIgnoreCase (s.getName ())) {
	               roomRef=ListRooms.indexOf(s) ;
	                
	            }
	        }
		 return roomRef;
	    }
	 
	 public static boolean getRoomName(int i) {
		 
	 if (i<=ListRooms.size()) {
		     
		     return true;
	 }
	return false;
	  }
	 
	 
	 
	 public   static boolean existRoom(testChatRoom s) {
			for (int i = 0; i <ListRooms.size (); i ++) {
	            if (ListRooms.get (i) .getName (). equalsIgnoreCase (s.getName ())) {
	                return true;
	            }
	        }
			return false;
		}
	
}

	
	


