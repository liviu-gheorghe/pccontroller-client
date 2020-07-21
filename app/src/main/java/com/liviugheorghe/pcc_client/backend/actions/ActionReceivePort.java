package com.liviugheorghe.pcc_client.backend.actions;

import com.liviugheorghe.pcc_client.backend.Action;
import com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes;

public class ActionReceivePort implements Action {


    private static final String EXTRA_PORT_NUMBER = "PORT_NUMBER";
    private String port;

    public ActionReceivePort(String port) {
        this.port = port;
    }

    @Override
    public void execute() {

    }

    @Override
    public int getActionType() {
        return ReceivedActionsCodes.RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION;
    }
}
