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

import es.artacho.tfm.optimizaciondistribuida.ga.Individual;

/**
 * A simple socket that connects to a Server and send some Action
 * to slave
 */
public class SendMessage extends AsyncTask<String, Void, Message> {
    private Context context; // Context of Activity
    private Action action; // Message in FSM to send
    private WifiP2pDevice senderDevice; // Information of sender device
    private Device receiverDevice; // Information of receiver device
    private Individual individual;
    private DeviceListFragment fragmentList = null; // view to notify data changed

    // Default constructor
    public SendMessage(Context context, Action action, Device receiverDevice, WifiP2pDevice senderDevice, Individual individual) {
        this.context = context;
        this.action = action;
        this.receiverDevice = receiverDevice;
        this.senderDevice = senderDevice;
        this.individual = individual;

        this.fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                .findFragmentById(R.id.frag_list);

        if (fragmentList != null){
            this.senderDevice = fragmentList.getDevice();
        }
    }

    // Receive IP of Server to connect and returns received Message
    @Override
    protected Message doInBackground(String... ip) {

        try {
            String slaveIP; // IP of slave device
            Socket client; // Socket for connection
            Message message = null; // Message to send
            String address = null; // MAC address of sender device
            PoolDevice pooledDevice;
            ObjectOutputStream outputStream; // OutputStream to write data
            ObjectInputStream inputStream; // InputStream to read data

            String connectIP = ip[0]; // IP of server

            //Log.d(MainActivity.TAG, "Connecting to server socket to send message");

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
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), Constants.CONNECTION_TIMEOUT);
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


                    if (receiverDevice != null) {
                        //client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), 8888);
                        client.connect((new InetSocketAddress(receiverDevice.getIp(), Constants.SERVER_SLAVE_PORT)), 8888);
                        outputStream = new ObjectOutputStream(client.getOutputStream());
                        message = new Message(client.getLocalAddress().getHostAddress().toString());
                        message.setIndividual(null);
                        //message.setAddress();
                        message.setAction(Action.EXEC);
                        message.setFlag(false);

                        pooledDevice = new PoolDevice(receiverDevice.getDevice().deviceAddress, receiverDevice.getDevice().deviceName, receiverDevice.getStatus(), receiverDevice.getIp());

                        message.setReceiver(pooledDevice);


                        outputStream.writeObject(message);
                    } else {
                        client.connect((new InetSocketAddress("", Constants.SERVER_SLAVE_PORT)), 8888);
                        outputStream = new ObjectOutputStream(client.getOutputStream());
                        message = new Message(client.getLocalAddress().getHostAddress().toString());
                        message.setAction(Action.EXEC);
                        message.setFlag(false);
                        outputStream.writeObject(message);

                    }

                    outputStream.close();
                    client.close();

                    break;


                // Modificar
                case RESUL:

                    Log.d(MainActivity.TAG, "Connecting to server socket to send RESUL message");

                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), Constants.CONNECTION_TIMEOUT);
                    slaveIP = client.getLocalAddress().getHostAddress().toString();

                    // Create new Message
                    Message dataMessage = new Message("");
                    dataMessage.setAction(Action.RESUL);
                    dataMessage.setMessage(slaveIP);
                    dataMessage.setAddress(senderDevice.deviceAddress);
                    dataMessage.setIndividual(individual);
                    pooledDevice = new PoolDevice(receiverDevice.getDevice().deviceAddress, receiverDevice.getDevice().deviceName, receiverDevice.getStatus(), receiverDevice.getIp());
                    dataMessage.setReceiver(pooledDevice);
                    //dataMessage.set(1);

                    // Create stream and write object
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    outputStream.writeObject(dataMessage);

                    /*inputStream = new ObjectInputStream(client.getInputStream());
                    message = (Message) inputStream.readObject();*/
                    Log.d(MainActivity.TAG, "RESUL finalizado");

                    // Close resources
                    outputStream.close();
                    //inputStream.close();
                    client.close();

                    Log.d(MainActivity.TAG, "SLAVE >> FINISHED RESUL MESSAGE - " + slaveIP);

                    break;


                case CONNECT:
                    Log.d(MainActivity.TAG, "SLAVE >> SENDING CONNECT MESSAGE");

                    // Socket connection
                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), Constants.CONNECTION_TIMEOUT);

                    // Get IP of slave node
                    slaveIP = client.getLocalAddress().getHostAddress().toString();

                    // Create new Message
                    dataMessage = new Message("");
                    dataMessage.setAction(Action.CONNECT);
                    dataMessage.setMessage(slaveIP);
                    dataMessage.setAddress(senderDevice.deviceAddress);
                    //dataMessage.set(1);

                    // Create stream and write object
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    outputStream.writeObject(dataMessage);

                    inputStream = new ObjectInputStream(client.getInputStream());
                    message = (Message) inputStream.readObject();
                    Log.d(MainActivity.TAG, "CONNECT finalizado");

                    // Close resources
                    outputStream.close();
                    inputStream.close();
                    client.close();

                    Log.d(MainActivity.TAG, "SLAVE >> FINISHED CONNECT MESSAGE - " + slaveIP);

                    break;


                case DISCONNECT:

                    Log.d(MainActivity.TAG, "Connecting to server socket to send DISCONNECT message");

                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), Constants.CONNECTION_TIMEOUT);
                    outputStream = new ObjectOutputStream(client.getOutputStream());

                    Log.d(MainActivity.TAG, "ENVIA + " + senderDevice.deviceName );
                    message = new Message(senderDevice.deviceName);
                    message.setAction(Action.DISCONNECT);
                    message.setFlag(false);

                    outputStream.writeObject(message);

                    inputStream = new ObjectInputStream(client.getInputStream());
                    message = (Message) inputStream.readObject();

                    outputStream.close();
                    inputStream.close();
                    client.close();

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

                    this.receiverDevice.setStatus(es.artacho.tfm.optimizaciondistribuida.Status.POOL);

                    //Modificar status del device en cuestion

                    //DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                    // .findFragmentById(R.id.frag_list);

                    if (fragmentList != null) {
                        ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                    }

                    Log.d(MainActivity.TAG, "POOLED device: " + this.receiverDevice.getIp());


                    break;
                case DISCONNECT:

                    ((DeviceListFragment.DeviceActionListener) ((MainActivity) context)).disconnect();
                    break;
            }
        }



    }
}