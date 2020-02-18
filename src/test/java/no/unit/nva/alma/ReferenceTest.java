package no.unit.nva.alma;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

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