package be.kdg.examen.gedistribueerde.server;

import be.kdg.examen.gedistribueerde.document.Document;
import be.kdg.examen.gedistribueerde.document.DocumentImpl;
import be.kdg.examen.gedistribueerde.communication.MessageManager;
import be.kdg.examen.gedistribueerde.communication.MethodCallMessage;
import be.kdg.examen.gedistribueerde.communication.NetworkAddress;
import be.kdg.examen.gedistribueerde.document.DocumentStub;

public class ServerSkeleton {
    private final MessageManager messageManager;
    private final Server documentServer;

    public ServerSkeleton(NetworkAddress address, Server documentServer) {
        this.messageManager = new MessageManager(address);
        System.out.println("my address = " + messageManager.getMyAddress());
        this.documentServer = documentServer;
    }

//======== LISTENERS =================

    public void listen() {
        System.out.println("Server started listening on " + this.messageManager.getMyAddress());

        //looping to wait for requests
        while (true) {

            // sync wait for req
            MethodCallMessage req = messageManager.wReceive();

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

    //======== DELEGATIONS =================
    private void handleType(MethodCallMessage req) {
        String documentSkeletonAddress = req.getParameter("documentAddress");
        String skeletonIP = documentSkeletonAddress.split(":")[0];
        int skeletonPORT = Integer.parseInt(documentSkeletonAddress.split(":")[1]);
        String text = req.getParameter("text");

        Document document = new DocumentStub(new NetworkAddress(skeletonIP, skeletonPORT), this.messageManager);

        this.documentServer.type(document, text);

        sendEmptyReply(req, messageManager);
    }

    private void handleToLower(MethodCallMessage req) {
        String documentSkeletonAddress = req.getParameter("documentAddress");
        String skeletonIP = documentSkeletonAddress.split(":")[0];
        int skeletonPORT = Integer.parseInt(documentSkeletonAddress.split(":")[1]);

        Document document = new DocumentStub(new NetworkAddress(skeletonIP, skeletonPORT), this.messageManager);

        this.documentServer.toLower(document);

        sendEmptyReply(req, messageManager);
    }

    private void handleToUpper(MethodCallMessage req) {
        String documentSkeletonAddress = req.getParameter("documentAddress");
        String skeletonIP = documentSkeletonAddress.split(":")[0];
        int skeletonPORT = Integer.parseInt(documentSkeletonAddress.split(":")[1]);

        Document document = new DocumentStub(new NetworkAddress(skeletonIP, skeletonPORT), this.messageManager);

        this.documentServer.toUpper(document);

        sendEmptyReply(req, messageManager);
    }

    private void handleCreate(MethodCallMessage req) {
        String text = req.getParameter("stringToAdd");
        Document doc = this.documentServer.create(text);

        MethodCallMessage resp = new MethodCallMessage(messageManager.getMyAddress(), "createNewDocument");
        resp.setParameter("newString", doc.getText());
        messageManager.send(resp, req.getOriginator());
    }

    private void handleLog(MethodCallMessage req) {
        String documentSkeletonAddress = req.getParameter("documentAddress");
        String skeletonIP = documentSkeletonAddress.split(":")[0];
        int skeletonPORT = Integer.parseInt(documentSkeletonAddress.split(":")[1]);

        String message = req.getParameter("documentText");

        Document document = new DocumentStub(new NetworkAddress(skeletonIP, skeletonPORT), this.messageManager);

        document.setText(message);

        documentServer.log(document);

        MethodCallMessage resp = new MethodCallMessage(messageManager.getMyAddress(), "logResponse");
        messageManager.send(resp, req.getOriginator());
    }

    //======== PRIVATE METHODS =================
    private static void sendEmptyReply(MethodCallMessage request, MessageManager messageManager) {
        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "Ok");
        messageManager.send(reply, request.getOriginator());
    }
}
