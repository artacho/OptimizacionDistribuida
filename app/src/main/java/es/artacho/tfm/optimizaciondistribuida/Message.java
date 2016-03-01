package es.artacho.tfm.optimizaciondistribuida;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v4.util.Pools;

import java.io.Serializable;

import es.artacho.tfm.optimizaciondistribuida.ga.Individual;

/**
 * Created by Pablo on 12/15/2015.
 */
public class Message implements Serializable {
    private PoolDevice device; // Master device
    private String address; // MAC of sender device
    private String message; // Data message
    private Action action; // Message in FSM
    private boolean flag; // ACK

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public PoolDevice getReceiver() {
        return receiver;
    }

    public void setReceiver(PoolDevice receiver) {
        this.receiver = receiver;
    }

    private Individual individual;
    private PoolDevice receiver;

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

    public PoolDevice getDevice() {
        return device;
    }

    public void setDevice(PoolDevice device) {
        this.device = device;
    }

    public String toString() {
        return message;
    }
}
