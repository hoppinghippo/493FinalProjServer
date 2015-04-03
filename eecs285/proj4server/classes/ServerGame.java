package eecs285.proj4server.classes;

import java.awt.Dimension;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import eecs285.proj4server.classes.Shot;

public class ServerGame{
  private int port;
  private ServerSocket serverSocket;
  
  private boolean[] ocean1;
  private boolean[] ocean2;
  private boolean[] ocean3;
  private boolean[] ocean4;
  
  private int shipsRemaining1;
  private int shipsRemaining2;
  private int shipsRemaining3;
  private int shipsRemaining4;
  
  private Socket player1;
  private Socket player2;
  private Socket player3;
  private Socket player4;
  private DataOutputStream outData1;
  private DataOutputStream outData2;
  private DataOutputStream outData3;
  private DataOutputStream outData4;
  private DataInputStream inData1;
  private DataInputStream inData2;
  private DataInputStream inData3;
  private DataInputStream inData4;
  
  private String name1;
  private String name2;
  private String name3;
  private String name4;
  
  private Shot[] roundShots;
  
  public ServerGame(){
    port = 5000;
    ocean1 = new boolean[100];
    ocean2 = new boolean[100];
    ocean3 = new boolean[100];
    ocean4 = new boolean[100];
    roundShots = new Shot[40];
    shipsRemaining1 = 5;
    shipsRemaining2 = 5;
    shipsRemaining3 = 5;
    shipsRemaining4 = 5;
  }
  
  //used to start server and wait for a client to connect
  //waits for a connection before continuing 
  public void startServer(){
    
    JFrame test = new JFrame("ShipWars Server");
    test.pack();
    test.setVisible(true);
    test.setMinimumSize(new Dimension(185, 10));
    test.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
    try{
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for 4 players to connect...");
        player1 = serverSocket.accept();  
        outData1 = new DataOutputStream(player1.getOutputStream());
        inData1 = new DataInputStream(player1.getInputStream());
        System.out.println("Waiting for 3 players to connect...");
        player2 = serverSocket.accept();  
        outData2 = new DataOutputStream(player2.getOutputStream());
        inData2 = new DataInputStream(player2.getInputStream());
        System.out.println("Waiting for 2 players to connect...");
        player3 = serverSocket.accept();  
        outData3 = new DataOutputStream(player3.getOutputStream());
        inData3 = new DataInputStream(player3.getInputStream());
        System.out.println("Waiting for 1 player to connect...");
        player4 = serverSocket.accept();  
        outData4 = new DataOutputStream(player4.getOutputStream());
        inData4 = new DataInputStream(player4.getInputStream());
        System.out.println("4 players connected.");
    }
    catch(IOException ioe){
      System.out.println("ERROR: Could not connect to port.");
      System.exit(1);
    }
  }
  
  public void getAndSendPlayersReady(){
    for(int i = 0; i < 4; ++i){
      try{        
        boolean found = false;
        while(!found){
          if(inData1.available() > 0){
            inData1.readBoolean();
            found = true;
            System.out.println("Player 1 ready.");
          }
          else if(inData2.available() > 0){
            inData2.readBoolean();
            found = true;
            System.out.println("Player 2 ready.");
          }
          else if(inData3.available() > 0){
            inData3.readBoolean();
            found = true;
            System.out.println("Player 3 ready.");
          }
          else if(inData4.available() > 0){
            inData4.readBoolean();
            found = true;
            System.out.println("Player 4 ready.");
          }
        }
        outData1.writeBoolean(true);
        outData2.writeBoolean(true);
        outData3.writeBoolean(true);
        outData4.writeBoolean(true);
      }
      
      catch(IOException ioe){}
    }    
  }
  
  public void sendPlayerNames(DataOutputStream outData, String name,
                              int playerNumber){
    try{
      outData.writeUTF(name);
      outData.writeInt(playerNumber);
    }
    catch (IOException ioe){}
  }
  
