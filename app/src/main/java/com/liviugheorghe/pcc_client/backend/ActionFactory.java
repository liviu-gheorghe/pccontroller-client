package com.liviugheorghe.pcc_client.backend;

import com.liviugheorghe.pcc_client.backend.actions.ActionReceiveHostname;
import com.liviugheorghe.pcc_client.backend.actions.ActionReceivePing;
import com.liviugheorghe.pcc_client.backend.actions.ActionReceivePort;
import com.liviugheorghe.pcc_client.backend.actions.ActionRingDevice;

import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_HOSTNAME;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_PING;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RING_DEVICE;

public class ActionFactory {

    public static Action createAction(int type, String content) {
        if (type == RECEIVE_HOSTNAME) return new ActionReceiveHostname(content);
        if (type == RECEIVE_PING) return new ActionReceivePing(content);
        if (type == RING_DEVICE) return new ActionRingDevice(content);
        if (type == RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION)
            return new ActionReceivePort(content);
        return null;
    }
}
