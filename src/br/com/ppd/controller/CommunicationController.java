package br.com.ppd.controller;

import br.com.ppd.model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CommunicationController extends Remote {
    void sendMessage(Player player, String message) throws RemoteException;
    void move(int initX, int initY, int destinationX, int destinationY, Player player) throws RemoteException;
    void startGame() throws RemoteException;
    void victory(Player player) throws RemoteException;
    void restartGame() throws RemoteException;
    void endTurn(int turn) throws RemoteException;
    void giveup() throws RemoteException;
}
