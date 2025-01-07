package org.example;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;
import java.util.Date;


public class CBD_23c {

    public static void main(String[] args) {
        MongoCollection<Document> collection = getCollection();

        totalNumGrades(collection);
        searchByGastronomiaEqualToAmericanOrChinese(collection);
        totalNumGradesByDay(collection);
        avgScoreAndNumGrades(collection);
        nomeLocalidadeMaiorAvaliacao(collection);


        
    }

    private static MongoCollection<Document> getCollection() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("CBD");
        return database.getCollection("restaurants");
    }


    // Questao 19 - listar o numero total de avaliacoes na collection 
    // Nao preciso de usar o unwind como fiz no shell
    public static void totalNumGrades(MongoCollection collection){
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
            new Document("$count", "numGrades")
        ));

        for (Document doc : result) {
            System.out.println("Total de avaliações (numGrades): " + doc.get("numGrades"));
        }
    }


    //  Questao 11. Liste o nome, a localidade e a gastronomia dos restaurantes que pertencem ao Bronx e cuja gastronomia é do tipo "American" ou "Chinese". 
    public static void searchByGastronomiaEqualToAmericanOrChinese(MongoCollection<Document> collection) {
        FindIterable<Document> result =collection.find(new Document("$or", Arrays.asList(
            new Document("gastronomia", "American"),
            new Document("gastronomia", "Chinese")
        )));

        // projeçao do que é para mostrar
        Document projection = new Document("nome", 1)
                                      .append("localidade", 1)
                                      .append("gastronomia", 1)
                                      .append("_id", 0);

        for (Document doc : result.projection(projection)) {
            System.out.println(doc.toJson());
        }

    }

    // Apresente o número total de avaliações (numGrades) em cada dia da semana.
    public static void totalNumGradesByDay(MongoCollection<Document> collection) {
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
            new Document("$unwind", "$grades"),
            new Document("$project", new Document("dayOfWeek", new Document("$dayOfWeek", "$grades.date"))),
            new Document("$group", new Document("_id", "$dayOfWeek").append("numGrades", new Document("$sum", 1))),
            new Document("$sort", new Document("_id", 1))
        ));

        
        for (Document doc : result) {
            System.out.println(doc.toJson());
        }

        
    }

    // Apresente o nome e o score médio (avgScore) e número de avaliações (numGrades) dos restaurantes com score médio superior a 30 desde 1-Jan-2014
    public static void avgScoreAndNumGrades(MongoCollection<Document> collection) {
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
            new Document("$unwind", "$grades"),
            new Document("$match", new Document("grades.date", new Document("$gte", new Date(114, 0, 1)))),
            new Document("$group", new Document("_id", "$nome")
                .append("avgScore", new Document("$avg", "$grades.score"))
                .append("numGrades", new Document("$sum", 1))
            ),
            new Document("$match", new Document("avgScore", new Document("$gt", 30))),
            new Document("$project", new Document("_id", 0)
                .append("nome", "$_id")
                .append("avgScore", 1)
                .append("numGrades", 1)
            )
        ));

        for (Document doc : result) {
            System.out.println(doc.toJson());
        }

    }

    // Pergunta feita por mim - Apresente o nome, localidade e a maior avaliação dos restaurantes com mais de 4 avaliacoes
    public static void nomeLocalidadeMaiorAvaliacao(MongoCollection<Document> collection) {
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
            new Document("$project", new Document("nome", 1)
                .append("localidade", 1)
                .append("grades", 1)
                .append("numGrades", new Document("$size", "$grades"))
            ),
            new Document("$match", new Document("numGrades", new Document("$gt", 4))),
            new Document("$project", new Document("nome", 1)
                .append("localidade", 1)
                .append("_id", 0)
                .append("maiorScore", new Document("$max", "$grades.score"))
            )
        ));

        for (Document doc : result) {
            System.out.println(doc.toJson());
        }

    }

}
