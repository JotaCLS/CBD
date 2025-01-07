package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class Search {

    public static void selectVideos(CqlSession session) {
        String query = "SELECT * FROM partilha_de_videos.videos_by_user WHERE author = 'user1';";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("Author: " + row.getString("author"));
            System.out.println("Video ID: " + row.getInt("video_id"));
            System.out.println("Description: " + row.getString("descricao"));
            System.out.println("Video Name: " + row.getString("nome_video"));
            System.out.println("Tags: " + row.getSet("tags", String.class));
            System.out.println("Upload Timestamp: " + row.getInstant("upload_timestamp"));
            System.out.println("----------------------");
        }
    }
}
