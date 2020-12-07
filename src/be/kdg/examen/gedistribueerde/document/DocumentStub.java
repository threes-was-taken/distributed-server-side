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

    private void waitForAck(){
        // infinite loop waiting for acknowledge
        MethodCallMessage ack = waitForResponse();

        if (!ack.getMethodName().equals("ack")) {
            System.err.println("Incorrect acknowledgement received");
            return;
        }

        if (!ack.getParameter("result").equals("ok")){
            System.err.println("Expected to have \"ok\" received with acknowledgement");
            return;
        }

        System.out.println("Acknowledgement received");
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

        waitForAck();

        messageManager.send(methodCall, this.replyAddress);
    }

    @Override
    public void setChar(int position, char c) {
        String character = c + " ";
        String pos = position + " ";

        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "setChar");
        methodCall.setParameter("positionChar", pos);
        methodCall.setParameter("character", character);

        waitForAck();

        messageManager.send(methodCall, this.replyAddress);
    }
}
