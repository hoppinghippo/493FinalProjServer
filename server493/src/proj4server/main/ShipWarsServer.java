package proj4server.main;

import proj4server.classes.ServerGame;

public class ShipWarsServer{
  public static void main(String [] args){
    ServerGame game = new ServerGame();
    game.startServer();
    game.getAndSendPlayersReady();
    game.getShipPlacement();
    game.getAndSendPlayerNames();
    while(true){
      game.getPowerUps();
      int count = game.getShots();
      game.sendShots(count);
      game.receiveShipsRemaining();
      game.updateShipsRemaining();
      game.sendPowerups();
      game.flipspower();
    }
  }
}