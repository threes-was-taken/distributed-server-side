/* Dries created on 12/5/2020 inside the package - be.kdg.examen.gedistribueerde.client */
package be.kdg.examen.gedistribueerde.document;

import be.kdg.examen.gedistribueerde.communication.MessageManager;
import be.kdg.examen.gedistribueerde.communication.MethodCallMessage;
import be.kdg.examen.gedistribueerde.communication.NetworkAddress;

import java.util.Objects;

public class DocumentStub implements Document{
    private NetworkAddress replyAddress;
    private final NetworkAddress originAddress;
    private final MessageManager messageManager;

    //======== CONSTRUCTORS =================

    public DocumentStub(NetworkAddress replyAddress, NetworkAddress originAddress, MessageManager messageManager) {
        this.replyAddress = replyAddress;
        this.originAddress = originAddress;
        this.messageManager = messageManager;
    }

    public DocumentStub(NetworkAddress originAddress, MessageManager messageManager) {
        this.originAddress = originAddress;
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

        waitForAck();
    }

    @Override
    public void append(char c) {
        String character = c + " ";
        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "appendText");
        methodCall.setParameter("toAppend", character);
        messageManager.send(methodCall, this.replyAddress);

        waitForAck();
    }

    @Override
    public void setChar(int position, char c) {
        String character = c + " ";
        String pos = position + " ";

        MethodCallMessage methodCall = new MethodCallMessage(this.messageManager.getMyAddress(), "setChar");
        methodCall.setParameter("positionChar", pos);
        methodCall.setParameter("character", character);
        messageManager.send(methodCall, this.replyAddress);

        waitForAck();
    }

    //======== OVERRIDE METHODS =================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentStub that = (DocumentStub) o;
        return originAddress.equals(that.originAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originAddress);
    }

    @Override
    public String toString() {
        return this.originAddress.toString();
    }
}