  public void getAndSendPlayerNames(){
    try{
      name1 = inData1.readUTF();
      name2 = inData2.readUTF();
      name3 = inData3.readUTF();
      name4 = inData4.readUTF();
      
      // p1
      sendPlayerNames(outData1, name1, 1);
      sendPlayerNames(outData1, name2, 2);
      sendPlayerNames(outData1, name3, 3);
      sendPlayerNames(outData1, name4, 4);
      
      // p2
      sendPlayerNames(outData2, name1, 2);
      sendPlayerNames(outData2, name2, 1);
      sendPlayerNames(outData2, name3, 3);
      sendPlayerNames(outData2, name4, 4);
      
      // p3
      sendPlayerNames(outData3, name1, 2);
      sendPlayerNames(outData3, name2, 3);
      sendPlayerNames(outData3, name3, 1);
      sendPlayerNames(outData3, name4, 4);
      
      // p4
      sendPlayerNames(outData4, name1, 2);
      sendPlayerNames(outData4, name2, 3);
      sendPlayerNames(outData4, name3, 4);
      sendPlayerNames(outData4, name4, 1);
    }
    catch(IOException ioe){}
    
  }
  
  public void getShipPlacement(){
    try{
      //player 1
      for(int i = 0; i < 100; ++i){
        ocean1[i] = inData1.readBoolean();
      }
      
      //player 2
      for(int i = 0; i < 100; ++i){
        ocean2[i] = inData2.readBoolean();
      }
      //player 3
      for(int i = 0; i < 100; ++i){
        ocean3[i] = inData3.readBoolean();
      }
      //player 4
      for(int i = 0; i < 100; ++i){
        ocean4[i] = inData4.readBoolean();
      }
      
    }
    catch(IOException excep){}
  }
  
  public int getShots(){
    int totalRoundShots = 0;
    try{
      
      boolean hitOrMiss = false;
      int shotNumber = 0;
      int count = 0;
      int playerNumber;
      int index;
      
      //player 1
      count = inData1.readInt();
      totalRoundShots += count;
      for(int i = 0; i < count; ++i){
        playerNumber = inData1.readInt();
        index = inData1.readInt();
        if(playerNumber == 2){
          hitOrMiss = ocean2[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 2);
          ++shotNumber;
        }
        else if(playerNumber == 3){
          hitOrMiss = ocean3[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 3);
          ++shotNumber;
        }
        else if(playerNumber == 4){
          hitOrMiss = ocean4[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 4);
          ++shotNumber;
        }
      }
      
      //player 2
      count = inData2.readInt();
      totalRoundShots += count;
      for(int i = 0; i < count; ++i){
        playerNumber = inData2.readInt();
        index = inData2.readInt();
        if(playerNumber == 2){
          hitOrMiss = ocean1[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 1);
          ++shotNumber;
        }
        else if(playerNumber == 3){
          hitOrMiss = ocean3[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 3);
          ++shotNumber;
        }
        else if(playerNumber == 4){
          hitOrMiss = ocean4[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 4);
          ++shotNumber;
        }
      }
      
      //player 3
      count = inData3.readInt();
      totalRoundShots += count;
      for(int i = 0; i < count; ++i){
        playerNumber = inData3.readInt();
        index = inData3.readInt();
        if(playerNumber == 2){
          hitOrMiss = ocean1[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 1);
          ++shotNumber;
        }
        else if(playerNumber == 3){
          hitOrMiss = ocean2[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 2);
          ++shotNumber;
        }
        else if(playerNumber == 4){
          hitOrMiss = ocean4[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 4);
          ++shotNumber;
        }
      }
      
      //player 4
      count = inData4.readInt();
      totalRoundShots += count;
      for(int i = 0; i < count; ++i){
        playerNumber = inData4.readInt();
        index = inData4.readInt();
        if(playerNumber == 2){
          hitOrMiss = ocean1[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 1);
          ++shotNumber;
        }
        else if(playerNumber == 3){
          hitOrMiss = ocean2[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 2);
          ++shotNumber;
        }
        else if(playerNumber == 4){
          hitOrMiss = ocean3[index];
          roundShots[shotNumber] = new Shot(hitOrMiss, index, 3);
          ++shotNumber;
        }
      }
    }
    catch(IOException excep){}
    return totalRoundShots;
  }
  
