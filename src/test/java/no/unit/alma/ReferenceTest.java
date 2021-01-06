package no.unit.alma;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceTest {

    public static final String SOME_TITLE = "Some title";

    @Test
    public void exists() {
        new Reference();
    }

    @Test
    public void reference_hasTitle() {
        Reference reference = new Reference();
        reference.setTitle(SOME_TITLE);
        assertEquals(SOME_TITLE, reference.getTitle());
    }

}