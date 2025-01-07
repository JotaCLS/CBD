package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Scanner;

import java.util.ArrayList;

public class CBD_24b {

    public static int timer = 120;
    public static int max_quantity = 3;

    public static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    public static MongoDatabase database = mongoClient.getDatabase("atendimento");

    public static AtomicInteger QuantidadeAdicionada = new AtomicInteger(0);

    public static void main(String[] args) {
        Timer timerInstance = new Timer();
        TimerTask resetProductCounterTask = new TimerTask() {
            @Override
            public void run() {
                QuantidadeAdicionada.set(0);
            }
        };
        timerInstance.scheduleAtFixedRate(resetProductCounterTask, timer * 1000, timer * 1000);

        while(true){
            System.out.println("Username: ");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.nextLine();
            opcoes(username);
            int opcao = scanner.nextInt();
            while(true){
                if(opcao == 1){
                    System.out.println("Produto: ");
                    String produto_nome = scanner.next();
                    System.out.println("Quantidade: ");
                    int quantidade = scanner.nextInt();
                    if(quantidade == 0){
                        System.out.println("Quantidade inválida");
                        continue;
                    }
                    if(QuantidadeAdicionada.get() + quantidade <= max_quantity){
                        MongoCollection<Document> collection = getCollection(username);
                        Document produto = new Document("nome", produto_nome).append("quantidade", quantidade);
                        collection.insertOne(produto);
                        QuantidadeAdicionada.addAndGet(quantidade);
                    } else {
                        System.out.println("\nLimite de quantidade de produtos atingido, quantidade possivel: " + (max_quantity - QuantidadeAdicionada.get()) + " produtos\n");
                    }
                }else if(opcao == 2){
                    Document projection = new Document("nome", 1)
                        .append("quantidade", 1)
                        .append("_id", 0);
                    MongoCollection<Document> collection = getCollection(username);
                    for (Document doc : collection.find().projection(projection)) {
                        System.out.println(doc.toJson());
                    }
            }else if(opcao == 3){
                break;
            }else{
                System.out.println("Opção inválida");
            }
            opcoes(username);
            opcao = scanner.nextInt();
        }
        mongoClient.close();
        System.exit(0);
    }   
}

    public static void opcoes(String username){
        System.out.println("\nBem vindo " + username);
        System.out.println("1 - Adicionar produto");
        System.out.println("2 - Ver produtos adicionados");
        System.out.println("3 - Sair\n");

    }

    public static MongoCollection<Document> getCollection(String utilizador){
        String collectionName = utilizador + "_produtos";
        if(!database.listCollectionNames().into(new ArrayList<String>()).contains(collectionName)){
            database.createCollection(collectionName);
            return database.getCollection(collectionName);
        }

        return database.getCollection(collectionName);
    }

}
