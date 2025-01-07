package org.example;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CBD_23b {

    public static void main(String[] args) {
        MongoCollection<Document> collection = getCollection();

        // Cria os índices
        createIndexes(collection);

        // Pesquisa por localidade
        searchByLocalidade(collection, "Aveiro");

        // Pesquisa por gastronomia
        searchByGastronomia(collection, "African");

        // Pesquisa por nome
        searchByNome(collection, "Restaurant");
    }


    private static MongoCollection<Document> getCollection() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("CBD");
        return database.getCollection("restaurants");
    }



    private static void createIndexes(MongoCollection<Document> collection) {
        collection.createIndex(new Document("localidade", 1)); 
        collection.createIndex(new Document("gastronomia", 1)); 
        collection.createIndex(new Document("nome", "text")); 
    }

    

    // pesquisar por localidade e apenas mostrar a localidade e o nome -- 110ms sem index --32ms com index
    private static void searchByLocalidade(MongoCollection<Document> collection, String localidade) {
        long start = System.currentTimeMillis();
        Document query = new Document("localidade", localidade);
        Document projection = new Document("localidade", 1).append("nome", 1); // retorna apenas localidade e nome
        for (Document doc : collection.find(query).projection(projection)) {
            System.out.println(doc.toJson());
        }
        long end = System.currentTimeMillis();
        System.out.println("Localidade search took " + (end - start) + "ms");
    }

    // pesquisar por gastronomia e apenas mostrar a gastronomia e o nome -- 10ms sem index -- 13 ms com index ?
    private static void searchByGastronomia(MongoCollection<Document> collection, String gastronomia) {
        long start = System.currentTimeMillis();
        Document query = new Document("gastronomia", gastronomia);
        Document projection = new Document("gastronomia", 1).append("nome", 1); // retorna apenas gastronomia e nome
        for (Document doc : collection.find(query).projection(projection)) {
            System.out.println(doc.toJson());
        }
        long end = System.currentTimeMillis();
        System.out.println("Gastronomia search took " + (end - start) + "ms");
    }

    // pesquisar por nome -- 494 ms sem index -- 460 ms com index
    private static void searchByNome(MongoCollection<Document> collection, String nomeRegex) {
        long start = System.currentTimeMillis();
        Document query = new Document("nome", new Document("$regex", nomeRegex));
        Document projection = new Document("nome", 1).append("localidade", 1); // retorna apenas nome e localidade
        for (Document doc : collection.find(query).projection(projection)) {
            System.out.println(doc.toJson());
        }
        long end = System.currentTimeMillis();
        System.out.println("Nome search took " + (end - start) + "ms");
    }

}
