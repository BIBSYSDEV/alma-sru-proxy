package no.unit.utils;

import java.time.Year;

public class YearWrapper {
    public int getCurrentYear() {
        return Year.now().getValue();
    }
}
