package com.neo4j.lab;

import java.io.FileReader;
import java.io.IOException;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;



public class Neo4jConnector {
    private static Driver driver;

    // Conecta ao servidor Neo4j
    public static void connect() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("joao", "12345678"));
    }

    // Fecha a conexão
    public static void close() {
        if (driver != null) {
            driver.close();
        }
    }

    public static Driver getDriver() {
        return driver;
    }
}
