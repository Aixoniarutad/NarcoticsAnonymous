package ciceroapps.tether.na;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingItem {
    public String ADDDT;
    public String ADDRESS;
    public String ADDUSER;
    public String BOROUGH;
    public String CITY;
    public String CONTGRANDPAR;
    public String COUNTRY;
    public String DAY;
    public String DAYCHAR;
    public String DIRECTIONS;
    public long DISTANCE;
    public String FORMAT;
    public String GROUPID;
    public String INSTITUTIONAL;
    public String LANG1;
    public String LANG2;
    public String LANG3;
    public double LATITUDE;
    public double LONGITUDE;
    public String MBADDDT;
    public String MBADDUSER;
    public String MBCHGDT;
    public String MBCHGUSER;
    public String MEETING;
    public String MEETINGUID;
    public String MTADDDT;
    public String MTADDUSER;
    public String MTCHGDT;
    public String MTCHGUSER;
    public String OPEN;
    public String PARENTADDDT;
    public String PARENTADDUSER;
    public String PLACE;
    public String ROOM;
    public String STATE;
    public String STATENAME;
    public String TIME;
    public String WCHAIR;
    public String ZIP;

    // Parameterless Constructor
    public MeetingItem(){
    }

    public String getADDDT() {
        return ADDDT;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public String getADDUSER() {
        return ADDUSER;
    }

    public String getBOROUGH() {
        return BOROUGH;
    }

    public String getCITY() {
        return CITY;
    }

    public String getCONTGRANDPAR() {
        return CONTGRANDPAR;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public String getDAY() {
        return DAY;
    }

    public String getDAYCHAR() {
        return DAYCHAR;
    }

    public String getDIRECTIONS() {
        return DIRECTIONS;
    }

    public long getDISTANCE() {
        return DISTANCE;
    }

    public String getFORMAT() {
        return FORMAT;
    }

    public String getGROUPID() {
        return GROUPID;
    }

    public String getINSTITUTIONAL() {
        return INSTITUTIONAL;
    }

    public String getLANG1() {
        return LANG1;
    }

    public String getLANG2() {
        return LANG2;
    }

    public String getLANG3() {
        return LANG3;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public String getMBADDDT() {
        return MBADDDT;
    }

    public String getMBADDUSER() {
        return MBADDUSER;
    }

    public String getMBCHGDT() {
        return MBCHGDT;
    }

    public String getMBCHGUSER() {
        return MBCHGUSER;
    }

    public String getMEETING() {
        return MEETING;
    }

    public String getMEETINGUID() {
        return MEETINGUID;
    }

    public String getMTADDDT() {
        return MTADDDT;
    }

    public String getMTADDUSER() {
        return MTADDUSER;
    }

    public String getMTCHGDT() {
        return MTCHGDT;
    }

    public String getMTCHGUSER() {
        return MTCHGUSER;
    }

    public String getOPEN() {
        return OPEN;
    }

    public String getPARENTADDDT() {
        return PARENTADDDT;
    }

    public String getPARENTADDUSER() {
        return PARENTADDUSER;
    }

    public String getPLACE() {
        return PLACE;
    }

    public String getROOM() {
        return ROOM;
    }

    public String getSTATE() {
        return STATE;
    }

    public String getSTATENAME() {
        return STATENAME;
    }

    public String getTIME() {
        return TIME;
    }

    public String getWCHAIR() {
        return WCHAIR;
    }

    public String getZIP() {
        return ZIP;
    }
}