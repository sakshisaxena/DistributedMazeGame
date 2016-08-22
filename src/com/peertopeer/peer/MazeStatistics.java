package com.peertopeer.peer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
/*
 * 
 */
public class MazeStatistics implements Serializable{
	
	private static final long serialVersionUID = 1L;
	HashMap<Integer,Coordinates> player_position = new HashMap<Integer,Coordinates>();
	HashMap<Coordinates,Integer> treasure_position;
	HashMap<Integer,Integer> player_score;
	HashMap<Integer,Peer_Interface> playerMap= new HashMap<Integer,Peer_Interface>();
	
	int primaryServerId, backupServerId;
	Peer_Interface primaryServer, backupServer;
	int N,M;
	int totalTresure;
	
	boolean gameStarted = false;
	
/*
 * 
 */
	public MazeStatistics(int N, int M, int primaryServerId, Peer_Interface pserver){
		this.N = N;
		this.M = M;
		this.totalTresure = M;
		
		this.player_position = new HashMap<Integer,Coordinates>();
		this.playerMap = new HashMap<Integer,Peer_Interface>();
		this.treasure_position = new HashMap<Coordinates,Integer>();
		this.player_score = new HashMap<Integer,Integer>();
		
		
		//this.playerMap=playerMap;
		this.gameStarted = true;
		this.primaryServer = pserver;
		this.primaryServerId = primaryServerId;
		//this.backupServer = playerMap.get(1);
		//this.backupServerId = 1;
		//System.out.println(this.playerMap.size());
		
		buildTreasure(N, M);
		
	}
	
	
	public boolean isGameStarted() {
		return gameStarted;
	}


	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}


	public synchronized Coordinates createDistinctCoordinates(int player_id, Peer_Interface obj){
		Random rn = new Random();
		Coordinates coords;
		int XCoordinate,YCoordinate;
		do{
			XCoordinate=rn.nextInt(N);
			YCoordinate=rn.nextInt(N-1);
			coords=new Coordinates(XCoordinate, YCoordinate);
		} while (player_position.containsValue(coords) || treasure_position.containsKey(coords));
		player_score.put(player_id, 0);
		player_position.put(player_id, coords);
		playerMap.put(player_id, obj);
		
		return coords;
	}
	
	public void buildTreasure(int N,int M){
		int sum = 0 ; 
		int XCoordinate, YCoordinate, treasure_count;
		Random rn = new Random();
		Coordinates coords = null;
		while(sum < M){
		
			XCoordinate=rn.nextInt(N);
			YCoordinate=rn.nextInt(N);
			//System.out.println("I am here!!!!"+XCoordinate+YCoordinate);
			coords = new Coordinates(XCoordinate,YCoordinate);
			
			do{
				treasure_count=rn.nextInt(M);
			}while(treasure_count == 0);
			sum = sum + treasure_count;
			
			if(sum > M)
			{
				treasure_count = M - (sum-treasure_count);
			}
			
			if(!this.treasure_position.containsKey(coords)){
				//System.out.println("inside if");	
				this.treasure_position.put(coords,treasure_count);
			}
				
		}
		//return this.treasure_position;
	}
	
	public void updateMazeStatistics(int player_id, int intendedX, int intendedY) {
		Coordinates coords = new Coordinates(intendedX,intendedY);
		/*int treasureAtPosition = treasure_position.get(coords);
		if(treasureAtPosition != 0)
		{
			if(player_treaure_count.containsKey(player_id))
			{	
				player_treaure_count.remove(player_id);
				player_treaure_count.put(player_id, treasureAtPosition);
			}
			else 
				player_treaure_count.put(player_id, treasureAtPosition);
		}*/
		if(this.treasure_position.containsKey(coords)){
	    	       int newScore = this.treasure_position.get(coords) + this.player_score.get(player_id);
	    	       this.totalTresure = this.totalTresure - this.treasure_position.get(coords);
	    	       this.treasure_position.remove(coords);
		    	   this.player_score.remove(player_id);
		    	   this.player_score.put(player_id, newScore);
		    	   
		    	   
		}   		
		this.player_position.remove(player_id);
		this.player_position.put(player_id, coords);
		
		
		
		
	}

	/*public void createPlayerScores() {
		for (Entry<Integer, Coordinates> entry : this.getPlayer_position().entrySet())
		   {
		       Integer playerId=entry.getKey();
		       Coordinates coords=entry.getValue();
		       if(this.treasure_position.containsKey(coords)){
		    	   player_score.put(playerId, this.treasure_position.get(coords));
		    	   
		       }
		       
		   }*/
	
	public HashMap<Integer, Integer> getPlayer_score() {
		return player_score;
	}


	public void setPlayer_score(HashMap<Integer, Integer> player_score) {
		this.player_score = player_score;
	}


	public int getPrimaryServerId() {
		return primaryServerId;
	}


	public void setPrimaryServerId(int primaryServerId) {
		this.primaryServerId = primaryServerId;
	}


	public int getBackupServerId() {
		return backupServerId;
	}


	public void setBackupServerId(int backupServerId) {
		this.backupServerId = backupServerId;
	}


	public Peer_Interface getPrimaryServer() {
		return primaryServer;
	}


	public void setPrimaryServer(Peer_Interface primaryServer) {
		this.primaryServer = primaryServer;
	}


	public Peer_Interface getBackupServer() {
		return backupServer;
	}


	public void setBackupServer(Peer_Interface backupServer) {
		this.backupServer = backupServer;
	}

	public HashMap<Integer, Peer_Interface> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(HashMap<Integer, Peer_Interface> playerMap) {
		this.playerMap = playerMap;
	}

	public MazeStatistics(int N){
		this.N = N;
	}
	
	public MazeStatistics() {
		// TODO Auto-generated constructor stub
	}


	public HashMap<Integer, Coordinates> getPlayer_position() {
		return player_position;
	}
	public void setPlayer_position(HashMap<Integer, Coordinates> player_position) {
		
		this.player_position = player_position;
	}
	

	public HashMap<Coordinates,Integer> getTreasure_position() {
		return treasure_position;
	}
	public void setTreasure_position(HashMap<Coordinates,Integer> treasure_position) {
		this.treasure_position = treasure_position;
	}
	public int getTotalTresure() {
		return totalTresure;
	}
	public void setTotalTresure(int totalTresure) {
		this.totalTresure = totalTresure;
	}
	
	public HashMap<Integer, Integer> getPlayerScore() {
		
		return player_score;
	}
	
}
