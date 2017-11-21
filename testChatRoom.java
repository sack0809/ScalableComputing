import java.util.ArrayList;

public class testChatRoom {

	private  String name;
	private static ArrayList <ClientThread> users;
	
	 
	   public testChatRoom (String name) {
		   this.name = name;
		   testChatRoom.users = new ArrayList <ClientThread> ();
		   //this.password = "";
		   //this.users = new ArrayList <> ();
		// this.banned = new ArrayList <> ();
		   }
	
	
	  public String getName () {
              return this.name;
          }
	  public void setName (String name) {
          this.name = name;
      }
	  
	  public  String enter(ClientThread u) {
		 if (!existsUser (u)) {
			 System.out.println(u);
			  u.setConnected (true);
			  
			 users.add(u);
			 broadcast (u.getUsername () + "to entered the room" + this.getName());
			 System.out.println("I am in Main Room");
			  updateListedUsers ();
			  u.sendMessage ("RoomName"	);
		  } else {
	             // We disconnect the user
	             u.setConnected (false);
	             return "400 The user is already in the room";
		  }
		  return "ok";
		  
	  }
	  
	  public void exit (ClientThread u) {
		// If the user exists we leave the room 
		if (existsUser(u)) {// We remove it from the user list of the
		 users.remove (u);
		// We spread the outgoing message to all members of the room broadcast 
		 broadcast(u.getUsername() + "to come out of the room" + this.name); 
		// We send the updated list of users to all the users of the room 
		 updateListedUsers (); 
		
		}}

	  
	  public  boolean existsUser ( ClientThread u) {
          for (ClientThread usr: users) {
              if (usr!=null && usr.getUsername() != null && usr.getUsername (). equalsIgnoreCase (u.getUsername ())) {
                  return true;
              }
          }
          return false;
      }
	  
	/*  public static void updateListedUsers () {
          for (ServerHandler usr: users) {
        	      System.out.println(usr.username);
              usr.sendUserList ();
          }
      }
	  public static void broadcast (String message) {
          for (ServerHandler usr: users) {
              usr.sendMessage (message);
          }
      }*/
	  public static	 ArrayList<ClientThread> getUsers () {
          return users;
      }
	  
	  public int getCountUsers() {
	        return users.size();
	    }
	  
	  public  void updateListedUsers () {
          
          for (ClientThread usr: users) {
                   usr.sendUserList();
 }
}
	  public void broadcast (String message) {
	         for (ClientThread usr: users) {
	             usr.sendMessage (message);
	         }
	     }

	  
}