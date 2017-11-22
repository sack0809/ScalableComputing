import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import servidor.Log;
import servidor.Sala;
import servidor.Servidor;



	public class ClientThread implements Runnable{
		
		// the socket where to listen/talk
		Socket socket;
		InputStream is;
	    InputStreamReader isr;
		BufferedReader br;
		OutputStream os;
		OutputStreamWriter osw;
		BufferedWriter bw;
		
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		private String username;
		// the only type of message a will receive
		String cm;
		// the date I connect
		String date;
		private testChatRoom testChatRoom;
		private boolean connected;
		private SimpleDateFormat sdf;

		public ClientThread(String username) {
	        this.username = username;
	    }
		
		public ClientThread(Socket s, testChatRoom testChatRoom) throws IOException {
	        this.testChatRoom = testChatRoom;
	        socket=s;
	       // this.username=;	
	        //this.loginTime = System.currentTimeMillis();
	        //this.IP = s.getInetAddress().getHostAddress();
	        //this.ping = 0;
	       // this.superUser = false;
	        //this.heartBeatOn = true;
	        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        bw = new BufferedWriter(new PrintWriter(s.getOutputStream()));
	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		        // Wait to receive the client login message
		        String login = recieveMsg ();
		        System.out.println(login);
		        // Check that the login message is correct
		        if (! login.startsWith ("USER")) {
		            // Disconnect the client if there is an error
		        writeMsg("400 Invalid Package received");
		            
		            connected = false;
		        } else {
		            // Check that the size of the nick is not too long
		            if (login.split (":") [1] .length () >= 12) {
		                // Send an error and disconnect the user
		            	writeMsg ("400 The username chosen is too long, enter a username of a maximum of 12 characters");
		                
		            } else {
		                // Connect to the client if the user does not exist in the room
		                connected =! testChatRoom.existsUser(this);
		            }
		        }
		        // If everything is correct, it connects to the room
		      if (connected) {
		            // We get the nick received from the client
		            connected =true;
		            username = login.split (":") [1];
		            System.out.println(login);
		            // We connect the user to the room
		            writeMsg (testChatRoom.enter(this));
		            // We send the list of users of the room
		            sendUserList ();
		            
		            // We send the name of the room
		            writeMsg ("ROOM" + testChatRoom.getName ());
		            
		            // Loop that will last until the user disconnects (EXIT or errors)
		            do {
		                // We are waiting to receive a message from the client
		                String packet = recieveMsg ();

		                // If the package is not empty, we analyze it
		                if (packet != null &&! packet.isEmpty ()) {
		                  	analyzeMessage (packet);
		                }
		                
		            } while (connected);
		            
		            // We send the disconnection message to the user
		            writeMsg ("400 You have been disconnected from the chat");
		            
		            // The client is no longer connected, we take him out of the room
		             //testChatRoom.exit (this);
		      }
		    }

			
		
		
		
		// Constructor
		/*ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			 Creating both Data Stream 
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				
				 is = socket.getInputStream();
                 isr = new InputStreamReader(is);
                  br = new BufferedReader(isr); 
                  os = socket.getOutputStream();
                  osw = new OutputStreamWriter(os);
                   bw = new BufferedWriter(osw);
				
				username = br.readLine();
				display(username + " just connected.");
				
				
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				String msg=recieveMsg();
				
				if (msg != null && !msg.isEmpty()) {
                      
                    analyzeMessage(msg);
                }
				System.out.println(msg);
				try {
					cm = br.readLine();
					
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				
				// the messaage part of the ChatMessage
				//String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
					
				case ChatMessage.LISTROOMS:
					String str ;
					 HashMap<Integer, String> hmap = new HashMap<Integer, String>();
					  hmap.put(1, "Room1");
					  hmap.put(2, "Room2");
			           writeMsg("List of Rooms");
			         Iterator<String> it=hmap.values().iterator();
			              while(it.hasNext()) {
						    //System.out.println(it.next() + " ");
			            	   str=it.next();
			            	   writeMsg(str);
			            }
			             
				      break ;
				      
				//case ChatMessage.HELLO:
					  
					
					try {
				         String host = "localhost";
				         InetAddress ip = InetAddress.getByName(host);
				         IP=  ip.getHostAddress();
				         //System.out.println("Hostname : " + ip.getHostName());
				      } catch(UnknownHostException uhe) {
				         System.out.println("Host Not Found");
				      } catch(Exception e) {
				         System.out.println(e);
				      }
					// Address(InetAddress.getLocalHost());
					 
					// String host = "localized";
			        // InetAddress ip = InetAddress.getByName(host);
			        // System.out.println("IP Address : " + ip.getHostAddress());
					// writeMsg("HELLO"+"   " +"IP: " + IP +" "+ "PortNumber: " + port +"   "+ "Student Id = 17311921"  );
					// break;
					 //System.out.println("Student Id = 17311921");
					  
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		

		private void analyzeMessage(String msg) {
			 
			if  (msg.equalsIgnoreCase ("LISTROOMS")) {// Request for a list of available rooms
	                // We obtain an array of strings with the names of the rooms
	                testChatRoom [] sl = serverHandler.getRoom();
	                // We send the information of the rooms to the user who makes the request
	                writeMsg ("=========================");
	               // bw.flush();
	                writeMsg("Rooms available:" + sl.length);
	                //bw.flush();
	                writeMsg ("=========================");
	                //bw.flush();
	                for (testChatRoom sl1: sl) {
	                	 writeMsg (sl1.getName () + "- Users:" + sl1.getCountUsers () +"");
	                   // bw.flush();
	                }
	               // bw.write ("=========================");
	                //bw.flush();
			} 
			else if (msg.startsWith("J")) { 
	            String[] p=  msg.split(":");
	            System.out.println(p.length);
	            for(String w:p){  
	            	System.out.println(w); }
	            	if (p.length> 3) {
	                    bw.write ("500 Incorrect Syntax");
	                    //System.out.println("Invalid package:" + s);
	                } else {
	                	if (serverHandler.existRoom (new testChatRoom(p [1])))
	                	   {
	                		if (p.length == 2) {
	                   // We get the room from the name
	                   testChatRoom sl = testServer.getRooms(p [1]);
	                   System.out.println(testServer.getRooms(p[1]));
	                   testClient clienttest=new testClient("Saquib", socket, new testChatRoom("Main"));
	                   //Exit from current and Enter
	                   sl.enter(this);
	                		}
	                	   }
	               }
			
			
		}

		public String recieveMsg() {
			// if Client is still connected send the message to it
			
			String msg="";
			if(!socket.isConnected()) {
				close();
				
			}
			// write the message to the stream
			
				try {
					msg= br.readLine();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
			// if an error occurs, do not abort just inform the user
			   return msg;
			
		}
		

		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(osw != null) osw.close();
			}
			catch(Exception e) {}
			try {
				if(isr != null) isr.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		
		 * Write a String to the Client output stream
		 
		public void writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				
			}
			// write the message to the stream
			try {
				bw.write(msg + "\n");
				bw.flush();
			}
			
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			
		}
		
		public boolean writemsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				bw.write(msg + "\n");
				bw.flush();
			}
			
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}*/
		
		private void analyzeMessage(String msg) {
			 
			if  (msg.equalsIgnoreCase ("LISTROOMS")) {// Request for a list of available rooms
	                // We obtain an array of strings with the names of the rooms
				    Server server=new Server();
	                testChatRoom [] sl = server.getRoom();
	                // We send the information of the rooms to the user who makes the request
	                writeMsg ("=========================");
	               // bw.flush();
	                writeMsg("Rooms available:" + sl.length);
	                //bw.flush();
	                writeMsg ("=========================");
	                //bw.flush();
	                for (testChatRoom sl1: sl) {
	                	 writeMsg (sl1.getName () + "- Users:" + sl1.getCountUsers () +"");
	                   // bw.flush();
	                }
	               //bw.write ("=========================");
	                //bw.flush();
			} else if (msg.startsWith("J")) { //Petición de entrada a una sala existente
	            String[] p;
	            p = msg.split(":");
	            
	            if (p.length > 3) {
	            	writeMsg("500 Sintaxis incorrecta");
	                
	            } else {
	                //Comprobamos que la sala existe
	                if (Server.existRoom(new testChatRoom(p[1]))) {
	                  
	                    
	               // 	Received 1 parameter (room name)
	                        if (p.length == 2) {
	                            //Obtenemos la sala a partir del nombre
	                            testChatRoom sl = Server.getRooms(p[1]);
	                            //Si tiene contraseña, indicamos que no puede entrar sin especificarla
	                            //Si no tiene contraseña, entramos a la sala
	                                //Sacamos al usuario de la sala actual
	                                testChatRoom.exit(this);
	                                //Lo metemos en la nueva sala
	                                sl.enter(this);
	                                //Cambiamos la sala en el usuario
	                                testChatRoom = sl;
	                                //Enviamos el nombre de la sala
	                                writeMsg("SALA " + testChatRoom.getName());
	                                //Actualizamos el listado de usuarios para todos los usuarios de la sala
	                                testChatRoom.updateListedUsers();
	                           
	                        } 
	                     
	                } else { //No existe la sala
	                	writeMsg("500 There is no room called " + p[1]);
	                }
	            }
	        }else if (msg.startsWith ("Create")) {// Request to create a new room
	                   String [] p;
	                   p = msg.split (":");
	                   
	                   if (p.length> 3) {
	                	   writeMsg ("500 Incorrect Syntax");
	                       
	                   } else {
	                       // We check that the room does not exist
	                       if (! Server.existRoom (new testChatRoom (p [1]))) {
	                           testChatRoom sl = null;
	                           // Room without password (1 parameter)
	                           if (p.length == 2) {
	                               // We created the room
	                               sl = new testChatRoom (p [1]);
	                           }/* else if (p.length == 3) {// Room with password (2 parameters)
	                               // We created the room
	                               sl = new testChatRoom (p [1], p [2]);
	                           }*/
	                           if (sl!= null) {
	                               // We add the room to the list of rooms
	                               Server.addRoom (sl);
	                               // We take the user out of the current room
	                               testChatRoom.exit (this);
	                               // We put it in the new room
	                               sl.enter (this);
	                               // We change the room in the user
	                               testChatRoom = sl;
	                               // We send the name of the new room to the user
	                               writeMsg ("ROOM" + sl.getName ());
	                               // We update the list of users for all users of the room
	                               testChatRoom.updateListedUsers();
	                           }
	                       } else {
	                    	   writeMsg ("500 already exists a room with that name");
	                       }
	                   }
	               }else if (msg.startsWith("L")) { //Petición de entrada a una sala existente
	   	            String[] p;
		            p = msg.split(":");
		            
		            if (p.length > 3) {
		            	writeMsg("500 Sintaxis incorrecta");
		                
		            } else {
		                //Comprobamos que la sala existe
		                if (Server.existRoom(new testChatRoom(p[1]))) {
		                  
		                    
		               // 	Received 1 parameter (room name)
		                        if (p.length == 2) {
		                            //Obtenemos la sala a partir del nombre
		                            testChatRoom sl = Server.getRooms(p[1]);
		                            //Si tiene contraseña, indicamos que no puede entrar sin especificarla
		                            //Si no tiene contraseña, entramos a la sala
		                                //Sacamos al usuario de la sala actual
		                                testChatRoom.exit(this);
		                                //Lo metemos en la nueva sala
		                               // sl.enter(this);
		                                //Cambiamos la sala en el usuario
		                               // testChatRoom = sl;
		                                //Enviamos el nombre de la sala
		                                //("SALA " + testChatRoom.getName());
		                                //Actualizamos el listado de usuarios para todos los usuarios de la sala
		                                testChatRoom.updateListedUsers();
		                           
		                        } 
		                     
		                } else { //No existe la sala
		                	writeMsg("500 There is no room called " + p[1]);
		                }
		            }
	               }

	            }
 	
		
		

		public void writeMsg(String msg) {
			// if Client is still connected send the message to it
			//if(!socket.isConnected()) {
				//close();
				
			//}
			// write the message to the stream
			try {
				bw.write(msg + "\n");
				bw.flush();
			}
			
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			
		}
		private void display(String msg) {
			String time = sdf.format(new Date()) + " " + msg;
			System.out.println(time);
			
			}
			
		public String recieveMsg() {
			// if Client is still connected send the message to it
			
			String msg="";
			//if(!socket.isConnected()) {
				//close();
				
			//}
			// write the message to the stream
			
				try {
					msg= br.readLine();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
			// if an error occurs, do not abort just inform the user
			   return msg;
			
		}
		private void close() {
			// try to close the connection
			try {
				if(osw != null) osw.close();
			}
			catch(Exception e) {}
			try {
				if(isr != null) isr.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
		 	
		public String getUsername() {
	        return username;
	    }
	
	 public boolean isConnected () {
	        return connected;
	    }

	public void setConnected ( boolean connected) {
	        this.connected = connected;
	    }

	public void sendMessage(String s) {
		try {
			bw.write(s + "\n");
			bw.flush();
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
		
	}

	public void sendUserList () {
        StringBuilder strb = new StringBuilder ();
        strb.append ("LIST:");
        for (ClientThread usr:testChatRoom.getUsers()) {
            strb.append (usr.getUsername ());
            strb.append ("");
        }
        sendMessage (strb.toString ());
    }

	
	
	  
}
