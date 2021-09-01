package no.unit.alma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AvailabilityResponseTest {

    @Test
    public void testGetterOgSetters() {
        AvailabilityResponse response = new AvailabilityResponse();
        final String libraryCode = "libraryCode";
        final String institution = "institution";
        final String mmsId = "mmsId";
        response.setLibraryCode(libraryCode);
        response.setInstitution(institution);
        response.setMmsId(mmsId);
        assertEquals(libraryCode, response.getLibraryCode());
        assertEquals(institution, response.getInstitution());
        assertEquals(mmsId, response.getMmsId());
    }

}
