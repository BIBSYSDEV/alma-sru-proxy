package no.unit.nva.alma;

public class MissingParameterException extends RuntimeException {

    public MissingParameterException(String parameterMissing) {
        super(parameterMissing);
    }
}
