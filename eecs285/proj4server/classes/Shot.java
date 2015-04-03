package eecs285.proj4server.classes;

public class Shot {
  
  boolean hitOrMiss;
  int index;
  int playerNumber;
  
  Shot(boolean inHitOrMiss, int inIndex, int inPlayerNumber){
    hitOrMiss = inHitOrMiss;
    index = inIndex;
    playerNumber = inPlayerNumber;
  }
  
  public void setShot(boolean inHitOrMiss, int inIndex, int inPlayerNumber){
    hitOrMiss = inHitOrMiss;
    index = inIndex;
    playerNumber = inPlayerNumber;
  }
  
  public void resetShot(){
    hitOrMiss = false;
    index = -1;
    playerNumber = 0;
  }
  
  public boolean getHitOrMiss(){
    return hitOrMiss;
  }
  
  public int getIndex(){
    return index;
  }
  
  public int getPlayerNumber(){
    return playerNumber;
  }

}
