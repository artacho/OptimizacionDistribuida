package es.artacho.tfm.optimizaciondistribuida;

import java.io.Serializable;

/**
 * Created by Pablo on 12/15/2015.
 */
public class Message implements Serializable {
    private String message;

    public Message (String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
