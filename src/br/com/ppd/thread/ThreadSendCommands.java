package br.com.ppd.thread;

import br.com.ppd.model.Command;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ThreadSendCommands implements Runnable{

    private ObjectOutputStream outputStream;
    private List<Command> commands;

    private Object lock;

    private boolean isRunning;

    public ThreadSendCommands(List<Command> commands, Object lock) {
        this.commands = commands;
        this.lock = lock;
        this.isRunning = true;
    }

    public void setSocket(Socket socket) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream()) ;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!this.commands.isEmpty()) {
                Command c = this.commands.remove(0);
                sendObject(c);
            } else {
                blockThread();
            }
        }
    }

    private void sendObject(Command c) {
        try {
            outputStream.writeObject(c);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    private void blockThread() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.isRunning = false;
    }

}
