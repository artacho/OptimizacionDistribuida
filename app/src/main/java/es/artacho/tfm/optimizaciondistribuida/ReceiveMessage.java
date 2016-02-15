package es.artacho.tfm.optimizaciondistribuida;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Pablo on 12/21/2015.
 */
public class ReceiveMessage extends AsyncTask<Object, Void, Message> {
    private Context context;
    /**
     * @param context
     */
    public ReceiveMessage(Context context) {
        this.context = context;
    }

    //socket = 0
    // list devices = 1
    @Override
    protected Message doInBackground(Object... objects) {
        try {
            Socket socket = (Socket) objects[0];
            ObjectOutputStream outputStream;
            Log.d(MainActivity.TAG, "Connecting to server socket");
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) inputStream.readObject();

            Action action = message.getAction();


            switch (action) {
                case ADD:

                    message.setFlag(true);
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                    outputStream.close();

                    Log.d(MainActivity.TAG, "RECIBIDO: " + message.toString());

                    break;


                case EXEC:

                    Log.d(MainActivity.TAG, "EJECUTANDO RECIBIDO");


                    DeviceDetailFragment fragmentDetail = (DeviceDetailFragment) ((MainActivity) context).getFragmentManager()
                            .findFragmentById(R.id.frag_detail);

                    if (fragmentDetail != null) {
                        Log.d(MainActivity.TAG, "Group Owner IP: " + fragmentDetail.info.groupOwnerAddress.toString());
                    } else {
                        Log.d(MainActivity.TAG, "ES NULO EL FRAGMENT");
                    }



                default:

                    break;
            }





            return message;



        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Message message) {
        Log.d(MainActivity.TAG, "Soy servidor y voy a hacer un texto");


        Action action = message.getAction();

        switch (action) {
            case IP:

                DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                        .findFragmentById(R.id.frag_list);

                if (fragmentList != null) {
                    for (Device d : fragmentList.getDevices()) {
                        if (d.getDevice().deviceAddress.equals(message.getAddress())) {
                            d.setIp(message.toString());
                            Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                break;

            case EXEC:


                break;

        }

    }

}