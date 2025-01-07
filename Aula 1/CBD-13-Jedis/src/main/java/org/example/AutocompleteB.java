package org.example;

import redis.clients.jedis.Jedis;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

// Processo 
// Criar um sorted set com os nomes e os scores
// Criar outro set apenas com os nomes sem os scores
// No set sem scores obter os nomes do auto complete como na alinea A
// Guardar esses nomes e procurar no set com scores os scores 
// Tendo os nomes e os scores ordena-los por ordem decrescente de scores e mostra-los 


public class AutocompleteB {
    public static String USERS_KEY = "users"; // Key set for users' name 
    public static String USERS_NAMES_KEY = "users_names"; // New set without scores 


    public static void main(String[] args) { 
        // Ensure you have redis-server running 
        Jedis jedis = new Jedis();

        createNamesSet(jedis);


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
                System.out.printf("%-15s -> %s\n", "Nome", "Popularidade");
                suggestions.forEach(name -> System.out.printf("%-15s -> %.2f\n", name, jedis.zscore(USERS_KEY, name)));
            }

            
            
        }
        jedis.close();
    }

    private static void createNamesSet(Jedis jedis) {
        List<String> names = jedis.zrevrange(USERS_KEY, 0, -1); // Obter todos os nomes em ordem decrescente de score
    
        for (String name : names) {
            jedis.zadd(USERS_NAMES_KEY, 0, name); 
        }
        System.out.println("Nomes inseridos no novo set sem scores em ordem decrescente.");
    }

    private static List<String> getAutocompleteSuggestions(Jedis jedis, String searchTerm) {
        String start = searchTerm;
        String end = searchTerm + "\uFFFF"; 

        List<String> lexResults = jedis.zrangeByLex(USERS_NAMES_KEY, "[" + start, "[" + end);


        Map<String, Double> nameScoreMap = new HashMap<>();
        for (String name : lexResults) {
            Double score = jedis.zscore(USERS_KEY, name); 
            nameScoreMap.put(name, score);
        }

        return nameScoreMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) 
                .map(Map.Entry::getKey)  
                .collect(Collectors.toList()); 
    }

}
