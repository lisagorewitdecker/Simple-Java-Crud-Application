package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for managing the lifecycle of Java RMI registries and preventing port binding conflicts.
 */
public final class RmiServerManager {

    private static final Logger log = LoggerFactory.getLogger(RmiServerManager.class);

    private static final Map<Integer, Registry> REGISTRIES = new ConcurrentHashMap<>();

    private RmiServerManager() {
        super();
        // Utility class
    }

    /**
     * Gets an existing locally created registry or creates a new RMI registry on the specified port.
     *
     * @param port the port for the registry (1 to 65535)
     * @return the RMI registry instance
     * @throws IllegalArgumentException if the port is outside the valid range 1-65535
     * @throws RemoteException if the registry cannot be created or accessed
     */
    public static synchronized Registry getOrCreateRegistry(int port) throws RemoteException {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port number must be between 1 and 65535: " + port);
        }
        Registry registry = REGISTRIES.get(port);
        if (registry != null) {
            return registry;
        }

        try {
            registry = LocateRegistry.createRegistry(port);
            REGISTRIES.put(port, registry);
            log.info("Created and registered local RMI registry on port {}", port);
            return registry;
        } catch (RemoteException e) {
            throw new RemoteException("Failed to create RMI registry on port " + port + "; port does not host an operational RMI registry: " + e.getMessage(), e);
        }
    }

    /**
     * Returns an unmodifiable view of locally created and tracked registries.
     *
     * @return map of port to Registry
     */
    public static Map<Integer, Registry> getLocallyCreatedRegistries() {
        return Collections.unmodifiableMap(REGISTRIES);
    }

    /**
     * Unexports a locally tracked RMI registry.
     *
     * @param registry the registry to unexport
     * @param force    if true, unexports even if pending calls exist
     * @return true if successfully unexported, false otherwise
     */
    public static synchronized boolean unexportRegistry(Registry registry, boolean force) {
        if (registry == null) {
            return false;
        }

        Integer portToRemove = null;
        for (Map.Entry<Integer, Registry> entry : REGISTRIES.entrySet()) {
            if (entry.getValue() == registry) {
                portToRemove = entry.getKey();
                break;
            }
        }

        if (portToRemove == null) {
            return false;
        }

        try {
            boolean success = UnicastRemoteObject.unexportObject(registry, force);
            if (success) {
                REGISTRIES.remove(portToRemove);
                log.info("Unexported local RMI registry on port {}", portToRemove);
            }
            return success;
        } catch (NoSuchObjectException e) {
            REGISTRIES.remove(portToRemove);
            return false;
        }
    }

    /**
     * Unexports a locally tracked RMI registry by port.
     *
     * @param port  the port of the registry to unexport
     * @param force if true, unexports even if pending calls exist
     * @return true if successfully unexported, false otherwise
     */
    public static synchronized boolean unexportRegistry(int port, boolean force) {
        if (port < 1 || port > 65535) {
            return false;
        }
        Registry registry = REGISTRIES.get(port);
        if (registry == null) {
            return false;
        }
        return unexportRegistry(registry, force);
    }

    /**
     * Unexports all locally tracked RMI registries.
     *
     * @param force if true, unexports even if pending calls exist
     */
    public static synchronized void unexportAllRegistries(boolean force) {
        REGISTRIES.entrySet().removeIf(entry -> {
            try {
                boolean success = UnicastRemoteObject.unexportObject(entry.getValue(), force);
                if (success) {
                    log.info("Unexported local RMI registry on port {}", entry.getKey());
                }
                return success;
            } catch (NoSuchObjectException ignored) {
                // Already unexported
                return true;
            }
        });
    }

    /**
     * Configures serialization security filters for RMI.
     */
    public static void configureSecurityFilters() {
        if (System.getProperty("jdk.serialFilter") == null) {
            System.setProperty("jdk.serialFilter", "maxbytes=1048576;maxdepth=10;maxarray=10000;maxrefs=500");
            log.info("Configured default jdk.serialFilter security property");
        }
    }
}
