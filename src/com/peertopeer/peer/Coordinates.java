package com.peertopeer.peer;
import java.io.Serializable;
/* The Coordinates class contains the X and Y coordinates - generation logic. 
 */
public class Coordinates implements Serializable {

	private static final long serialVersionUID = 1L;
			int x;
	        int y;

	        public boolean equals(Object o) {
	        	Coordinates c = (Coordinates) o;
	            return c.x == x && c.y == y;
	        }

	        public int hashCode(){
	            int hashcode = 0;
	            hashcode = x*20;
	            hashcode += y*30;
	            return hashcode;
	        }
	        
	        public Coordinates(int x, int y) {
	            super();
	            this.x = x;
	            this.y = y;
	        }
	        
	        public int getXCoordinate(){
	        return x;
	        }
	        
	        public int getYCoordinate(){
	        return y;
	        }

	        public int getMazeSize(){
	        	int N=6;
	        	return N;
	        }
	}

