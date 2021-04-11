package br.com.ppd.utilitary;

public class SocketUtilitary {

    private static SocketUtilitary instance;

    public static SocketUtilitary getInstance() {
        if (instance == null)
            instance = new SocketUtilitary();
        return instance;
    }

    public static void send(Object obj) {

    }

    public static void receive() {

    }
}
