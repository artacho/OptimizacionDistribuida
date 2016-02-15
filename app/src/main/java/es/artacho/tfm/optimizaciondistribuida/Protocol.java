package es.artacho.tfm.optimizaciondistribuida;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
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
 * Created by Pablo on 1/11/2016.
 */
public class Protocol extends AsyncTask<String, Void, Data> {

    private Context context; // Context of Activity
    private Action action; // Message in FSM to send
    private Device receiverDevice; // Information of receiver device
    private WifiP2pDevice senderDevice; // Information of sender device
    private int role; // Role of sender device, master = 0 or slave = 1
    private Socket socket;

    private DeviceListFragment fragmentList = null; // view to notify data changed

    // Default constructor
    public Protocol(Context context, Action action, Device receiverDevice, WifiP2pDevice senderDevice, int role, Socket socket) {
        this.context = context;
        this.action = action;
        this.receiverDevice = receiverDevice;
        this.role = role;
        this.socket = socket;

        this.fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                .findFragmentById(R.id.frag_list);

        this.senderDevice = null;
        if (fragmentList != null) this.senderDevice = fragmentList.getDevice();
    }

    @Override
    protected Data doInBackground(String... strings) {
        String connectIP = strings[0];

        Socket client; // Socket for connection

        ObjectOutputStream outputStream = null; // OutputStream to write data
        ObjectInputStream inputStream = null; // InputStream to read data

        String address = null; // MAC address of sender device

        Data dataMessage = null;

        try {
            switch (action) {

                case IP:

                    break;

                case CONNECT:
                    Log.d(MainActivity.TAG, "SLAVE >> SENDING CONNECT MESSAGE");

                    // Socket connection
                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_MASTER_PORT)), Constants.CONNECTION_TIMEOUT);

                    // Get IP of slave node
                    String slaveIP = client.getLocalAddress().getHostAddress().toString();

                    // Create new Message
                    dataMessage = new Data();
                    dataMessage.setAction(action);
                    dataMessage.setData(slaveIP);
                    dataMessage.setMAC(senderDevice.deviceAddress);
                    dataMessage.setRole(1);

                    // Create stream and write object
                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    outputStream.writeObject(dataMessage);

                    // Close resources
                    outputStream.close();
                    client.close();

                    Log.d(MainActivity.TAG, "SLAVE >> FINISHED CONNECT MESSAGE - " + slaveIP);

                    break;

                case ADD:
                    Log.d(MainActivity.TAG, "MASTER >> SENDING ADD MESSAGE");

                    client = new Socket();
                    client.connect((new InetSocketAddress(connectIP, Constants.SERVER_SLAVE_PORT)), Constants.CONNECTION_TIMEOUT);

                    Log.d(MainActivity.TAG, connectIP);

                    dataMessage = new Data();
                    dataMessage.setData(client.getLocalAddress().getHostAddress().toString());
                    dataMessage.setAction(Action.ADD);
                    dataMessage.setRole(0);
                    dataMessage.setAck(false);

                    outputStream = new ObjectOutputStream(client.getOutputStream());
                    outputStream.writeObject(dataMessage);

                    inputStream = new ObjectInputStream(client.getInputStream());
                    dataMessage = (Data) inputStream.readObject();

                    Log.d(MainActivity.TAG, "ROL: " + dataMessage.getRole());
                    Log.d(MainActivity.TAG, "DATA: " + dataMessage.getData());

                    outputStream.close();
                    inputStream.close();
                    client.close();


                    Log.d(MainActivity.TAG, "MASTER >> FINISHED ADD MESSAGE - " + connectIP);

                    /*((MainActivity) context).pool.add(receiverDevice);

                    Log.d(MainActivity.TAG, "POOLED DEVICES: " + ((MainActivity) context).pool.toString());*/

                    break;


                case EXEC:

                    break;

                case RESUL:

                    break;

                case DISCONNECT:

                    break;

                default:

                    dataMessage = receiveMessage();
                    return dataMessage;
                    //break;


            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dataMessage;

    }

    private Data receiveMessage() {
        try {

            Log.d(MainActivity.TAG, "* >> RECEIVING MESSAGE");

            ObjectOutputStream outputStream;

            // Create input stream and read data
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Data dataMessage = (Data) inputStream.readObject();

            Action action = dataMessage.getAction();

            Log.d(MainActivity.TAG, "MESSAGE TYPE : " + action.toString());

            switch (action) {

                case CONNECT:

                    dataMessage.setRole(0);

                    break;

                case ADD:

                    dataMessage.setAck(true);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(dataMessage);
                    outputStream.close();

                    dataMessage = null;

                    break;


                case EXEC:

                   /* Log.d(MainActivity.TAG, "EJECUTANDO RECIBIDO");


                    DeviceDetailFragment fragmentDetail = (DeviceDetailFragment) ((MainActivity) context).getFragmentManager()
                            .findFragmentById(R.id.frag_detail);

                    if (fragmentDetail != null) {
                        Log.d(MainActivity.TAG, "Group Owner IP: " + fragmentDetail.info.groupOwnerAddress.toString());
                    } else {
                        Log.d(MainActivity.TAG, "ES NULO EL FRAGMENT");
                    }*/



                default:

                    break;
            }





            return dataMessage;



        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Data dataMessage) {

        if (dataMessage != null && dataMessage.getRole() == 0) {

            Action action = dataMessage.getAction();

            Log.d(MainActivity.TAG, "POSTEXECUTE: " + action.toString());

            switch (action) {



                case CONNECT:

                    if (fragmentList != null) {

                        Log.d(MainActivity.TAG, "ENTROOOOO");

                        for (Device d : fragmentList.getDevices()) {
                            if (d.getDevice().deviceAddress.equals(dataMessage.getMAC())) {
                                d.setIp(dataMessage.getData());
                                Toast.makeText(context, dataMessage.getData(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    break;



                case ADD:
                    Toast.makeText(context, new Boolean(dataMessage.isAck()).toString(), Toast.LENGTH_LONG).show();

                    this.receiverDevice.setStatus(es.artacho.tfm.optimizaciondistribuida.Status.POOL);

                   //Modificar status del device en cuestion

                    //DeviceListFragment fragmentList = (DeviceListFragment) ((MainActivity) context).getFragmentManager()
                    // .findFragmentById(R.id.frag_list);

                    if (fragmentList != null) {
                        ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                    }

                    break;
            }
        }
}}
