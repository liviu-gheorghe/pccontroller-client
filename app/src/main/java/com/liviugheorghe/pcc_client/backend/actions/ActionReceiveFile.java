package com.liviugheorghe.pcc_client.backend.actions;



import com.liviugheorghe.pcc_client.backend.Action;
import com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes;

import java.io.InputStream;

public class ActionReceiveFile implements Action {

    private InputStream inputStream;

    public ActionReceiveFile(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void execute() {

    }

    @Override
    public int getActionType() {
        return ReceivedActionsCodes.RECEIVE_FILE;
    }
}
