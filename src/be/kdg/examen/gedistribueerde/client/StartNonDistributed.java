package be.kdg.examen.gedistribueerde.client;

import be.kdg.examen.gedistribueerde.server.Server;
import be.kdg.examen.gedistribueerde.server.ServerImpl;

public class StartNonDistributed {
    public static void main(String[] args) {
        Server server = new ServerImpl();
        DocumentImpl document = new DocumentImpl();
        Client client = new Client(server, document);
        client.run();
    }
}
