package org.example;

import com.datastax.oss.driver.api.core.CqlSession;

public class Insert {

    public static void insertVideo(CqlSession session) {
        String query = "INSERT INTO partilha_de_videos.videos_by_user (author, upload_timestamp, video_id, descricao, nome_video, tags) " +
                "VALUES ('user1', '2024-01-01 10:00:00', 124, 'tutorial de java sobre cassandra', 'tutorial java', {'tutorial', 'java'});";
        session.execute(query);
        System.out.println("Success on insertion");
    }
}
