package es.artacho.tfm.optimizaciondistribuida;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Pablo on 12/17/2015.
 */
public class Device {
    private WifiP2pDevice device;
    private Status status;
    private String ip;

    public Device (WifiP2pDevice device, Status status, String ip) {
        this.device = device;
        this.status = status;
        this.ip = ip;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String toString () {
        return device.deviceName;
    }

}
