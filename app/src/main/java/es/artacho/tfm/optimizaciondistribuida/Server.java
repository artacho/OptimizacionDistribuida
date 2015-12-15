package es.artacho.tfm.optimizaciondistribuida;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Pablo on 12/15/2015.
 */
public class Server extends Thread {
    private ServerSocket serverSocket;
    private int PORT;
    boolean ok = true;

    public Server (int PORT) {
        this.PORT = PORT;
    }

    public Server (int PORT, boolean ok) {
        this.PORT = PORT;
        this.ok = ok;
    }

    public void createServer () {
        try {
            Log.d(MainActivity.TAG, "Server created!");
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

    public void run () {
        try {
            while (true) {
                Socket s = serverSocket.accept();
                Log.d(MainActivity.TAG, "Connection with the Server done!");
                if (ok) {
                    Message message = new Message("HOLA HOLA");
                    Emisor emisor = new Emisor(message);
                } else {
                    ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
                    Message message = (Message) inputStream.readObject();
                    Log.d(MainActivity.TAG, message.toString());
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

}
