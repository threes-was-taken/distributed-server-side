package be.kdg.examen.gedistribueerde.document;

public interface Document {
    String getText();
    void setText(String text);
    void append(char c);
    void setChar(int position, char c);
}
