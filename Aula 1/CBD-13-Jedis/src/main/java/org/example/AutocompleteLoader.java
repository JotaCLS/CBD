package org.example;

import redis.clients.jedis.Jedis; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class AutocompleteLoader {

    public static String USERS_KEY = "users"; // Key set for users' name 
    public static void main(String[] args) { 
        // Ensure you have redis-server running 
        Jedis jedis = new Jedis();

        jedis.del(USERS_KEY);

        if (args.length == 0) {
            System.out.println("No filepath provided");
            return;
        }

        String filePath = args[0];

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = br.readLine()) != null) {
                jedis.zadd(USERS_KEY, 0, line); // zsets - collection of unique strings ordered by score and lexicographically
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Names inserted");
        jedis.close();
        
    }

}
