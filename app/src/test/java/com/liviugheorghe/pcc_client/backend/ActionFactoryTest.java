package com.liviugheorghe.pcc_client.backend;

import com.liviugheorghe.pcc_client.backend.actions.ActionReceiveHostname;
import com.liviugheorghe.pcc_client.backend.actions.ActionReceivePing;
import com.liviugheorghe.pcc_client.backend.actions.ActionReceivePort;
import com.liviugheorghe.pcc_client.backend.actions.ActionRingDevice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_HOSTNAME;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_PING;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION;
import static com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes.RING_DEVICE;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("Tests for ActionFactory")
public class ActionFactoryTest {


    public String actionContent = "action content";


    @Test
    @DisplayName("When ActionReceiveHostname is created the action type should be RECEIVE_HOSTNAME")
    public void testActionReceiveHostname() {
        ActionReceiveHostname action = (ActionReceiveHostname) ActionFactory.createAction(RECEIVE_HOSTNAME, actionContent);
        assert (action != null);
        assertEquals(action.getActionType(), RECEIVE_HOSTNAME);
    }

    @Test
    @DisplayName("When ActionReceiveHostname is created the action type should be RECEIVE_PING")
    public void testActionReceivePing() {
        ActionReceivePing action = (ActionReceivePing) ActionFactory.createAction(RECEIVE_PING, actionContent);
        assert (action != null);
        assertEquals(action.getActionType(), RECEIVE_PING);
    }

    @Test
    @DisplayName("When ActionReceivePort is created the action type should be RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION")
    public void testActionReceivePort() {
        ActionReceivePort action = (ActionReceivePort) ActionFactory.createAction(RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION, actionContent);
        assert (action != null);
        assertEquals(action.getActionType(), RECEIVE_PORT_NUMBER_FOR_FILE_TRANSMISSION);
    }

    @Test
    @DisplayName("When ActionRingDevice is created the action type should be RING_DEVICE")
    public void testActionRingDevice() {
        ActionRingDevice action = (ActionRingDevice) ActionFactory.createAction(RING_DEVICE, actionContent);
        assert (action != null);
        assertEquals(action.getActionType(), RING_DEVICE);
    }
}
