/* Dries created on 12/5/2020 inside the package - be.kdg.examen.gedistribueerde.client */
package be.kdg.examen.gedistribueerde.document;

import be.kdg.examen.gedistribueerde.communication.MessageManager;
import be.kdg.examen.gedistribueerde.communication.MethodCallMessage;
import be.kdg.examen.gedistribueerde.communication.NetworkAddress;

import java.util.Objects;

public class DocumentStub implements Document{
    private final NetworkAddress replyAddress;
    private final MessageManager messageManager;

    //======== CONSTRUCTORS =================

    public DocumentStub(NetworkAddress replyAddress, MessageManager messageManager) {
        this.replyAddress = replyAddress;
        this.messageManager = messageManager;
    }

    //======== PRIVATE METHODS =================
    public static void checkEmptyReply(MessageManager messageManager) {
        MethodCallMessage reply = messageManager.wReceive();
        if (!"ack".equals(reply.getMethodName())) {
            throw new RuntimeException("Expected ack, got " + reply.getMethodName());
        }
    }

    private MethodCallMessage waitForResponse() {
        //infinite loop waiting for resp
        while(true){
            // sync wait for response
            MethodCallMessage resp = messageManager.wReceive();

            return resp;
        }
    }

    //======== INTERFACE METHODS =================
    @Override
    public String getText() {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "getText");
        messageManager.send(methodCall, this.replyAddress);

        //wait for response
        MethodCallMessage response = waitForResponse();
        return response.getParameter("value");
    }

    @Override
    public void setText(String text) {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "setText");
        methodCall.setParameter("textToSet", text);
        messageManager.send(methodCall, this.replyAddress);

        MethodCallMessage resp = messageManager.wReceive();
        if (!resp.getMethodName().equals("setText")){
            throw new RuntimeException("Expected setText response, got " + resp.getMethodName());
        }
    }

    @Override
    public void append(char c) {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "appendText");
        methodCall.setParameter("toAppend", String.valueOf(c));
        messageManager.send(methodCall, this.replyAddress);
        checkEmptyReply(messageManager);
    }

    @Override
    public void setChar(int position, char c) {
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "setChar");
        methodCall.setParameter("positionChar", String.valueOf(position));
        methodCall.setParameter("character", String.valueOf(c));
        messageManager.send(methodCall, this.replyAddress);
        checkEmptyReply(messageManager);
    }
}
