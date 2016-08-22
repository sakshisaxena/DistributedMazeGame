package com.peertopeer.peer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.peertopeer.peer.Coordinates;
import com.peertopeer.peer.MazeStatistics;

public interface Peer_Interface extends Remote{

	
	//MazeStatistics stats = null;

	public long sayHello()throws RemoteException;
	
	public Coordinates createPlayerMap(int playerid, Peer_Interface obj) throws RemoteException;

	public void stayConnected() throws RemoteException;

	public boolean timerToStart(long tEnd) throws RemoteException;

	public MazeStatistics getStats() throws RemoteException;
	
	public void createBackupServer() throws RemoteException;

	public void notifyPeers(int playerId, MazeStatistics stats) throws RemoteException;
	
	public void setStats(MazeStatistics mazeStatistics) throws RemoteException;
	
	public void setCheckPrimary(boolean checkPrimary)throws RemoteException;

	public boolean isCheckBackup()throws RemoteException;

	public void setCheckBackup(boolean checkBackup)throws RemoteException;

	public boolean isFirstPeer()throws RemoteException;

	public void setFirstPeer(boolean firstPeer)throws RemoteException;

	public void buildMaze() throws RemoteException;

	public void start() throws RemoteException;

	public void makeAMove() throws RemoteException;

	public MazeStatistics move(int playerId, String direction) throws RemoteException;
	public void updatePrimaryAndBackupServer(int primaryId, int backUpId,Peer_Interface pri, Peer_Interface Bac) throws RemoteException;
	public MazeStatistics checkMove(int player_Id, String direction) throws RemoteException;
	public boolean isAlive() throws RemoteException;
	
}
