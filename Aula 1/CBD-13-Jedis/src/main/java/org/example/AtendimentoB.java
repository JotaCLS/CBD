package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Scanner;
import java.time.Instant;

// Processo
// Cada utilizador vai ter um zset associado a si com os produtos que pediu 
// Cada produto vai ter um score associado à quantidade pedida
// Quando um utilizador pede um produto, é pedido o zset do utilizador
// Verifica-se se nesse zset a quantidade de produtos pedidos é maior que o "limite"
// Se tiver excedido nao aceita o pedido
// Se nao adiciona o pedido e a quantidade ao zset do utilizador

// Similar ao anterior mas em vez do tempo ser o score, a quantidade é o score

public class AtendimentoB {
    
    public static String ATENDIMENTO_KEY = "atendimento:"; // Key set for users' name
    public static int limite = 3;
    public static int timeslot = 60*60;

    public static void main(String[] args) {
        Jedis jedis = new Jedis();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nEnter username ('Enter' to exit): ");
            String username = scanner.nextLine();

            if (username.isEmpty()) {
                break;
            }

            System.out.print("\nEnter product: ");
            String product = scanner.nextLine();

            System.out.println("\nEnter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); 


            // Processar o pedido
            boolean allowed = processRequest(jedis, username, product, quantity);

            if (allowed) {
                System.out.println("Request added successfully.");
            } else {
                System.out.println("Error: You have exceeded the limit of " + limite + " products in the last " + (timeslot / 60) + " minutes.");
            }
        }

        jedis.close();
    }

    private static boolean processRequest(Jedis jedis, String username, String product, int quantity) {
        String userKey = ATENDIMENTO_KEY + username + ":products"; // associar o produto ao utilizador

        long currentTime = Instant.now().getEpochSecond(); // tempo atual para marcar quando o user faz o pedido

        Transaction transaction = jedis.multi(); // garantir que multiplas operacoes acontecem sem interrupcoes ou insconsitencias

        transaction.zremrangeByScore(userKey, "-inf", String.valueOf(currentTime - timeslot)); // remover produtos fora do timeslot

        Response<List<Tuple>> products = transaction.zrangeWithScores(userKey, 0, -1); // obter os produtos pedidos

        System.out.println(products);

        transaction.exec();

        int totalQuantity = 0;
        for (Tuple t : products.get()) {
            totalQuantity += t.getScore();
        }
        System.out.println(totalQuantity);

        if (totalQuantity + quantity > limite) {
            return false;
        }

        transaction = jedis.multi();

        transaction.zadd(userKey, quantity, product); // adiciona o produto ao set do utilizador com a quantidade
        transaction.expire(userKey, timeslot); // definir o tempo de vida do set

        transaction.exec();

        return true;

    }
    
    

}
