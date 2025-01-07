package org.example;

import redis.clients.jedis.Jedis;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Autocomplete {
    public static String USERS_KEY = "users"; // Key set for users' name 

    public static void main(String[] args) { 
        // Ensure you have redis-server running 
        Jedis jedis = new Jedis();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nSearch for ('Enter' for quit): ");
            String searchTerm = scanner.nextLine();

            if (searchTerm.isEmpty()) {
                break;
            }

            List<String> suggestions = getAutocompleteSuggestions(jedis, searchTerm);
            if (suggestions.isEmpty()) {
                System.out.println("No autocomplete found");
            } else {
                suggestions.forEach(System.out::println);
            }

            
            
        }
        jedis.close();
    }

    private static List<String> getAutocompleteSuggestions(Jedis jedis, String searchTerm){
        String start = searchTerm;
        String end = searchTerm + "\uFFFF"; // 


        List<String> results = jedis.zrangeByLex(USERS_KEY, "[" + start, "[" + end);

        return results;
    }
}
