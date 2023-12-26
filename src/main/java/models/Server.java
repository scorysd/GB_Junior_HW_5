package models;

import models.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Server {
    public Client client;
    public static final int PORT = 8181;
    private static long clientIdCounter = 1L;
    private static Map<Long, SocketWrapper> clients = new HashMap<>();

    public Server() {
    }


    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                final Socket client = server.accept();
                final long clientId = clientIdCounter++;
                SocketWrapper wrapper = new SocketWrapper(clientId, client);
                System.out.println("Подключился новый клиент[" + wrapper + "]");
                clients.put(clientId, wrapper);

                new Thread(() -> {
                    try (Scanner input = wrapper.getInput(); PrintWriter output = wrapper.getOutput()) {
                        output.println("Подключение успешно. Список всех клиентов: " + clients);

                        while (true) {
                            String clientInput = input.nextLine();
                            if (Objects.equals("q", clientInput)) {
                                // todo разослать это сообщение всем остальным клиентам
                                clients.remove(clientId);
                                clients.values().forEach(it -> it.getOutput().println("Клиент[" + clientId + "] отключился"));
                                break;
                            }
                            // формат сообщения: "@цифра сообщение"
                            else if (clientInput.charAt(0) == '@') {
                                long destinationId = Long.parseLong(clientInput.substring(1, 2));
                                SocketWrapper destination = clients.get(destinationId);
                                destination.getOutput().println(clientInput.substring(3));
                            } else {
                                clients.values().forEach(it -> it.getOutput().println(clientInput));
                            }
                        }
                    }
                }).start();
            }
        }
    }

}



