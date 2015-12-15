package es.artacho.tfm.optimizaciondistribuida;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by Pablo on 12/15/2015.
 */
public class Emisor extends Thread implements Serializable {
    private Message message;
    private Socket destino;

    public Emisor (Message message) {
        this.message = message;
    }

    public void run () {
        Log.d(MainActivity.TAG, "Enviando: " + message);
        ObjectOutputStream outputStream = null;

        try {
            outputStream = new ObjectOutputStream(destino.getOutputStream());
            outputStream.writeObject(message);
            outputStream.close();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }

    }
}
