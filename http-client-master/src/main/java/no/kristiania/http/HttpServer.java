package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public HttpServer(int port) throws IOException {
        //Open a entry point to our program for network clients
        ServerSocket serverSocket = new ServerSocket(port);

        //New Threads executes the code in a separate "thread", that is in parallel
        new Thread(() -> { //Annonymous function with code that will be executed in parallel
            while (true) {
                try {
                    //Accept waits for a client to try to connect - blocks
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    //If something went wrong - print the exception and try again
                    e.printStackTrace();
                }
            }
        }).start(); //Start the threads, so the code inside executes without blocking the current thread
    }

    //This code will be executed for each client
    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);
        //Example: GET /echo?body=Hello HTTP/1.1

        String requestTarget = requestLine.split(" ")[1];
        //Example: "/echo?body=Hello"
        String statusCode = "200";

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1) {
            String queryString = requestTarget.substring(questionPos+1);
            //body = hello

            int equalPos = queryString.indexOf('=');
            String parameterName = queryString.substring(0, equalPos);
            String parameterValue = queryString.substring(equalPos+1);
            statusCode = parameterValue;
        }

        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: 29\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Hello <strong>World</strong>!";

        //Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }
}
