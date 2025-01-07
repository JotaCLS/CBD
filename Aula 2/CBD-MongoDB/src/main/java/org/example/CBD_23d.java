package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class CBD_23d {
        public static String restaurant = "Park";
    
        public static void main(String[] args) {
            System.out.println("Numero de localidades distintas: " + countLocalidades());
            System.out.println("Numero de restaurantes por localidade: " + countRestByLocalidade());
            System.out.println("Restaurantes com nome mais proximo de " + restaurant + ": " + getRestWithNameCloserTo(restaurant));
        
        
    }

    public static int countLocalidades(){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("CBD");
        MongoCollection<Document> collection = database.getCollection("restaurants");
        
        Set<String> localidades = new HashSet<>();
        for (Document doc : collection.find()) {
            localidades.add(doc.getString("localidade"));
        }
        System.out.println(localidades);

        mongoClient.close();

        return localidades.size();
    }

    // Numero de restaurantes por localidade
    public static Map<String, Integer> countRestByLocalidade()  {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("CBD");
        MongoCollection<Document> collection = database.getCollection("restaurants");
        
        Map<String, Integer> localidades = new HashMap<>();
        for (Document doc : collection.find()) {
            String localidade = doc.getString("localidade");
            if (localidades.containsKey(localidade)) {
                localidades.put(localidade, localidades.get(localidade) + 1);
            } else {
                localidades.put(localidade, 1);
            }
        }

        mongoClient.close();

        return localidades;
    }

    // Nome de restaurantes contendo a string no nome
    public static List<String> getRestWithNameCloserTo(String name) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("CBD");
        MongoCollection<Document> collection = database.getCollection("restaurants");
        
        List<String> restaurantes = new ArrayList<>();

        for (Document doc : collection.find(Filters.regex("nome", name, "i"))){ // i tanto faz lowercase ou uppercase
            restaurantes.add(doc.getString("nome"));
        }

        mongoClient.close();

        return restaurantes;
        
    }
}
