package es.artacho.tfm.optimizaciondistribuida;

/**
 * Created by Pablo on 1/7/2016.
 */

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
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
    private WifiP2pDevice senderDevice; // Information of sender device
    private Device receiverDevice; // Information of receiver device

    private DeviceListFragment fragmentList = null; // view to notify data changed

    // Default constructor
    public SendMessage(Context context, Action action, Device receiverDevice, WifiP2pDevice senderDevice) {
        this.context = context;
        this.action = action;
        this.receiverDevice = receiverDevice;
        this.senderDevice = senderDevice;

        this.fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                .findFragmentById(R.id.frag_list);

        this.senderDevice = null;
        if (fragmentList != null){
            Log.d(MainActivity.TAG, "ESTABLECIENDO SENDER");
            this.senderDevice = fragmentList.getDevice();
        }
    }

    @Override
    // Receive IP of Server and returns received Message from Server
    protected Message doInBackground(String... ip) {

        try {

            String connectIP = ip[0];
            String slaveIP;
            Socket client; // Socket for connection

            ObjectOutputStream outputStream; // OutputStream to write data
            ObjectInputStream inputStream; // InputStream to read data

            Message message = null;
            String address = null; // MAC address of sender device

            Log.d(MainActivity.TAG, "Connecting to server socket to send message");

            switch (action) {
                // Slave node must send its IP to Master node
                /*case IP:

                    double ini = 0, fin = 0;
                    ini = System.currentTimeMillis();
                    client = new Socket();

                    Log.d(MainActivity.TAG, "IP MASTER: " + connectIP);

                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), 8888);

                    outputStream = new ObjectOutputStream(client.getOutputStream());

                    String slaveIP = client.getLocalAddress().getHostAddress().toString(); // IP of slave node

                    // Create new Message
                    message = new Message(slaveIP);
                    message.setAction(Action.IP);
                    Log.d(MainActivity.TAG, senderDevice.deviceAddress);
                    message.setAddress(senderDevice.deviceAddress);

                    outputStream.writeObject(message);
                    outputStream.close();
                    client.close();
                    fin = System.currentTimeMillis();

                    Log.d(MainActivity.TAG, "TIEMPO: " + (fin - ini));


                    Log.d(MainActivity.TAG, "IP Message: " + slaveIP);



                    break;*/
                case ADD:

                    Log.d(MainActivity.TAG, "Connecting to server socket to send ADD message");
                    client = new Socket();
                    Log.d(MainActivity.TAG, connectIP);
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), 1500);
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


                    Log.d(MainActivity.TAG, "SLAVE ADDED? " + message.isFlag());

                    ((MainActivity) context).pool.add(receiverDevice);

                    Log.d(MainActivity.TAG, "POOLED DEVICES: " + ((MainActivity) context).pool.toString());

                    break;

                case EXEC:

                    Log.d(MainActivity.TAG, "Connecting to server socket to send EXEC message");
                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), 8888);
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    message = new Message(client.getLocalAddress().getHostAddress().toString());
                    message.setAction(Action.EXEC);
                    message.setFlag(false);

                    outputStream.writeObject(message);



                    outputStream.close();
                    client.close();

                    break;


                case RESUL:

                    break;

                case CONNECT:
                    Log.d(MainActivity.TAG, "SLAVE >> SENDING CONNECT MESSAGE");

                    // Socket connection
                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), Constants.CONNECTION_TIMEOUT);

                    // Get IP of slave node
                    slaveIP = client.getLocalAddress().getHostAddress().toString();

                    // Create new Message
                    Message dataMessage = new Message("");
                    dataMessage.setAction(Action.CONNECT);
                    dataMessage.setMessage(slaveIP);
                    dataMessage.setAddress(senderDevice.deviceAddress);
                    //dataMessage.set(1);

                    // Create stream and write object
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    outputStream.writeObject(dataMessage);

                    // Close resources
                    outputStream.close();
                    client.close();

                    Log.d(MainActivity.TAG, "SLAVE >> FINISHED CONNECT MESSAGE - " + slaveIP);

                    break;


                case DISCONNECT:

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

                    this.receiverDevice.setStatus(oldClasses.Status.POOL);

                    //Modificar status del device en cuestion

                    //DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                           // .findFragmentById(R.id.frag_list);

                    if (fragmentList != null) {
                        ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                    }

                    Log.d(MainActivity.TAG, "POOLED device: " + this.receiverDevice.getIp());


                    break;
            }
        }



    }
}