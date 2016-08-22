package com.peertopeer.peer;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.Serializable;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import java.util.Map.Entry;

import com.peertopeer.peer.Coordinates;
import com.peertopeer.peer.MazeStatistics;
import com.peertopeer.peer.Peer_Interface;

public class Peer_Implementation extends UnicastRemoteObject implements Serializable,Peer_Interface {
	
	static int N=5, M =10;
	static MazeStatistics stats = null;
	private static final long serialVersionUID = 1L;
	private boolean checkPrimary;
	private boolean checkBackup;
	private boolean firstPeer;
	
	public Peer_Interface stub;
	public int player_Id;
	
	public static HashMap<Integer,Peer_Interface> playerMap=new HashMap<Integer,Peer_Interface>();
	public static long tStart,tEnd;
	
	public void stayConnected(){
		try{
		while(true);
		}
		catch(Exception e)
		{
			System.err.println("Player crashed");
		}
	}
	
	public long sayHello() {
		
		if(tEnd >= System.currentTimeMillis()){ 

		return tEnd;
		}
		return tEnd;
	}
	
	public Coordinates createPlayerMap (int player_id, Peer_Interface obj){
		Coordinates coords;
		coords = stats.createDistinctCoordinates(player_id,obj);
		return coords;
	}
	
	public Peer_Implementation(int id) throws RemoteException{
		this.player_Id = id;
	}
	
	public boolean timerToStart(long tEnd){
		 if(System.currentTimeMillis() > tEnd){
			
		    	return false;
		    }
		 else{
			
			    while(tEnd >= System.currentTimeMillis())
			    {		    	
			    	
				    if(tEnd == System.currentTimeMillis()) {
				    	return true;
				    }
				}
		   }
		return true; 
	}
	
	synchronized public void createBackupServer(){
		HashMap<Integer, Peer_Interface> pMap = stats.getPlayerMap();
		
		stats.setBackupServer(pMap.get(this.player_Id + 1));
		stats.setBackupServerId(this.player_Id+1);
		
		System.out.println("The backup server is "+ ":"+stats.getBackupServerId());
		
		System.out.print("Current Players List"+" ");
		for (Entry<Integer, Peer_Interface> entry : stats.getPlayerMap().entrySet()){
			Integer playerId=entry.getKey();
			System.out.print(playerId+" ");			
		}
	}
	
