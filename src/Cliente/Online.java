/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Gabriel Schenato <gabriel@uniplaclages.edu.br>
 */
public class Online implements Serializable {

    private ArrayList<String> clientesOnline = new ArrayList();

    public ArrayList<String> getClientesOnline() {
        return clientesOnline;
    }

    public void setClientesOnline(ArrayList<String> clientesOnline) {
        this.clientesOnline = clientesOnline;
    }

    public String[] getClientesOnlineArray() {
        String[] clientesOnlineArray = new String[clientesOnline.size()];
        clientesOnlineArray = clientesOnline.toArray(clientesOnlineArray);
        return clientesOnlineArray;
    }

}
