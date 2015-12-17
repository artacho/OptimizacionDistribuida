package es.artacho.tfm.optimizaciondistribuida;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener {
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>(); // peers on WiFi P2P
    private List<Device> devices = new ArrayList<Device>(); // peers on list

    ProgressDialog progressDialog = null;
    View mContentView = null;

    private WifiP2pDevice device;
    private Device protocolDevice;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, devices));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_device_list, null);
        return mContentView;
    }
    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(MainActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Device protocolDevice = (Device) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(protocolDevice);
    }
    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    protected class WiFiPeerListAdapter extends ArrayAdapter<Device> {
        private List<Device> items;
        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                                   List<Device> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            Device protocolDevice = items.get(position);
            WifiP2pDevice device =  protocolDevice.getDevice();
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    //bottom.setText(getDeviceStatus(device.status));
                    bottom.setText(protocolDevice.getStatus().toString());
                }
            }
            return v;
        }
    }
    /**
     * Update UI for this device.
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        // Update protocolDevices according to WiFi state, update state, update insert and delete.

        boolean isFound = false;
        List<Device> changedDevices = new ArrayList<Device>();

        for (WifiP2pDevice newDevice : peers) {
            isFound = false;
            for (Device oldDevice : devices) {
                if (oldDevice.getDevice().deviceAddress.equals(newDevice.deviceAddress)) { // found device
                    isFound = true;
                    if (newDevice.status == 0 && oldDevice.getDevice().status != 0) { //connection
                        oldDevice.setDevice(newDevice);
                        oldDevice.setStatus(Status.WAIT); // change state on connect
                        changedDevices.add(oldDevice);
                        break;
                    } else if (newDevice.status != 0 && oldDevice.getDevice().status == 0) { // disconnection
                        oldDevice.setDevice(newDevice);
                        oldDevice.setStatus(Status.NSYNC); // change state on connect
                        changedDevices.add(oldDevice);
                    } else { // no changes
                        changedDevices.add(oldDevice);
                    }
                }
            }
            if (!isFound) {
                Device addDevice = new Device(newDevice, Status.NSYNC, "");
                changedDevices.add(addDevice);
            }

        }

        devices.clear();
        devices.addAll(changedDevices);

        // finish of updating

        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(MainActivity.TAG, "No devices found");
            return;
        }
    }
    public void clearPeers() {
        peers.clear();
        devices.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }
    /**
     *
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }
    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {
        void showDetails(Device device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}