	public void start(){
		MazeStatistics stats = this.stats;
		HashMap<Integer,Peer_Interface> playerMap = this.stats.getPlayerMap();
		
		for( Entry<Integer, Peer_Interface> entry : playerMap.entrySet()){
			try {
				 Peer_Interface p = entry.getValue();
				 p.notifyPeers(entry.getKey(),stats);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void notifyPeers(int playerId, MazeStatistics stats){
			this.setStats(stats);
	
				if (stats.getPrimaryServerId()== playerId) {
					this.checkPrimary = true;
					this.checkBackup = false;
				} else if (stats.getBackupServerId() == playerId) {
					this.checkPrimary =false;
					this.checkBackup = true;
				} else {
					this.checkPrimary =false;
					this.checkBackup = false;
				}
				
		try {
			this.buildMaze();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePrimaryAndBackupServer(int primaryId, int backUpId,Peer_Interface pri, Peer_Interface Bac){
		this.stats.setBackupServer(Bac);
		this.stats.setBackupServerId(backUpId);
		this.stats.setPrimaryServer(pri);
		this.stats.setPrimaryServerId(primaryId);
	}
	
	public void makeAMove() throws RemoteException {
		
			while (stats.totalTresure != 0) {
				System.out.println(" ");
				System.out.println("Total Treasure Left :"+stats.totalTresure);
				InputStreamReader is =  new InputStreamReader(System.in);
			    	BufferedReader br = new BufferedReader(is);
				String direction = null;
				System.out.println("Enter Direction in which you want to move (N - North, S - South, E - East, W - West or NM - NoMove):");
		    	try {
		    		
					direction = (br.readLine()).trim();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	if(direction.equals("N") || direction.equals("S") || direction.equals("E") || direction.equals("W") || direction.equals("NM") )
		    	{	
		    		try{
		    			this.stats=this.stats.getPrimaryServer().checkMove(this.player_Id,direction);
		    		}
		    		catch(Exception e){
		    			System.err.println("Primary Server Crashed");
		    			try{
		    				System.out.println("Using Backup Server:");
		    				this.stats = this.stats.getBackupServer().checkMove(this.player_Id, direction);
		    			}
		    			catch(Exception re){
		    				//System.err.println("Both server crashed");
		    				//System.err.println("                                        GAME ENDS!!!!!");
		    				//re.printStackTrace();
		    				//System.exit(1);
		    				
		    				stats.getPlayerMap().remove(stats.getPrimaryServerId());
		    				stats.getPlayer_position().remove(stats.getPrimaryServer());
		    				
		    				stats.getPlayerMap().remove(stats.getBackupServerId());
		    				stats.getPlayer_position().remove(stats.getBackupServer());
		    				System.out.println("inside double crash :"+this.stats.getPlayerMap().size());
		    				int primaryId = this.player_Id;
    					    Peer_Interface pri = this.stats.getPlayerMap().get(primaryId);
    					    int backUpId ;
    					    Peer_Interface Bac = null;
    					    this.checkPrimary = true;
    					    this.checkMove(this.player_Id, direction);
    					    if(this.stats.getPlayerMap().size()>1){
    					    backUpId = primaryId +1;
    					    Bac = this.stats.getPlayerMap().get(backUpId);
    					    Bac.setCheckBackup(true);
    					    
    					    
		    				System.out.println("Updating new primary and backup");
		    				for(Entry<Integer,Peer_Interface> playerAlive : this.stats.getPlayerMap().entrySet()){
		    					Peer_Interface pAlive = playerAlive.getValue();
		    					    
		    						try {
		    							
		    							pAlive.updatePrimaryAndBackupServer(primaryId, backUpId, pri, Bac);
		    						} catch (RemoteException e1) {
		    							// TODO Auto-generated catch block
		    							//e1.printStackTrace();
		    						}
		    					}
		    				
    					    }
		    				
		    			}
		    		}
		    	}
				try {
					buildMaze();
				} catch (RemoteException e) {
					
					e.printStackTrace();
				}
				// Make the current player as BackUp Server
				if (this.stats.getBackupServerId() == this.player_Id && !this.checkBackup) {
					this.notifyPeers(player_Id, this.stats);
				}
				
			}
			
		}
	
	
	public boolean isAlive()
	{
		return true;
	}
	
	public synchronized MazeStatistics checkMove(int player_Id, String direction) {
			System.out.println(this.stats.getPlayerMap().size());
			if(this.stats.getPlayerMap().size()<2){
				System.err.println("                                        GAME ENDS!!!!!");
				System.exit(0);
			}
			
		if (checkPrimary) {
			System.err.println("Player "+player_Id+" requested to move in "+direction+ " direction.");

			stats = this.move(player_Id, direction);

			try {
				System.err.println("Transmitting the player: " + player_Id + "'s"+" 'move' request to Backup Server");
				stats.getBackupServer().move(player_Id, direction);
				long breakTime = System.currentTimeMillis();
		    	while(System.currentTimeMillis() < breakTime + 100){
		    	
		    	}
			} catch (RemoteException e) {
				System.err.println("Backup Server Crash: While Updating");
				boolean isPlayerPrimary = false;
				this.stats.getPlayerMap().remove(stats.getBackupServerId());           // removing backup server from player list
				this.stats.getPlayer_position().remove(stats.getBackupServerId());
				if(stats.getPrimaryServerId() == player_Id)
				{
					isPlayerPrimary = true;
				}
				boolean isPlayerBackup = false;
				if(stats.getBackupServerId() == player_Id){
					isPlayerBackup = true;
				}
				if(!isPlayerPrimary && !isPlayerBackup){
					System.err.println("Choosing player: " + player_Id + " to be our new Backup Server");
					this.stats.getPlayerMap().remove(stats.getBackupServerId());           // removing backup server from player list
					this.stats.getPlayer_position().remove(stats.getBackupServerId());
					this.stats.setBackupServerId(player_Id);
					this.stats.setBackupServer(this.stats.getPlayerMap().get(player_Id));
					
					
					
					
					
					
					
					
				} else if(isPlayerBackup){
					System.err.println("Backup server(current player) crashed. Move has been ignored");
				} else {
					System.err.println("Current player :(" + player_Id + ") is the Primary Server now. This move is allowed without the Backup Server.");
				}
			}
		} else if (checkBackup) {
			System.err.println("Primary Server has crashed. Handling the primary server crash.");

			boolean isPlayerBackup = false;
			if(stats.getBackupServerId() == player_Id){
				isPlayerBackup = true;
			}
			if(isPlayerBackup){
				System.err.println("Current player (" + player_Id + ") is the Backup Server. This move is allowed without the Primary Server");
				stats.getPlayerMap().remove(stats.getPrimaryServerId());
				stats.getPlayer_position().remove(stats.getPrimaryServer());
				
				if(stats.getPlayerMap().size() >1){
				stats.setPrimaryServerId(stats.getBackupServerId()+1);
				stats.setPrimaryServer(stats.getPlayerMap().get(stats.getBackupServerId()+1));
				}
				
				stats = this.move(player_Id, direction);
			}
			//Current Status -> Current Server = BackUp Server
			else if (checkBackup) {
				System.err.println("Promoting the backup server "+ this.player_Id + " as Primary Server");
				this.stats.getPlayerMap().remove(stats.getPrimaryServerId());      // Removing primary server from player list
				this.stats.getPlayer_position().remove(stats.getPrimaryServerId());
				
				
				stats.setPrimaryServerId(this.player_Id);
				stats.setPrimaryServer(stats.getPlayerMap().get(this.player_Id));
				checkPrimary = true;
				
				System.err.println("Promoting the player " + player_Id + " in the game as the 'new' BackUp Server");
				this.stats.setBackupServerId(player_Id);
				this.stats.setBackupServer(this.stats.getPlayerMap().get(player_Id));
				checkBackup = false;

				System.err.println("Moving Player " + player_Id + " in "+direction +" direction");
				
						
				
				stats = this.move(player_Id, direction);
			}
			// BackUp Server has been promoted to Primary server
			else {
				System.err.println("Request is made by Primary Server " + player_Id);
				assert (checkPrimary && !checkBackup);

				stats = move(player_Id, direction);
				
			}
		} else {
			assert (false);
		}

		
		//Checking Alive Players
		
		for(Entry<Integer,Peer_Interface> playerAlive : this.stats.getPlayerMap().entrySet()){
			Peer_Interface pAlive = playerAlive.getValue();
			
			try {
				boolean checkALive = pAlive.isAlive();
			} catch (RemoteException e) {
				//Normal Player Crashed
				System.out.println("Player "+playerAlive.getKey()+ " crashed. Removing it from Player List");
				this.stats.getPlayerMap().remove(playerAlive.getKey());
				this.stats.getPlayer_position().remove(pAlive);
				System.out.println("PLAYER MAP SIZE :"+this.stats.getPlayerMap().size());
				
				
			}
		}
		
		
		
		
		
		
		return stats;
	}
	
	
		synchronized public MazeStatistics move(int player_id, String direction){
			
	    	Coordinates currentCoord = stats.getPlayer_position().get(player_id);
			int curX = currentCoord.getXCoordinate();
			int curY = currentCoord.getYCoordinate();
			boolean shouldItMove = true;
			int intendedX =-1, intendedY = -1;
			
			if (direction.equals("N")){
				intendedX = curX - 1;
				intendedY = curY;
			}
			if (direction.equals("S")){
				intendedX = curX + 1;
				intendedY = curY;
			}
			if (direction.equals("E")){
				intendedX = curX ;
				intendedY = curY+1;
			}
			if (direction.equals("W")){
				intendedX = curX;
				intendedY = curY-1;
			}
			
			if(intendedX >= N || intendedY >=N || intendedX <0 || intendedY < 0)
				shouldItMove = false;
			else{
				
				for (Entry<Integer, Coordinates> entry : this.stats.getPlayer_position().entrySet())
				   {
				       Coordinates coords=entry.getValue();
				       if(intendedX==coords.getXCoordinate() && intendedY==coords.getYCoordinate())
				    	   shouldItMove = false;
				       
				   }
			}
			
			if(shouldItMove == true){
				stats.updateMazeStatistics(player_id,intendedX,intendedY);
				return stats;
			}				
	    	return stats;	    	
	    }	

	public static void main(String args[]) throws NumberFormatException, RemoteException
	{
		try {
		String ipAddress;
		int port;
		Peer_Implementation peerImpl;
		Peer_Interface stub = null;
		Registry registry = null;
		boolean timer_flag= false;
		int playerId;
		Peer_Interface obj = new Peer_Implementation(Integer.parseInt(args[1]));
		if(args.length >= 4){             // Input format is in the form of :  ('H'/'P')  playerId IPAddress port
			
			if(args[0].equals("H")){
				
				obj.setFirstPeer(true);
				ipAddress = args[2];
				port = Integer.parseInt(args[3]);
				Peer_Interface serverobj =obj;
				    stub = (Peer_Interface)serverobj;
				    registry = LocateRegistry.createRegistry(8000);
				    registry.rebind("Peer_Interface", stub);
				    System.out.println("                                ****GAME STARTS****");
				    System.out.println("Primary server activated : Primary server id is :"+Integer.parseInt(args[1]));
				    tStart = System.currentTimeMillis();
		    		tEnd = tStart + 30000;
		    		timer_flag = stub.timerToStart(tEnd); 
				    try{
				    				    	
				    	stub.setStats(new MazeStatistics(N,M,Integer.parseInt(args[1]),serverobj));
					    Coordinates coords=stub.createPlayerMap(Integer.parseInt(args[1]),serverobj);					 
				    	//System.out.println("Successfully joined : Your player id is :"+Integer.parseInt(args[1])+" "+"Current Coordinates:"+"("+coords.getXCoordinate()+","+coords.getYCoordinate()+")");
				    	long breakTime = System.currentTimeMillis();
				    	while(System.currentTimeMillis() < breakTime + 100){
				    	
				    	}
				    	stub.createBackupServer();
				    	//System.out.println("Game Started :"+stub.getStats().gameStarted);
				    	breakTime = System.currentTimeMillis();
				    	stub.start();
				    	
				    }
				    catch(Exception e){
				    	e.printStackTrace();
				    }	    		
			}
						
			if(args[0].equals("P")){
				playerId = Integer.parseInt(args[1]);
				try {
					registry = LocateRegistry.getRegistry(8000);
				
					stub = (Peer_Interface) registry.lookup("Peer_Interface");
				    long responseTime = stub.sayHello();
				    
				 	timer_flag = stub.timerToStart(responseTime);
				 	//System.out.println(timer_flag);
					    if(timer_flag){
					    	
					    	System.out.println("                                ****GAME STARTS****");
					    	Coordinates coords=stub.createPlayerMap(Integer.parseInt(args[1]),obj);
					    	System.out.println("Successfully joined : Your player id is :"+Integer.parseInt(args[1])+" "+"Current Coordinates:"+"("+coords.getYCoordinate()+","+coords.getXCoordinate()+")");
					    	
					    	long breakTime = System.currentTimeMillis();
					    	while(System.currentTimeMillis() < breakTime + 500){
					    	
					    	}
					    	
					    	while(!obj.getStats().gameStarted){
					    		try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
								}
					    	}
					    	//stub.stayConnected();
					    	
					    }
					    else
					    	System.out.println("Time out");						    
				    }		
								
				catch (Exception e) {					
					e.printStackTrace();
				}				
			}
			
				obj.makeAMove();
				System.err.println("                                        GAME ENDS!!!!!");
				System.exit(0);
			}
		}
		catch(Exception e)
		{
			/*e.printStackTrace();*/
			
		}
		}
		
	public void buildMaze() throws RemoteException{
		int present =0;
		System.out.println(" ");
		if(checkBackup){
			System.out.println("This is Backup Server");
		}
		if(checkPrimary){
			System.out.println("This is Primary Server");
		}
			
		System.out.println("Primary Server Id : "+this.stats.getPrimaryServerId());
		System.out.println("Backup Server Id : "+this.stats.getBackupServerId());
		
		 System.out.println("Treasure Map");
		 System.out.println("------------ ");
		 System.out.println(" ");
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				present =0;
				for (Entry<Coordinates,Integer> entry : this.stats.getTreasure_position().entrySet())
				   {
				       Coordinates coords=entry.getKey();
				       //System.out.println("Treasure position :"+coords.getXCoordinate()+","+coords.getYCoordinate());
				       
				       if(i==coords.getXCoordinate() && j==coords.getYCoordinate())
				       {
				    	   System.out.print("$"+entry.getValue());
				    	   present = 1;
				       }
				       
				   }
				
				for (Entry<Integer, Coordinates> entry : this.stats.getPlayer_position().entrySet())
				   {
				       Integer playerId=entry.getKey();
				       Coordinates coords=entry.getValue();
				       //System.out.println("Treasure position :"+coords.getXCoordinate()+","+coords.getYCoordinate());				      
				       
				       if(i==coords.getXCoordinate() && j==coords.getYCoordinate())
				       {   System.out.print("P"+playerId);
				    	   present = 1;
				       }
				   }
				if(present != 1)
				{	
					if(present>=2)
					    System.out.print(".");
					else
						System.out.print("."+"     ");
				}else{
					System.out.print("    ");
				}
			}
			System.out.println();	
		}
		this.displayPlayerScores();
	}
	
	/**** Player's position Map****/

	public void displayPlayerScores(){
		System.out.println(" ");
		System.out.println("SCORE BOARD");
		System.out.println("------------");
		for (Entry<Integer, Integer> entry : this.stats.getPlayerScore().entrySet()){			
			System.out.println("P"+entry.getKey() + ":::::::::" + entry.getValue());
		}
	}

	public MazeStatistics getStats() {
		return this.stats;
	}

	public void setStats(MazeStatistics stats) {
		this.stats = stats;
	}
		
		
	public boolean isCheckPrimary() {
		return checkPrimary;
	}

	public void setCheckPrimary(boolean checkPrimary) {
		this.checkPrimary = checkPrimary;
	}

	public boolean isCheckBackup() {
		return checkBackup;
	}

	public void setCheckBackup(boolean checkBackup) {
		this.checkBackup = checkBackup;
	}

	public boolean isFirstPeer() {
		return firstPeer;
	}

	public void setFirstPeer(boolean firstPeer) {
		this.firstPeer = firstPeer;
	}
		
	}