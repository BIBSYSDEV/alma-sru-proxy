package no.unit.alma;

import no.unit.utils.StringUtils;

public class Config {

    public static final String MISSING_ENVIRONMENT_VARIABLES = "Missing environment variables ALMA_SRU_HOST or ALMA_UPDATE_HOST";
    public static final String CORS_ALLOW_ORIGIN_HEADER_ENVIRONMENT_NAME = "ALLOWED_ORIGIN";
    public static final String ALMA_SRU_HOST_KEY = "ALMA_SRU_HOST";
    public static final String ALMA_UPDATE_HOST_KEY = "ALMA_UPDATE_HOST";
    public static final String ALMA_SRU_QUERY_PATH_NETWORK = "view/sru/47BIBSYS_NETWORK";

    private String corsHeader;
    private String almaSruHost;
    private String almaUpdateHost;


    private Config() {
    }

    private static class LazyHolder {

        private static final Config INSTANCE = new Config();

        static {
            INSTANCE.setAlmaSruHost(System.getenv(ALMA_SRU_HOST_KEY));
            INSTANCE.setAlmaUpdateHost(System.getenv(ALMA_UPDATE_HOST_KEY));
            INSTANCE.setCorsHeader(System.getenv(CORS_ALLOW_ORIGIN_HEADER_ENVIRONMENT_NAME));
        }
    }

    public static Config getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Checking if almaSruHost is present.
     *
     * @return <code>TRUE</code> if property is present.
     */
    public boolean checkProperties() {
        if (StringUtils.isEmpty(almaSruHost) && StringUtils.isEmpty(almaUpdateHost)) {
            throw new RuntimeException(MISSING_ENVIRONMENT_VARIABLES);
        }
        return true;
    }

    public void setAlmaSruHost(String almaSruHost) {
        this.almaSruHost = almaSruHost;
    }

    public String getAlmaSruHost() {
        return almaSruHost;
    }

    public void setAlmaUpdateHost(String almaUpdateHost) {
        this.almaUpdateHost = almaUpdateHost;
    }

    public String getAlmaUpdateHost() {
        return almaUpdateHost;
    }

    public String getCorsHeader() {
        return corsHeader;
    }

    public void setCorsHeader(String corsHeader) {
        this.corsHeader = corsHeader;
    }

}
