package Connections;

import java.io.IOException;
import models.Client;
import models.Role;

public class ClientConnect {
    public static void main(String[] args) throws IOException {
        new Client(Role.ADMIN).connection();




    }
}