  public void sendHitOrMissHelper(DataOutputStream playerOS, boolean hitOrMiss,
                                  int index, int playerNumber){
    try{
      playerOS.writeInt(playerNumber);
      playerOS.writeBoolean(hitOrMiss);
      playerOS.writeInt(index);
    }
    catch(IOException excep){}
  }
  
  public void sendShipsRemaining(DataOutputStream playerOS, int shipsRemaining,
                                 int playerNumber){
    try{
      playerOS.writeInt(playerNumber);
      playerOS.writeInt(shipsRemaining);
    }
    catch(IOException excep){}
  }
  
  public void sendShots(int count){
    int playerNumber;
    try{
      outData1.writeInt(count);
      outData2.writeInt(count);
      outData3.writeInt(count);
      outData4.writeInt(count);
    }
    catch(IOException excep){}
    
    // player 1
    for(int i = 0; i < count; ++i){
      playerNumber = roundShots[i].getPlayerNumber();
      if(playerNumber == 1){
        sendHitOrMissHelper(outData1, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 1);
        sendHitOrMissHelper(outData2, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 2);
        sendHitOrMissHelper(outData3, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 2);
        sendHitOrMissHelper(outData4, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 2);
      }
      else if(playerNumber == 2){
        sendHitOrMissHelper(outData1, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 2);
        sendHitOrMissHelper(outData2, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 1);
        sendHitOrMissHelper(outData3, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 3);
        sendHitOrMissHelper(outData4, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 3);
      }
      else if(playerNumber == 3){
        sendHitOrMissHelper(outData1, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 3);
        sendHitOrMissHelper(outData2, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 3);
        sendHitOrMissHelper(outData3, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 1);
        sendHitOrMissHelper(outData4, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 4);
      }
      else if(playerNumber == 4){
        sendHitOrMissHelper(outData1, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 4);
        sendHitOrMissHelper(outData2, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 4);
        sendHitOrMissHelper(outData3, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 4);
        sendHitOrMissHelper(outData4, roundShots[i].getHitOrMiss(),
                            roundShots[i].getIndex(), 1);
      }
    }
  }
  
  public void receiveShipsRemaining(){
    try{
      shipsRemaining1 = inData1.readInt();
      shipsRemaining2 = inData2.readInt();
      shipsRemaining3 = inData3.readInt();
      shipsRemaining4 = inData4.readInt();
    }
    catch(IOException excep){}
  }
  
  public void updateShipsRemaining(){
    // player 1
    sendShipsRemaining(outData1, shipsRemaining1, 1);
    sendShipsRemaining(outData1, shipsRemaining2, 2);
    sendShipsRemaining(outData1, shipsRemaining3, 3);
    sendShipsRemaining(outData1, shipsRemaining4, 4);
    
    // player 2
    sendShipsRemaining(outData2, shipsRemaining1, 2);
    sendShipsRemaining(outData2, shipsRemaining2, 1);
    sendShipsRemaining(outData2, shipsRemaining3, 3);
    sendShipsRemaining(outData2, shipsRemaining4, 4);
    
    // player 3
    sendShipsRemaining(outData3, shipsRemaining1, 2);
    sendShipsRemaining(outData3, shipsRemaining2, 3);
    sendShipsRemaining(outData3, shipsRemaining3, 1);
    sendShipsRemaining(outData3, shipsRemaining4, 4);
    
    // player 4
    sendShipsRemaining(outData4, shipsRemaining1, 2);
    sendShipsRemaining(outData4, shipsRemaining2, 3);
    sendShipsRemaining(outData4, shipsRemaining3, 4);
    sendShipsRemaining(outData4, shipsRemaining4, 1);
  }
  
}
