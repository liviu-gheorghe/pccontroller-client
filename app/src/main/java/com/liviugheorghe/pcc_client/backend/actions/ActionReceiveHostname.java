package com.liviugheorghe.pcc_client.backend.actions;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Action;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes;

public class ActionReceiveHostname implements Action {

    private String hostname;
    private Client client;

    public ActionReceiveHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void execute() {

        new Thread(
                () -> {
                    App.CONNECTED_DEVICE_HOSTNAME = hostname;
                    try {
                        //TODO
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    @Override
    public int getActionType() {
        return ReceivedActionsCodes.RECEIVE_HOSTNAME;
    }
}
