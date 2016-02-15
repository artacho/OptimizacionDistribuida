package es.artacho.tfm.optimizaciondistribuida;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import es.artacho.tfm.optimizaciondistribuida.DeviceListFragment.DeviceActionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    protected Device myDevice;
    protected WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 0;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);

                /*if (myDevice != null && info != null) {
                    new Protocol(getActivity(), Action.CONNECT, myDevice).execute(info.groupOwnerAddress.toString().substring(1,info.groupOwnerAddress.toString().length()));
                } else {
                    Log.d(MainActivity.TAG, "NULOOO");
                }*/


            }
        });
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });
        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);*/
                        Log.d(MainActivity.TAG, "GO IP: " + info.groupOwnerAddress.toString());
                        String s = info.groupOwnerAddress.toString();
                        s = s.substring(1, s.length());
                        Log.d(MainActivity.TAG, "GO IP: " + s);

                        /*new SendMessage(getActivity(), Action.IP, myDevice)
                                .execute(s);*/
                    }
                });
        mContentView.findViewById(R.id.btn_add_slave).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Protocol(getActivity(), Action.ADD, myDevice, null, 0, null).execute(myDevice.getIp());
                        //new SendMessage(getActivity(), Action.ADD, myDevice)
                                //.execute(myDevice.getIp());

                        /*DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                                .findFragmentById(R.id.frag_list);

                        if (fragmentList != null) {
                            ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                        }*/

                        Log.d(MainActivity.TAG, device.deviceAddress);
                    }
                });
        mContentView.findViewById(R.id.btn_exec).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(MainActivity.TAG, "EXEC");


                        new SendMessage(getActivity(), Action.EXEC, myDevice)
                                .execute(myDevice.getIp());

                        /*DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                                .findFragmentById(R.id.frag_list);

                        if (fragmentList != null) {
                            ((DeviceListFragment.WiFiPeerListAdapter) fragmentList.getListAdapter()).notifyDataSetChanged();
                        }*/

                        //Log.d(MainActivity.TAG, device.deviceAddress);
                    }
                });
        return mContentView;
    }



    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));
        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {

            if (((MainActivity) getActivity()).masterServer == null) {
                ((MainActivity) getActivity()).masterServer = new Servidor(Constants.SERVER_MASTER_PORT, getActivity());
                ((MainActivity) getActivity()).masterServer.createServer();
                ((MainActivity) getActivity()).masterServer.start();
                ((MainActivity) getActivity()).pool = new ConcurrentLinkedQueue<Device>();
            }

            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    //.execute();

            //mContentView.findViewById(R.id.btn_add_slave).setVisibility(View.VISIBLE);
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            new Protocol(getActivity(), Action.CONNECT, myDevice, null, 1, null).execute(info.groupOwnerAddress.toString().substring(1, info.groupOwnerAddress.toString().length()));


            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }
        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }
    /**
     * Updates the UI with device data
     */
    public void showDetails(Device protocolDevice) {
        this.device = protocolDevice.getDevice();
        this.myDevice = protocolDevice;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

        Log.d(MainActivity.TAG, "IP: " + protocolDevice.getIp());

        // Permite añadir al pool los dispositivos conectados
        if (device.status == 0) {
            mContentView.findViewById(R.id.btn_add_slave).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.btn_add_slave).setVisibility(View.GONE);
        }

        // si soy slave y estoy conectado permitir desconexion
    }
    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        mContentView.findViewById(R.id.btn_add_slave).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


}