package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;


public class Exercicio33B {

    public static void main(String[] args) {
        CassandraConnector connector = new CassandraConnector();

        try {
            String datacenter = "datacenter1";

            connector.connect("127.0.0.1", 9042, datacenter);

            CqlSession session = connector.getSession();

            selectVideosByTag(session);
            selectVideosByUser(session);
            selectTotalCommentsByVideo(session);
            selectFollowersByVideo(session);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.close();
        }
    }

    // Querie 3 - Todos os vídeos com a tag Aveiro;
    // SELECT * FROM partilha_de_videos.videos_by_tag WHERE tag = 'Aveiro';
    public static void selectVideosByTag(CqlSession session) {
        String query = "SELECT * FROM partilha_de_videos.videos_by_tag WHERE tag = 'Aveiro';";
        ResultSet resultSet = session.execute(query);
        // um ResultSet é um conjunto de resultados de uma query, que pode ser iterado para obter os resultados individuais

        System.out.println(resultSet);

        for (Row row : resultSet) {
            
            System.out.println("Video ID: " + row.getInt("video_id"));
            System.out.println("Tags: " + row.getString("tag"));
            System.out.println("----------------------");
        }
    }

    // Querie 5 - Vídeos partilhados por determinado utilizador (maria1987, por exemplo) num 
    // SELECT * FROM partilha_de_videos.videos_by_user WHERE author = 'user1' and upload_timestamp>= '2017-08-01 00:00:00+0000' AND upload_timestamp <= '2017-08-31 23:59:59+0000';
    public static void selectVideosByUser(CqlSession session) {
        String query = "SELECT * FROM partilha_de_videos.videos_by_user WHERE author = 'user1' and upload_timestamp>= '2017-08-01 00:00:00+0000' AND upload_timestamp <= '2017-08-31 23:59:59+0000';";
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

    // Querie 12 - Numero total de comentários por video
    // SELECT video_id, count(*) as num_comentarios from partilha_de_videos.comments group by video_id;
    public static void selectTotalCommentsByVideo(CqlSession session) {
        String query = "SELECT video_id, count(*) as num_comentarios from partilha_de_videos.comments group by video_id;";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("Video ID: " + row.getInt("video_id"));
            System.out.println("Total Comments: " + row.getLong("num_comentarios"));
            System.out.println("----------------------");
        }
    }
    
    // Querie 7 - Todos os seguidores (followers) de determinado vídeo;
    // SELECT * FROM partilha_de_videos.video_followers WHERE video_id = 101;
    public static void selectFollowersByVideo(CqlSession session) {
        String query = "SELECT * FROM partilha_de_videos.video_followers WHERE video_id = 101;";
        ResultSet resultSet = session.execute(query);

        for (Row row : resultSet) {
            System.out.println("Video ID: " + row.getInt("video_id"));
            System.out.println("Follower: " + row.getString("follower_username"));
            System.out.println("Follow Timestamp: " + row.getInstant("follow_timestamp"));
            System.out.println("----------------------");
        }
    }
}
