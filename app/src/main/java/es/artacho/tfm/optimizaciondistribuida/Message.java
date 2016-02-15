package es.artacho.tfm.optimizaciondistribuida;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;

/**
 * Created by Pablo on 12/15/2015.
 */
public class Message implements Serializable {
    private WifiP2pDevice device; // Master device
    private String address; // MAC of sender device
    private String message; // Data message
    private Action action; // Message in FSM
    private boolean flag; // ACK

    public Message (String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String toString() {
        return message;
    }
}
