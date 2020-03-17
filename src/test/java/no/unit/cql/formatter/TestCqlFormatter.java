package no.unit.cql.formatter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCqlFormatter {

    public static final String FAKE_AUTHORITY_ID = "123";
    public static final String EXPECTED_AUTHORITY_ID_CQL = "alma.authority_id=";
    public static final String EXPECTED_CREATOR_CQL = "alma.creator=";
    public static final String FAKE_NAME = "Nameson, Name";
    public static final String IBSEN_SCN = "90061718";
    public static final String IBSEN_HENRIK = "Ibsen, Henrik";

    @Test
    public void assertExists() {
        new CqlFormatter();
    }

    @Test
    public void assertReturnsCqlString() {
        CqlFormatter cqlFormatter = new CqlFormatter();
        assertEquals(cqlFormatter.build().getClass(), String.class);
    }

    @Test
    public void assertReturnsCqlAuthorityQuery() {
        CqlFormatter cqlFormatter = new CqlFormatter()
                .withAuthorityId(FAKE_AUTHORITY_ID);
        assertEquals(EXPECTED_AUTHORITY_ID_CQL + FAKE_AUTHORITY_ID, cqlFormatter.build());
    }

    @Test
    public void assertReturnsCqlCreatorQuery() {
        CqlFormatter cqlFormatter = new CqlFormatter()
                .withCreator(FAKE_NAME);
        assertEquals(EXPECTED_CREATOR_CQL + "\"" + FAKE_NAME + "\"", cqlFormatter.build());
    }

    @Test
    public void assertReturnsCqlWithDateQuery() {
        CqlFormatter cqlFormatter = new CqlFormatter()
                .withAuthorityId(FAKE_AUTHORITY_ID)
                .withRetrospective(true);
        String expected = "alma.authority_id=123 AND "
                + "(alma.main_pub_date=1980 OR alma.main_pub_date=1981 OR alma.main_pub_date=1982 "
                + "OR alma.main_pub_date=1983 OR alma.main_pub_date=1984 OR alma.main_pub_date=1985 "
                + "OR alma.main_pub_date=1986 OR alma.main_pub_date=1987 OR alma.main_pub_date=1988 "
                + "OR alma.main_pub_date=1989 OR alma.main_pub_date=1990 OR alma.main_pub_date=1991 "
                + "OR alma.main_pub_date=1992 OR alma.main_pub_date=1993 OR alma.main_pub_date=1994 "
                + "OR alma.main_pub_date=1995 OR alma.main_pub_date=1996 OR alma.main_pub_date=1997 "
                + "OR alma.main_pub_date=1998 OR alma.main_pub_date=1999 OR alma.main_pub_date=2000 "
                + "OR alma.main_pub_date=2001 OR alma.main_pub_date=2002 OR alma.main_pub_date=2003 "
                + "OR alma.main_pub_date=2004 OR alma.main_pub_date=2005 OR alma.main_pub_date=2006 "
                + "OR alma.main_pub_date=2007 OR alma.main_pub_date=2008 OR alma.main_pub_date=2009 "
                + "OR alma.main_pub_date=2010 OR alma.main_pub_date=2011 OR alma.main_pub_date=2012 "
                + "OR alma.main_pub_date=2013 OR alma.main_pub_date=2014 OR alma.main_pub_date=2015 "
                + "OR alma.main_pub_date=2016 OR alma.main_pub_date=2017 OR alma.main_pub_date=2018 "
                + "OR alma.main_pub_date=2019 OR alma.main_pub_date=2020)";
        assertEquals(expected, cqlFormatter.build());
    }

    @Test
    public void assertReturnsCqlWithSorting() {
        CqlFormatter cqlFormatter = new CqlFormatter()
                .withAuthorityId(FAKE_AUTHORITY_ID)
                .withSorting(true);
        String expected = "alma.authority_id=123 sortBy alma.main_pub_date/sort.descending";
        assertEquals(expected, cqlFormatter.build());
    }

    @Test
    public void assertFullyFeaturedBuilder() {
        CqlFormatter cqlFormatter = new CqlFormatter()
                .withAuthorityId(FAKE_AUTHORITY_ID)
                .withCreator(FAKE_NAME)
                .withRetrospective(true)
                .withSorting(true);
        String expected = "alma.authority_id=123 AND alma.creator=\"Nameson, Name\" AND "
                + "(alma.main_pub_date=1980 OR alma.main_pub_date=1981 OR alma.main_pub_date=1982 "
                + "OR alma.main_pub_date=1983 OR alma.main_pub_date=1984 OR alma.main_pub_date=1985 "
                + "OR alma.main_pub_date=1986 OR alma.main_pub_date=1987 OR alma.main_pub_date=1988 "
                + "OR alma.main_pub_date=1989 OR alma.main_pub_date=1990 OR alma.main_pub_date=1991 "
                + "OR alma.main_pub_date=1992 OR alma.main_pub_date=1993 OR alma.main_pub_date=1994 "
                + "OR alma.main_pub_date=1995 OR alma.main_pub_date=1996 OR alma.main_pub_date=1997 "
                + "OR alma.main_pub_date=1998 OR alma.main_pub_date=1999 OR alma.main_pub_date=2000 "
                + "OR alma.main_pub_date=2001 OR alma.main_pub_date=2002 OR alma.main_pub_date=2003 "
                + "OR alma.main_pub_date=2004 OR alma.main_pub_date=2005 OR alma.main_pub_date=2006 "
                + "OR alma.main_pub_date=2007 OR alma.main_pub_date=2008 OR alma.main_pub_date=2009 "
                + "OR alma.main_pub_date=2010 OR alma.main_pub_date=2011 OR alma.main_pub_date=2012 "
                + "OR alma.main_pub_date=2013 OR alma.main_pub_date=2014 OR alma.main_pub_date=2015 "
                + "OR alma.main_pub_date=2016 OR alma.main_pub_date=2017 OR alma.main_pub_date=2018 "
                + "OR alma.main_pub_date=2019 OR alma.main_pub_date=2020) sortBy alma.main_pub_date/sort.descending";

        assertEquals(expected, cqlFormatter.build());
    }

    @Test
    public void generateTestableUri() {
        String expected = "alma.authority_id=90061718%20AND%20alma.creator=%22Ibsen,%20Henrik%22%20"
                + "AND%20(alma.main_pub_date=1980%20OR%20alma.main_pub_date=1981%20"
                + "OR%20alma.main_pub_date=1982%20OR%20alma.main_pub_date=1983%20"
                + "OR%20alma.main_pub_date=1984%20OR%20alma.main_pub_date=1985%20"
                + "OR%20alma.main_pub_date=1986%20OR%20alma.main_pub_date=1987%20"
                + "OR%20alma.main_pub_date=1988%20OR%20alma.main_pub_date=1989%20"
                + "OR%20alma.main_pub_date=1990%20OR%20alma.main_pub_date=1991%20"
                + "OR%20alma.main_pub_date=1992%20OR%20alma.main_pub_date=1993%20"
                + "OR%20alma.main_pub_date=1994%20OR%20alma.main_pub_date=1995%20"
                + "OR%20alma.main_pub_date=1996%20OR%20alma.main_pub_date=1997%20"
                + "OR%20alma.main_pub_date=1998%20OR%20alma.main_pub_date=1999%20"
                + "OR%20alma.main_pub_date=2000%20OR%20alma.main_pub_date=2001%20"
                + "OR%20alma.main_pub_date=2002%20OR%20alma.main_pub_date=2003%20"
                + "OR%20alma.main_pub_date=2004%20OR%20alma.main_pub_date=2005%20"
                + "OR%20alma.main_pub_date=2006%20OR%20alma.main_pub_date=2007%20"
                + "OR%20alma.main_pub_date=2008%20OR%20alma.main_pub_date=2009%20"
                + "OR%20alma.main_pub_date=2010%20OR%20alma.main_pub_date=2011%20"
                + "OR%20alma.main_pub_date=2012%20OR%20alma.main_pub_date=2013%20"
                + "OR%20alma.main_pub_date=2014%20OR%20alma.main_pub_date=2015%20"
                + "OR%20alma.main_pub_date=2016%20OR%20alma.main_pub_date=2017%20"
                + "OR%20alma.main_pub_date=2018%20OR%20alma.main_pub_date=2019%20"
                + "OR%20alma.main_pub_date=2020)%20sortBy%20alma.main_pub_date%2Fsort.descending";

        String encoded = new CqlFormatter()
                .withRetrospective(true)
                .withSorting(true)
                .withAuthorityId(IBSEN_SCN)
                .withCreator(IBSEN_HENRIK)
                .encode();

        assertEquals(expected, encoded);
    }

}
