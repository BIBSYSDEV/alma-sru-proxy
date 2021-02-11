package no.unit.alma.sru;

public class ParsingException extends Exception {
    public ParsingException(String message, Exception e) {
        super(message, e);
    }
}
