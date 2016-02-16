package oldClasses;

import java.io.Serializable;

import es.artacho.tfm.optimizaciondistribuida.Action;

/**
 * Created by Pablo on 1/11/2016.
 */
public class Data implements Serializable {
    private Action action; // Action
    private String data; // Data to send
    private int role; // Master or slave
    private String MAC; // MAC address
    private boolean ack;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }
}
