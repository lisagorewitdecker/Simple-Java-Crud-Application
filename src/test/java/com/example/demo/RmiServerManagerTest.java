package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

class RmiServerManagerTest {

    @AfterEach
    void tearDown() {
        RmiServerManager.unexportAllRegistries(true);
    }

    @Test
    void testGetOrCreateRegistryAndUnexport() throws RemoteException {
        int testPort = 11099;
        Registry registry = RmiServerManager.getOrCreateRegistry(testPort);
        assertNotNull(registry, "Registry should not be null");
        assertTrue(RmiServerManager.getLocallyCreatedRegistries().containsKey(testPort),
                "Locally created registry should be tracked to avoid GC");

        // Calling again on the same port should reuse without throwing exception
        Registry secondRegistry = RmiServerManager.getOrCreateRegistry(testPort);
        assertSame(registry, secondRegistry, "Second registry call should return the locally tracked instance");

        // Clean up
        boolean unexported = RmiServerManager.unexportRegistry(registry, true);
        assertTrue(unexported, "Registry should unexport cleanly");
        assertFalse(RmiServerManager.getLocallyCreatedRegistries().containsKey(testPort),
                "Unexported registry should be removed from tracking");
    }

    @Test
    void testUnexportByPort() throws RemoteException {
        int testPort = 11100;
        Registry registry = RmiServerManager.getOrCreateRegistry(testPort);
        assertNotNull(registry);

        boolean unexported = RmiServerManager.unexportRegistry(testPort, true);
        assertTrue(unexported, "Registry should unexport cleanly by port");
        assertFalse(RmiServerManager.unexportRegistry(testPort, true), "Subsequent unexport on same port should return false");
    }

    @Test
    void testUnexportAllRegistries() throws RemoteException {
        int port1 = 11101;
        int port2 = 11102;
        RmiServerManager.getOrCreateRegistry(port1);
        RmiServerManager.getOrCreateRegistry(port2);

        assertEquals(2, RmiServerManager.getLocallyCreatedRegistries().size());
        RmiServerManager.unexportAllRegistries(true);
        assertTrue(RmiServerManager.getLocallyCreatedRegistries().isEmpty(),
                "All locally tracked registries should be unexported and removed");
    }

    @Test
    void testUnexportUntrackedStubReturnsFalse() throws RemoteException {
        Registry externalStub = LocateRegistry.getRegistry(11103);
        assertFalse(RmiServerManager.unexportRegistry(externalStub, true),
                "Attempting to unexport an untracked/client stub should safely return false");
    }

    @Test
    void testUnexportNullRegistry() {
        assertFalse(RmiServerManager.unexportRegistry(null, true));
    }

    @Test
    void testPortOccupiedByNonRmiServiceThrowsRemoteException() throws IOException {
        int nonRmiPort = 11104;
        try (ServerSocket serverSocket = new ServerSocket(nonRmiPort)) {
            RemoteException ex = assertThrows(RemoteException.class, () -> {
                RmiServerManager.getOrCreateRegistry(nonRmiPort);
            });
            assertTrue(ex.getMessage().contains("does not host an operational RMI registry") ||
                    ex.getMessage().contains("Failed to create"),
                    "Exception message should indicate port failure and non-operational RMI registry");
        }
    }

    @Test
    void testInvalidPortThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RmiServerManager.getOrCreateRegistry(0));
        assertThrows(IllegalArgumentException.class, () -> RmiServerManager.getOrCreateRegistry(-1));
        assertThrows(IllegalArgumentException.class, () -> RmiServerManager.getOrCreateRegistry(70000));
        assertFalse(RmiServerManager.unexportRegistry(0, true));
        assertFalse(RmiServerManager.unexportRegistry(-1, true));
        assertFalse(RmiServerManager.unexportRegistry(70000, true));
    }

    @Test
    void testSecurityFiltersConfigured() {
        RmiServerManager.configureSecurityFilters();
        assertNotNull(System.getProperty("jdk.serialFilter"),
                "jdk.serialFilter system property should be configured");
    }
}
