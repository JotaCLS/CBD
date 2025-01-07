package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Scanner;
import java.time.Instant;

// Processo
// Cada utilizador vai ter um zset associado a si com os produtos que pediu
// Cada produto vai ter um score associado ao tempo em que foi pedido
// Quando um utilizador pede um produto, é verificado se o utilizador ja fez mais de "limite" pedidos nos ultimos "timeslot" segundos
// Se sim, o produto é removido do zset do utilizador
// Se nao, o produto é adicionado ao zset do utilizador com o tempo atual

public class Atendimento {
    
    public static String ATENDIMENTO_KEY = "user:"; // Key set for users' name
    public static int limite = 3;
    public static int timeslot = 60;

    public static void main(String[] args) {
        Jedis jedis = new Jedis();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nEnter username ('Enter' to exit): ");
            String username = scanner.nextLine();

            if (username.isEmpty()) {
                break;
            }

            System.out.print("Enter product: ");
            String product = scanner.nextLine();

            // Processar o pedido
            boolean allowed = processRequest(jedis, username, product);

            if (allowed) {
                System.out.println("Request added successfully.");
            } else {
                System.out.println("Error: You have exceeded the limit of " + limite + " products in the last " + (timeslot / 60) + " minutes.");
            }
        }

        jedis.close();
    }

    private static boolean processRequest(Jedis jedis, String username, String product) {
        String userKey = ATENDIMENTO_KEY + username + ":products"; // associar o produto ao utilizador

        long currentTime = Instant.now().getEpochSecond(); // tempo atual para marcar quando o user faz o pedido

        Transaction transaction = jedis.multi(); // garantir que multiplas operacoes acontecem sem interrupcoes ou insconsitencias

        transaction.zremrangeByScore(userKey, "-inf", String.valueOf(currentTime - timeslot)); // remover produtos fora do timeslot
        transaction.zcard(userKey); // obter o numero de produtos pedidos
        transaction.zadd(userKey, currentTime, product); // adiciona o produto ao set do utilizador com o tempo atual
        transaction.expire(userKey, timeslot); // definir o tempo de vida do set

        List<Object> results = transaction.exec();

        long numRequests = (long) results.get(1); // Quantidade de produtos pedidos

        if (numRequests >= limite) {
            // Se o limite for excedido, remover o produto recém-adicionado
            jedis.zrem(userKey, product);
            return false;
        }

        return true;
    }

}
