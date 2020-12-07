package be.kdg.examen.gedistribueerde.server;

import be.kdg.examen.gedistribueerde.document.Document;
import be.kdg.examen.gedistribueerde.document.DocumentImpl;
import be.kdg.examen.gedistribueerde.communication.MessageManager;
import be.kdg.examen.gedistribueerde.communication.MethodCallMessage;
import be.kdg.examen.gedistribueerde.communication.NetworkAddress;

public class ServerSkeleton {
    private final MessageManager messageManager;
    private final Server documentServer;

    public ServerSkeleton(NetworkAddress address, Server documentServer) {
        this.messageManager = new MessageManager(address);
        System.out.println("my address = " + messageManager.getMyAddress());
        this.documentServer = documentServer;
    }

//======== LISTENERS =================

    public void listen(){
        System.out.println("Server started listening on " + this.messageManager.getMyAddress());

        //looping to wait for requests
        while (true){

            // sync wait for req
            MethodCallMessage req = messageManager.wReceive();

            //ack that we have received the req
            ack(req.getOriginator());

            //handle the request
            handleRequest(req);
        }
    }

    //======== MESSAGE CONTROL =================

    private void handleRequest(MethodCallMessage req) {
        System.out.println("Incoming request " + req);

        switch (req.getMethodName()) {
            case "log":
                handleLog(req);
                break;
            case "create":
                handleCreate(req);
                break;
            case "toUpper":
                handleToUpper(req);
                break;
            case "toLower":
                handleToLower(req);
                break;
            case "type":
                handleType(req);
                break;
            default:
                System.err.println("Unrecognized request " + req.getMethodName() + " received.");
                break;
        }
    }

    private void ack(NetworkAddress originator) {
        MethodCallMessage ack = new MethodCallMessage(this.messageManager.getMyAddress(), "ack");
        ack.setParameter("result", "ok");
        this.messageManager.send(ack, originator);
    }

    //======== DELEGATIONS =================
    private void handleType(MethodCallMessage req) {
        String document = req.getParameter("document");
        String text = req.getParameter("text");

        this.documentServer.type(new DocumentImpl(document), text);
    }

    private void handleToLower(MethodCallMessage req) {
        String text = req.getParameter("document");

        this.documentServer.toLower(new DocumentImpl(text));
    }

    private void handleToUpper(MethodCallMessage req) {
        String text = req.getParameter("document");

        this.documentServer.toUpper(new DocumentImpl(text));

    }

    private void handleCreate(MethodCallMessage req) {
        String text = req.getParameter("stringToAdd");
        Document doc = this.documentServer.create(text);

        MethodCallMessage resp = new MethodCallMessage(messageManager.getMyAddress(), "createNewDocument");
        resp.setParameter("newString", doc.getText());
        messageManager.send(resp, req.getOriginator());
    }

    private void handleLog(MethodCallMessage req) {
        String message = req.getParameter("documentText");

        this.documentServer.log(new DocumentImpl(message));
    }
}
