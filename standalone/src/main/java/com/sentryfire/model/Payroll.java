 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      AccountRecievable.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import org.influxdb.annotation.Column;
 import org.influxdb.annotation.Measurement;

 @Measurement (name = "PAY")
 public class Payroll
 {

    @Column (name = "time")
    protected String time;

    @Column (name = "ATSAV")
    protected String ATSAV;

    @Column (name = "BEN")
    protected String BEN;

    @Column (name = "BTSAV")
    protected String BTSAV;

    @Column (name = "CHKDATE")
    protected String CHKDATE;

    @Column (name = "CHKNUM")
    protected String CHKNUM;

    @Column (name = "DED10")
    protected String DED10;

    @Column (name = "DED5")
    protected String DED5;

    @Column (name = "DED6")
    protected String DED6;

    @Column (name = "DED7")
    protected String DED7;

    @Column (name = "DED8")
    protected String DED8;

    @Column (name = "DED9")
    protected String DED9;

    @Column (name = "EN")
    protected String EN;

    @Column (name = "FED_WH")
    protected String FED_WH;

    @Column (name = "FLI")
    protected String FLI;

    @Column (name = "GROSS")
    protected Double GROSS;

    @Column (name = "HOURS10")
    protected String HOURS10;

    @Column (name = "HOURS5")
    protected String HOURS5;

    @Column (name = "HOURS6")
    protected String HOURS6;

    @Column (name = "HOURS7")
    protected String HOURS7;

    @Column (name = "HOURS8")
    protected String HOURS8;

    @Column (name = "HOURS9")
    protected String HOURS9;

    @Column (name = "LOC_WH")
    protected String LOC_WH;

    @Column (name = "MED")
    protected String MED;

    @Column (name = "MISC1")
    protected String MISC1;

    @Column (name = "NAME")
    protected String NAME;

    @Column (name = "NHOURS")
    protected String NHOURS;

    @Column (name = "OHOURS")
    protected String OHOURS;

    @Column (name = "REIMB")
    protected String REIMB;

    @Column (name = "SDI")
    protected String SDI;

    @Column (name = "SHOURS")
    protected String SHOURS;

    @Column (name = "SOC")
    protected String SOC;

    @Column (name = "ST_WH")
    protected String ST_WH;

    @Column (name = "SUI")
    protected String SUI;

    @Column (name = "TIPS")
    protected String TIPS;

    @Column (name = "VHOURS")
    protected String VHOURS;

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getATSAV()
    {
       return ATSAV;
    }

    public void setATSAV(String ATSAV)
    {
       this.ATSAV = ATSAV;
    }

    public String getBEN()
    {
       return BEN;
    }

    public void setBEN(String BEN)
    {
       this.BEN = BEN;
    }

    public String getBTSAV()
    {
       return BTSAV;
    }

    public void setBTSAV(String BTSAV)
    {
       this.BTSAV = BTSAV;
    }

    public String getCHKDATE()
    {
       return CHKDATE;
    }

    public void setCHKDATE(String CHKDATE)
    {
       this.CHKDATE = CHKDATE;
    }

    public String getCHKNUM()
    {
       return CHKNUM;
    }

    public void setCHKNUM(String CHKNUM)
    {
       this.CHKNUM = CHKNUM;
    }

    public String getDED10()
    {
       return DED10;
    }

    public void setDED10(String DED10)
    {
       this.DED10 = DED10;
    }

    public String getDED5()
    {
       return DED5;
    }

    public void setDED5(String DED5)
    {
       this.DED5 = DED5;
    }

    public String getDED6()
    {
       return DED6;
    }

    public void setDED6(String DED6)
    {
       this.DED6 = DED6;
    }

    public String getDED7()
    {
       return DED7;
    }

    public void setDED7(String DED7)
    {
       this.DED7 = DED7;
    }

    public String getDED8()
    {
       return DED8;
    }

    public void setDED8(String DED8)
    {
       this.DED8 = DED8;
    }

    public String getDED9()
    {
       return DED9;
    }

    public void setDED9(String DED9)
    {
       this.DED9 = DED9;
    }

    public String getEN()
    {
       return EN;
    }

    public void setEN(String EN)
    {
       this.EN = EN;
    }

    public String getFED_WH()
    {
       return FED_WH;
    }

    public void setFED_WH(String FED_WH)
    {
       this.FED_WH = FED_WH;
    }

    public String getFLI()
    {
       return FLI;
    }

    public void setFLI(String FLI)
    {
       this.FLI = FLI;
    }

    public Double getGROSS()
    {
       return GROSS;
    }

    public void setGROSS(Double GROSS)
    {
       this.GROSS = GROSS;
    }

    public String getHOURS10()
    {
       return HOURS10;
    }

    public void setHOURS10(String HOURS10)
    {
       this.HOURS10 = HOURS10;
    }

    public String getHOURS5()
    {
       return HOURS5;
    }

    public void setHOURS5(String HOURS5)
    {
       this.HOURS5 = HOURS5;
    }

    public String getHOURS6()
    {
       return HOURS6;
    }

    public void setHOURS6(String HOURS6)
    {
       this.HOURS6 = HOURS6;
    }

    public String getHOURS7()
    {
       return HOURS7;
    }

    public void setHOURS7(String HOURS7)
    {
       this.HOURS7 = HOURS7;
    }

    public String getHOURS8()
    {
       return HOURS8;
    }

    public void setHOURS8(String HOURS8)
    {
       this.HOURS8 = HOURS8;
    }

    public String getHOURS9()
    {
       return HOURS9;
    }

    public void setHOURS9(String HOURS9)
    {
       this.HOURS9 = HOURS9;
    }

    public String getLOC_WH()
    {
       return LOC_WH;
    }

    public void setLOC_WH(String LOC_WH)
    {
       this.LOC_WH = LOC_WH;
    }

    public String getMED()
    {
       return MED;
    }

    public void setMED(String MED)
    {
       this.MED = MED;
    }

    public String getMISC1()
    {
       return MISC1;
    }

    public void setMISC1(String MISC1)
    {
       this.MISC1 = MISC1;
    }

    public String getNAME()
    {
       return NAME;
    }

    public void setNAME(String NAME)
    {
       this.NAME = NAME;
    }

    public String getNHOURS()
    {
       return NHOURS;
    }

    public void setNHOURS(String NHOURS)
    {
       this.NHOURS = NHOURS;
    }

    public String getOHOURS()
    {
       return OHOURS;
    }

    public void setOHOURS(String OHOURS)
    {
       this.OHOURS = OHOURS;
    }

    public String getREIMB()
    {
       return REIMB;
    }

    public void setREIMB(String REIMB)
    {
       this.REIMB = REIMB;
    }

    public String getSDI()
    {
       return SDI;
    }

    public void setSDI(String SDI)
    {
       this.SDI = SDI;
    }

    public String getSHOURS()
    {
       return SHOURS;
    }

    public void setSHOURS(String SHOURS)
    {
       this.SHOURS = SHOURS;
    }

    public String getSOC()
    {
       return SOC;
    }

    public void setSOC(String SOC)
    {
       this.SOC = SOC;
    }

    public String getST_WH()
    {
       return ST_WH;
    }

    public void setST_WH(String ST_WH)
    {
       this.ST_WH = ST_WH;
    }

    public String getSUI()
    {
       return SUI;
    }

    public void setSUI(String SUI)
    {
       this.SUI = SUI;
    }

    public String getTIPS()
    {
       return TIPS;
    }

    public void setTIPS(String TIPS)
    {
       this.TIPS = TIPS;
    }

    public String getVHOURS()
    {
       return VHOURS;
    }

    public void setVHOURS(String VHOURS)
    {
       this.VHOURS = VHOURS;
    }

    @Override
    public String toString()
    {
       return "Payroll{" +
              "time='" + time + '\'' +
              ", ATSAV=" + ATSAV +
              ", BEN=" + BEN +
              ", BTSAV=" + BTSAV +
              ", CHKDATE='" + CHKDATE + '\'' +
              ", CHKNUM=" + CHKNUM +
              ", DED10=" + DED10 +
              ", DED5=" + DED5 +
              ", DED6=" + DED6 +
              ", DED7=" + DED7 +
              ", DED8=" + DED8 +
              ", DED9=" + DED9 +
              ", EN=" + EN +
              ", FED_WH=" + FED_WH +
              ", FLI=" + FLI +
              ", GROSS=" + GROSS +
              ", HOURS10=" + HOURS10 +
              ", HOURS5=" + HOURS5 +
              ", HOURS6=" + HOURS6 +
              ", HOURS7=" + HOURS7 +
              ", HOURS8=" + HOURS8 +
              ", HOURS9=" + HOURS9 +
              ", LOC_WH=" + LOC_WH +
              ", MED=" + MED +
              ", MISC1=" + MISC1 +
              ", NAME='" + NAME + '\'' +
              ", NHOURS=" + NHOURS +
              ", OHOURS=" + OHOURS +
              ", REIMB=" + REIMB +
              ", SDI=" + SDI +
              ", SHOURS=" + SHOURS +
              ", SOC=" + SOC +
              ", ST_WH=" + ST_WH +
              ", SUI=" + SUI +
              ", TIPS=" + TIPS +
              ", VHOURS=" + VHOURS +
              '}';
    }
 }
