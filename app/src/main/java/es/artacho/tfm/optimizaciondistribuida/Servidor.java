package es.artacho.tfm.optimizaciondistribuida;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Pablo on 12/17/2015.
 */
public class Servidor extends Thread {
    private ServerSocket serverSocket;
    private int PORT;
    private Context context;


    public Servidor(int PORT, Context context) {
        this.PORT = PORT;
        this.context = context;
    }

    public void createServer() {
        try {
            Log.d(MainActivity.TAG, "Server created!");
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                Socket s = serverSocket.accept();
                Log.d(MainActivity.TAG, "Connection with the Server done!");
                // Proccess connection
                //new DeviceDetailFragment.ReceiveMessage(context).execute(s);
                new ReceiveMessage(context).execute(s);

            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }
}
