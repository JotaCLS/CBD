package org.example;

import com.datastax.oss.driver.api.core.CqlSession;

public class Update {

    public static void updateTags(CqlSession session) {
        String query = "UPDATE partilha_de_videos.videos_by_user " +
                "SET tags = tags + {'cassandra'} WHERE author = 'user1' AND upload_timestamp = '2024-01-01 10:00:00' AND video_id = 124;";
        session.execute(query);
        System.out.println("success on update");
    }
}
