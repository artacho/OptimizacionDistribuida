package es.artacho.tfm.optimizaciondistribuida;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
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
    // Receive socket connection and read from input
    @Override
    protected Message doInBackground(Object... objects) {

        try {
            Socket socket = (Socket) objects[0]; // socket
            ObjectOutputStream outputStream = null;

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) inputStream.readObject();

            Action action = message.getAction();

            switch (action) {
                case CONNECT:

                    message.setFlag(true);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                    outputStream.close();

                    Log.d(MainActivity.TAG, "CONNECT MESSAGE RECIBIDO: " + message.toString());

                    break;

                case DISCONNECT:

                    message.setFlag(true);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());


                    WifiP2pDevice p = new WifiP2pDevice();
                    p.deviceName = message.getMessage();

                    outputStream.writeObject(message);
                    outputStream.close();

                    Log.d(MainActivity.TAG, "DISCONNECT MESSAGE RECIBIDO: " + message.toString());


                    Log.d(MainActivity.TAG, "SLAVE REMOVED? " + message.isFlag());

                    for (Device d : ((MainActivity) context).pool) {
                        if (d.getDevice().deviceName.equals(message.toString())) ((MainActivity) context).pool.remove(d);
                    }

                    //((MainActivity) context).pool.remove(p);

                    Log.d(MainActivity.TAG, "POOLED DEVICES: " + ((MainActivity) context).pool.toString());

                    break;

                case ADD:

                    message.setFlag(true);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                    outputStream.close();

                    Log.d(MainActivity.TAG, "ADD MESSAGE RECIBIDO: " + message.toString());

                    break;

                case EXEC:

                    Log.d(MainActivity.TAG, "EJECUTANDO RECIBIDO");


                    DeviceDetailFragment fragmentDetail = (DeviceDetailFragment) ((MainActivity) context).getFragmentManager()
                            .findFragmentById(R.id.frag_detail);

                    if (fragmentDetail != null) {
                        Log.d(MainActivity.TAG, "SE PUEDE EJECUTAR EL EXE " + fragmentDetail.info.groupOwnerAddress.toString());
                    } else {
                        Log.d(MainActivity.TAG, "ES NULO EL FRAGMENT");
                    }

                    break;


                case RESUL:

                    message.setFlag(true);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                    outputStream.close();

                    Log.d(MainActivity.TAG, "RESUL MESSAGE RECIBIDO: " + message.toString());


                    break;

                default:

                    break;
            }

            socket.close();

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
            /*case IP:

                DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                        .findFragmentById(R.id.frag_list);

                Log.d(MainActivity.TAG, "Entro1");
                if (fragmentList != null) {
                    Log.d(MainActivity.TAG, "Entro2");
                    for (Device d : fragmentList.getDevices()) {
                        if (d.getDevice().deviceAddress.equals(message.getAddress())) {
                            d.setIp(message.toString());
                            Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                break;*/

            case CONNECT:

                DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                        .findFragmentById(R.id.frag_list);

                Log.d(MainActivity.TAG, "Entro1");
                if (fragmentList != null) {
                    Log.d(MainActivity.TAG, "Entro2");
                    for (Device d : fragmentList.getDevices()) {
                        if (d.getDevice().deviceAddress.equals(message.getAddress())) {
                            d.setIp(message.toString());
                            Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                break;

            case EXEC:

                Toast.makeText(context,"1", Toast.LENGTH_SHORT).show();

                break;

            case RESUL:

                //anadir al pool




                break;

        }

    }

}