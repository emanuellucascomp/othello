package br.com.ppd.controller;

import br.com.ppd.model.Command;
import br.com.ppd.model.StartGameCommand;
import br.com.ppd.thread.ThreadReceiveCommands;
import br.com.ppd.thread.ThreadSendCommands;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CommandController {
    private Socket socket;
    private ServerSocket serverSocket;
    private String host;
    private int port;

    private ThreadSendCommands threadSendCommands;
    private ThreadReceiveCommands threadReceiveCommands;

    private List<Command> toSend;
    private List<Command> receivedCommands;

    private Object sendCommandLock;
    private Object updateViewLock;

    public CommandController(String host, int port) {
        this.sendCommandLock = new Object();
        this.updateViewLock = new Object();

        this.host = host;
        this.port = port;

        this.toSend = new ArrayList<>();
        this.receivedCommands = new ArrayList<Command>();
        this.threadSendCommands = new ThreadSendCommands(this.toSend, this.sendCommandLock);
        this.threadReceiveCommands = new ThreadReceiveCommands(this.receivedCommands, this.updateViewLock);
    }

    public void createServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.socket = this.serverSocket.accept();

        this.threadSendCommands.setSocket(socket);
        Thread sender = new Thread(this.threadSendCommands);
        sender.start();

        this.threadReceiveCommands.setSocket(socket);
        Thread receiver = new Thread(this.threadReceiveCommands);
        receiver.start();
    }

    public void connect() throws UnknownHostException, IOException {
        this.socket = new Socket(this.host, this.port);
        this.threadSendCommands.setSocket(socket);
        Thread sender = new Thread(this.threadSendCommands);
        sender.start();

        this.threadReceiveCommands.setSocket(socket);
        Thread receiver = new Thread(this.threadReceiveCommands);
        receiver.start();

        this.addCommand(new StartGameCommand());
    }

    public void addCommand(Command c) {
        this.toSend.add(c);
        synchronized (sendCommandLock) {
            sendCommandLock.notify();
        }
    }

    public List<Command> getReceivedCommands() {
        return this.receivedCommands;
    }

    public Object getUpdateViewLock() {
        return this.updateViewLock;
    }

    public void stopCommunication() {
        this.threadSendCommands.stop();
        this.threadReceiveCommands.stop();
        closeSocket();
        closeServer();

        relaseLock(this.sendCommandLock);
        relaseLock(this.updateViewLock);
    }

    private void relaseLock(Object lock) {
        synchronized (lock) {
            lock.notify();
        }
    }

    private void closeServer() {
        try {
            if (this.serverSocket != null)
                this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            if (this.socket != null)
                this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
