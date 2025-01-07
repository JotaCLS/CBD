package org.example;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;


public class CBD_23a {
    public static void main(String[] args) { 
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        System.out.println("Connected to MongoDB!");

        MongoDatabase database = mongoClient.getDatabase("CBD"); 
        System.out.println("Connected to database: " + database.getName());

        MongoCollection<Document> collection = database.getCollection("restaurants"); 
        System.out.println("Connected to collection: " + collection.getNamespace().getCollectionName());

        long count = collection.countDocuments();
        System.out.println("Number of documents in the collection: " + count);

        // SEARCH

        Document query = new Document("nome", "CUA");
        Document result = collection.find(query).first();
        System.out.println("Document found: " + result.toJson());

        
        // INSERT
        if (result.isEmpty()){
            Document document = new Document("nome", "CUA")
                .append("gastronomia", "snack-bar")
                .append("localidade", "Aveiro")
                .append("address", new Document("rua", "Rua do CUA")
                    .append("coordenadas", new Document("latitude", 40.6441)
                        .append("longitude", -8.6455)))
                    .append("zipcode", "3810-193")
                    .append("building", "catacumbas")
                .append("grades", new Document("date", "2021-06-01")
                    .append("grade", "A")
                    .append("score", 10));
                
            collection.insertOne(document);
            System.out.println("Document inserted!" + document.toJson());
        }

        // UPDATE

        Document querytoUpdate = new Document("nome", "CUA"); // query to update
        Document update = new Document("$set", new Document("gastronomia", "restaurante")); // update to apply  
        collection.updateOne(querytoUpdate, update);

        // SEARCH UPDATED DOCUMENT

        Document queryUpdated = new Document("nome", "CUA");
        Document resultUpdated = collection.find(queryUpdated).first();
        System.out.println("Document found: " + resultUpdated.toJson());


        mongoClient.close();

    }
}