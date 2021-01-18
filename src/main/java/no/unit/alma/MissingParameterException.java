package no.unit.alma;

public class MissingParameterException extends RuntimeException {

    public MissingParameterException(String parameterMissing) {
        super(parameterMissing);
    }
}
