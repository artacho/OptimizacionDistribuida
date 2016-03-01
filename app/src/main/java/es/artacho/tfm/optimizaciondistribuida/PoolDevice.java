package es.artacho.tfm.optimizaciondistribuida;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;

/**
 * Created by Pablo on 01/03/2016.
 */
public class PoolDevice implements Serializable{
    public String deviceAddress;
    public String deviceName;
    private Status status;
    private String ip;

    public PoolDevice (String deviceAddress, String deviceName, Status status, String ip) {
        this.deviceAddress = deviceAddress;
        this.deviceName = deviceName;
        this.status = status;
        this.ip = ip;
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
        return deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
