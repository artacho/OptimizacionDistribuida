package es.artacho.tfm.optimizaciondistribuida;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import es.artacho.tfm.optimizaciondistribuida.DeviceListFragment.DeviceActionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
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
        // Connect
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 0; // priority to determine group owner

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {

//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);
            }
        });
        // Disconnect
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SendMessage(getActivity(), Action.DISCONNECT, myDevice, myDevice.getDevice()).execute(info.groupOwnerAddress.toString().substring(1, info.groupOwnerAddress.toString().length()));
                        //((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        // In this moment nothing
        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(MainActivity.TAG, "GO IP: " + info.groupOwnerAddress.toString());
                        String s = info.groupOwnerAddress.toString();
                        s = s.substring(1, s.length());
                        Log.d(MainActivity.TAG, "GO IP: " + s);
                    }
                });
        // Add
        mContentView.findViewById(R.id.btn_add_slave).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //new Protocol(getActivity(), Action.ADD, myDevice, null, 0, null).execute(myDevice.getIp());
                        Log.d(MainActivity.TAG, "ADD");
                        new SendMessage(getActivity(), Action.ADD, myDevice, null).execute(myDevice.getIp());
                    }
                });
        // Execute
        mContentView.findViewById(R.id.btn_exec).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(MainActivity.TAG, "EXEC");
                        new SendMessage(getActivity(), Action.EXEC, myDevice,null)
                                .execute(myDevice.getIp());
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

        // After the group negotiation, we assign the group owner
        if (info.groupFormed && info.isGroupOwner) {

            // Create server for the master device
            if (((MainActivity) getActivity()).masterServer == null) {

                Log.d(MainActivity.TAG, "MASTER >> Server socket created!");
                ((MainActivity) getActivity()).masterServer = new Servidor(Constants.SERVER_MASTER_PORT, getActivity());
                ((MainActivity) getActivity()).masterServer.createServer();
                ((MainActivity) getActivity()).masterServer.start();
                ((MainActivity) getActivity()).pool = new ConcurrentLinkedQueue<>();
            }
            //mContentView.findViewById(R.id.btn_add_slave).setVisibility(View.VISIBLE);
        } else if (info.groupFormed) {

            //Log.d(MainActivity.TAG, "ESCLAVOOOOOO");
            //Log.d(MainActivity.TAG, device.deviceAddress);

            new SendMessage(getActivity(), Action.CONNECT, myDevice, device).execute(info.groupOwnerAddress.toString().substring(1, info.groupOwnerAddress.toString().length()));

            /*mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));*/
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