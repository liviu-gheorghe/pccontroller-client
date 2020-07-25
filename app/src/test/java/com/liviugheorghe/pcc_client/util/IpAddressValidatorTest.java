package com.liviugheorghe.pcc_client.util;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Tests for IPAddressValidator")
public class IpAddressValidatorTest {


    @Test
    @DisplayName("When null is passed should return false")
    public void withNull_shouldReturnFalse() {
        assertFalse(IpAddressValidator.isLocalIpAddress(null));
    }

    @Test
    @DisplayName("When empty string is passed should return false")
    public void withEmptyString_shouldReturnFalse() {
        assertFalse(IpAddressValidator.isLocalIpAddress(""));
    }

    @Test
    @DisplayName("When public ip is passed should return false")
    public void withPublicIp_shouldReturnFalse() {
        assertFalse(IpAddressValidator.isLocalIpAddress("87.55.123.22"));
    }

    @Test
    @DisplayName("When random long string is passed should return false")
    public void withVeryLongString_shouldReturnFalse() {
        assertFalse(IpAddressValidator.isLocalIpAddress("jCYJK7ic0uk0Tw0H07xaA92VByIa7uEdXMC3XcMlFiN2IO0zMUCHeFK41xIz"));
    }


    @Test
    @DisplayName("When class A local IP is passed should return false")
    public void withClassALocalIp_shouldReturnTrue() {
        assertTrue(IpAddressValidator.isLocalIpAddress("10.12.14.234"));
    }

    @Test
    @DisplayName("When class B local IP is passed should return false")
    public void withClassBLocalIp_shouldReturnTrue() {
        assertTrue(IpAddressValidator.isLocalIpAddress("192.168.1.3"));
    }

    @Test
    @DisplayName("When class C local IP is passed should return false")
    public void withClassCLocalIp_shouldReturnTrue() {
        assertTrue(IpAddressValidator.isLocalIpAddress("172.16.0.23"));
    }

}
