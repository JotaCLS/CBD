package com.neo4j.lab;

import static org.neo4j.driver.Values.parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;



public class Neo4jLoader {
    public static void main(String[] args) {
                // Conectar ao Neo4j
        Neo4jConnector.connect();
        Driver driver = Neo4jConnector.getDriver();

        // Caminho do arquivo CSV
        String csvFilePath = "resources/spotify_tracks.csv";
        
        try (BufferedReader br = Files.newBufferedReader(Paths.get(csvFilePath))) {
            String line;
            // Ignorar o cabeçalho
            br.readLine();

            try (Session session = driver.session()) {
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");

                    String id = fields[0];
                    String name = fields[1];
                    String genre = fields[2];
                    String artists = fields[3];
                    String album = fields[4];
                    String popularity = fields[5];
                    String duration_ms = fields[6];
                    String explicit = fields[7];

                    // Iniciar uma transação explícita
                    try (Transaction tx = session.beginTransaction()) {
                        // Criar nó de Música
                        String createMusicQuery = "MERGE (m:Musica {id: $id, name: $name, genre: $genre, popularity: $popularity, duration_ms: $duration_ms, explicit: $explicit})";
                        tx.run(createMusicQuery, parameters("id", id, "name", name, "genre", genre, "popularity", popularity, "duration_ms", duration_ms, "explicit", explicit));

                        // Criar nó de Gênero
                        String createGenreQuery = "MERGE (g:Genero {name: $genre})";
                        tx.run(createGenreQuery, parameters("genre", genre));

                        // Criar nó de Artista
                        String[] artistList = artists.split(";");
                        for (String artist : artistList) {
                            String createArtistQuery = "MERGE (a:Artista {name: $artist})";
                            tx.run(createArtistQuery, parameters("artist", artist.trim()));

                            // Relacionar Artista com Música
                            String createArtistMusicRelation = "MATCH (m:Musica {id: $id}), (a:Artista {name: $artist}) MERGE (m)-[:INTERPRETADO_POR]->(a)";
                            tx.run(createArtistMusicRelation, parameters("id", id, "artist", artist.trim()));
                        }

                        // Criar nó de Álbum
                        String createAlbumQuery = "MERGE (al:Album {name: $album})";
                        tx.run(createAlbumQuery, parameters("album", album));

                        // Relacionar Álbum com Música
                        String createAlbumMusicRelation = "MATCH (m:Musica {id: $id}), (al:Album {name: $album}) MERGE (m)-[:PERTENCE_A]->(al)";
                        tx.run(createAlbumMusicRelation, parameters("id", id, "album", album));

                        // Commit da transação
                        tx.commit();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Fechar a conexão
            Neo4jConnector.close();
        }
    }
}
