package be.kdg.examen.gedistribueerde;

import be.kdg.examen.gedistribueerde.communication.NetworkAddress;
import be.kdg.examen.gedistribueerde.server.Server;
import be.kdg.examen.gedistribueerde.server.ServerImpl;
import be.kdg.examen.gedistribueerde.server.ServerSkeleton;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class StartDistributedServer {
    public static void main(String[] args) {
        //Check if the args are equal to 1
        if (args.length != 1) {
            System.err.println("Usage: java Start <port>");
            System.exit(1);
        }

        NetworkAddress networkAddress = null;

        try{
            //get designated port from args
            int port = Integer.parseInt(args[0]);
            InetAddress localhost = InetAddress.getLocalHost();

            //make server address
            networkAddress = new NetworkAddress(localhost.getHostAddress(), port);
        } catch (UnknownHostException e) {
            System.err.println("Failed to get network address");
            e.printStackTrace();
            System.exit(1);
        }

        //create server impl
        Server documentServer = new ServerImpl();

        //create server skeleton
        ServerSkeleton serverSkeleton = new ServerSkeleton(networkAddress, documentServer);

        //start listening
        serverSkeleton.listen();
    }
}
