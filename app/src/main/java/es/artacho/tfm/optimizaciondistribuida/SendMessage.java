package es.artacho.tfm.optimizaciondistribuida;

/**
 * Created by Pablo on 1/7/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A simple socket that connects to a Server and send some Action
 * to slave
 */
public class SendMessage extends AsyncTask<String, Void, Message> {
    private Context context; // Context of Activity
    private Action action; // Message in FSM to send
    private Device device; // Sender device

    private DeviceListFragment fragmentList = null; // view to notify data changed

    // Default constructor
    public SendMessage(Context context, Action action, Device device) {
        this.context = context;
        this.action = action;
        this.device = device;
        fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                .findFragmentById(R.id.frag_list);
    }

    @Override
    // Receive IP of Server and returns received Message from Server
    protected Message doInBackground(String... ip) {
        try {
            String connectIP = ip[0];

            Socket client; // Socket for connection

            ObjectOutputStream outputStream; // OutputStream to write data
            ObjectInputStream inputStream; // InputStream to read data

            Message message = null;
            String address = null;

            Log.d(MainActivity.TAG, "Connecting to server socket to send message");

            switch (action) {
                // Slave node must send its IP to Master node
                case IP:

                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), 8888);

                    outputStream = new ObjectOutputStream(client.getOutputStream());

                    message = new Message(client.getLocalAddress().getHostAddress().toString());
                    message.setAction(Action.IP);

                    //fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                            //.findFragmentById(R.id.frag_list);

                    if (fragmentList != null) {
                        address = fragmentList.getDevice().deviceAddress;
                    }

                    Log.d(MainActivity.TAG, "DIFERENCIAS: " + address + " /// " + device.getDevice().deviceAddress);

                    message.setAddress(address);

                    outputStream.writeObject(message);
                    outputStream.close();
                    client.close();
                    Log.d(MainActivity.TAG, "SLAVE ADDRESS: " + address);


                    break;


                case ADD:
                    Log.d(MainActivity.TAG, "Connecting to server socket to send ADD message");
                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), 8888);
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    message = new Message(client.getLocalAddress().getHostAddress().toString());
                    message.setAction(Action.ADD);
                    message.setFlag(false);

                    outputStream.writeObject(message);

                    inputStream = new ObjectInputStream(client.getInputStream());
                    message = (Message) inputStream.readObject();


                    outputStream.close();
                    inputStream.close();
                    client.close();


                    Log.d(MainActivity.TAG, "SLAVE ADDED? " + message.isFlag() );

                    break;
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
        if (message != null) {
            switch (message.getAction()) {
                case ADD:
                    Toast.makeText(context, new Boolean(message.isFlag()).toString(), Toast.LENGTH_LONG).show();

                    this.device.setStatus(es.artacho.tfm.optimizaciondistribuida.Status.POOL);

                    //Modificar status del device en cuestion

                    //DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                           // .findFragmentById(R.id.frag_list);

                    if (fragmentList != null) {
                        ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                    }

                    Log.d(MainActivity.TAG, "POOLED device: " + this.device.getIp());


                    break;
            }
        }



    }
}