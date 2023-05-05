package com.alexkzk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Smarthouse {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/smarthouse/v2/summary", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("ready to accept connections");
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String jsonString;
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("smarthouse-data.json")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
                jsonString = mapper.writeValueAsString(jsonNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, jsonString.length());
            OutputStream os = t.getResponseBody();
            os.write(jsonString.getBytes());
            os.close();
        }
    }

}