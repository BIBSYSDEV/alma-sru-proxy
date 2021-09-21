package no.unit.alma;

public class AvailabilityResponse {

    private String mmsId = "";
    private String institution = "";
    private String libraryCode = "";
    private int totalNumberOfItems;
    private int numberAvailForInterLibraryLoan;
    private String availableDate = "";

    public int getTotalNumberOfItems() {
        return totalNumberOfItems;
    }

    public void setTotalNumberOfItems(int totalNumberOfItems) {
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public int getNumberAvailForInterLibraryLoan() {
        return numberAvailForInterLibraryLoan;
    }

    public void setNumberAvailForInterLibraryLoan(int numberAvailForInterLibraryLoan) {
        this.numberAvailForInterLibraryLoan = numberAvailForInterLibraryLoan;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getMmsId() {
        return mmsId;
    }

    public void setMmsId(String mmsId) {
        this.mmsId = mmsId;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getLibraryCode() {
        return libraryCode;
    }

    public void setLibraryCode(String libraryCode) {
        this.libraryCode = libraryCode;
    }
}
