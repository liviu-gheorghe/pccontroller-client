package com.liviugheorghe.pcc_client.backend;

import com.liviugheorghe.pcc_client.backend.actions.*;

import java.io.InputStream;

import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.*;

public class ActionFactory {

    public static Action createAction(int type, String content) {
        if (type == RECEIVE_HOSTNAME) return new ActionReceiveHostname(content);
        if (type == RECEIVE_PING) return new ActionReceivePing(content);
        if (type == RING_DEVICE) return new ActionRingDevice(content);
        if (type == RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION)
            return new ActionReceivePort(content);
        return null;
    }

    public static Action createAction(int type, InputStream inputStream) {
        if (type == RECEIVE_FILE) return new ActionReceiveFile(inputStream);
        return null;
    }
}
