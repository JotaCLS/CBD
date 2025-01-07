package org.example;

import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;

public class CassandraConnector {
    private CqlSession session;

    public void connect(String node, int port, String datacenter) {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(node, port))
                .withLocalDatacenter(datacenter) 
                .build();
    }

    public CqlSession getSession() {
        return session;
    }

    public void close() {
        if (session != null) {
            session.close();
        }
    }
}
