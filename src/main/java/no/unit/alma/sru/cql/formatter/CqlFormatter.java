package no.unit.alma.sru.cql.formatter;

import com.google.common.net.UrlEscapers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import no.unit.utils.YearWrapper;

import static java.util.Objects.nonNull;

public class CqlFormatter {

    private static final String BEGINS_COMPARATOR = "=";
    private static final String TERM_PATH_SEPARATOR = ".";
    private static final String TERM_SET = "alma";
    private static final String MODIFIER_SEPARATOR = "/";
    private static final String CLAUSE_SEPARATOR = " ";

    private static final String AUTHORITY_ID = "authority_id";
    private static final String CREATOR = "creator";
    private static final String PUBLICATION_DATE = "main_pub_date";

    private static final String MMS_ID = "mms_id";
    private static final String INSTITUTION_MMS_ID = "all_for_ui";

    private static final String ISBN = "isbn";

    private static final String SORT_BY = "sortBy";
    private static final String SORT_MODIFIER = "sort.descending";
    private static final int LAST_N_YEARS = 41;
    private static final String LOGICAL_AND = "AND";
    private static final String STRING_DELIMITER = "\"";
    private static final String CLAUSE_DELIMITER_LEFT = "(";
    private static final String CLAUSE_DELIMITER_RIGHT = ")";
    private static final CharSequence LOGICAL_OR = "OR";
    private static final String WHITESPACE = " ";
    private final YearWrapper yearProvider;

    private transient String authorityId;
    private transient String creator;
    private transient String mmsId;
    private transient String isbn;
    private transient String institution;
    private transient boolean sorted;
    private transient boolean retrospective;

    public CqlFormatter() {
        this.yearProvider = new YearWrapper();
    }

    public CqlFormatter(YearWrapper yearWrapper) {
        this.yearProvider = yearWrapper;
    }

    public CqlFormatter withAuthorityId(String authorityId) {
        this.authorityId = authorityId;
        return this;
    }

    public CqlFormatter withMmsId(String mmsId) {
        this.mmsId = mmsId;
        return this;
    }

    public CqlFormatter withIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public CqlFormatter withInstitution(String institution) {
        this.institution = institution;
        return this;
    }

    /**
     * Builds a formatted cql-query from authorityId (scn) and creator name.
     * @return cql-query
     */
    public String build() {

        List<String> clauses = new ArrayList<>();

        if (nonNull(this.authorityId)) {
            clauses.add(generateCqlClause(generateIndex(AUTHORITY_ID), this.authorityId));
        }

        if (nonNull(this.creator)) {
            clauses.add(generateCqlClause(generateIndex(CREATOR), this.creator));
        }

        if (nonNull(this.mmsId)) {
            if (nonNull(this.institution)) {
                clauses.add(generateCqlClause(generateIndex(INSTITUTION_MMS_ID), this.mmsId));
            } else {
                clauses.add(generateCqlClause(generateIndex(MMS_ID), this.mmsId));
            }
        } else if (nonNull(this.isbn)) {
            clauses.add(generateCqlClause(generateIndex(ISBN), this.isbn));
        }

        if (retrospective) {
            clauses.add(generateDateClause());
        }

        String query = String.join(CLAUSE_SEPARATOR + LOGICAL_AND + CLAUSE_SEPARATOR, clauses);

        if (sorted) {
            final StringBuilder sortedQuery = new StringBuilder();
            sortedQuery.append(query)
                    .append(CLAUSE_SEPARATOR)
                    .append(SORT_BY)
                    .append(CLAUSE_SEPARATOR)
                    .append(generateSortSpecification(generateIndex(PUBLICATION_DATE)));
            query = sortedQuery.toString();
        }

        return query;
    }

    private String generateSortSpecification(String index) {
        return index + MODIFIER_SEPARATOR + CqlFormatter.SORT_MODIFIER;
    }

    private String generateIndex(String index) {
        return String.join(TERM_PATH_SEPARATOR, CqlFormatter.TERM_SET, index);
    }

    public CqlFormatter withCreator(String creator) {
        this.creator = creator;
        return this;
    }

    private String generateDateClause() {
        int currentYear = yearProvider.getCurrentYear();
        List<String> dateClauses = new ArrayList<>();

        IntStream.range(0, LAST_N_YEARS).forEach(year -> dateClauses
                .add(generateCqlClause(generateIndex(PUBLICATION_DATE), String.valueOf(currentYear - year))));
        Collections.reverse(dateClauses);

        return CLAUSE_DELIMITER_LEFT
                + String.join(String.join(LOGICAL_OR, CLAUSE_SEPARATOR, CLAUSE_SEPARATOR),
                dateClauses) + CLAUSE_DELIMITER_RIGHT;
    }

    private String generateCqlClause(String term, String value) {
        String cqlValue = value;
        if (value.contains(WHITESPACE)) {
            cqlValue = STRING_DELIMITER + value + STRING_DELIMITER;
        }
        return String.join(CqlFormatter.BEGINS_COMPARATOR, term, cqlValue);
    }

    public CqlFormatter withRetrospective(boolean retrospective) {
        this.retrospective = retrospective;
        return this;
    }

    public CqlFormatter withSorting(boolean sorting) {
        this.sorted = sorting;
        return this;
    }

    public String encode() {
        return UrlEscapers.urlPathSegmentEscaper().escape(build());
    }
}
