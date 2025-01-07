package org.example;


import redis.clients.jedis.Jedis; 
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimplePost { 
    public static String USERS_KEY = "users"; // Key set for users' name 
    public static List<String> list = new ArrayList<>();
    public static HashMap<String, String> hash = new HashMap<>();
    public static void main(String[] args) { 
        Jedis jedis = new Jedis(); 
        // some users 
        String[] users = { "Ana", "Pedro", "Maria", "Luis" }; 
        
        // add elements to list
        list.add("elemento1");
        list.add("elemento2");
        list.add("elemento3");
        list.add("elemento4");

        // add elements to HashMap

        hash.put("1", "hash1");
        hash.put("12", "hash2");
        hash.put("4", "hash3");
        hash.put("15", "hash4");


        // jedis.del(USERS_KEY); // remove if exists to avoid wrong type 
        for (String user : users)  
        jedis.sadd(USERS_KEY, user); 
        jedis.smembers(USERS_KEY).forEach(System.out::println); 
        

        jedis.del("list");

        for (String element : list)
        jedis.rpush("list", element);
        jedis.lrange("list", 0, -1).forEach(System.out::println);

        // jedis.del("Hash");

        jedis.hmset("myHash", hash);
        Map<String, String> HashMapFinal = jedis.hgetAll("myHash");
        System.out.println("Retrieved HashMap: " + HashMapFinal);
        
        jedis.close();


    } 
} 
