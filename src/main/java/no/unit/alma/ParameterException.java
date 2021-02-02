package no.unit.alma;

public class ParameterException extends RuntimeException {

    public ParameterException(String parameterMissing) {
        super(parameterMissing);
    }
}
