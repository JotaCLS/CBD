package org.example;

import com.datastax.oss.driver.api.core.CqlSession;

public class MainApp {
    public static void main(String[] args) {
        CassandraConnector connector = new CassandraConnector();

        try {
            String datacenter = "datacenter1";

            connector.connect("127.0.0.1", 9042, datacenter);

            CqlSession session = connector.getSession();

            Insert.insertVideo(session);

            Update.updateTags(session);

            Search.selectVideos(session);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.close();
        }
    }
}
