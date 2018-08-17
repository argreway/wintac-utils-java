 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      WO.java
  * Created:   6/22/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.io.Serializable;
 import java.util.List;
 import java.util.Objects;

 import org.influxdb.annotation.Column;
 import org.influxdb.annotation.Measurement;

 @Measurement (name = "WO")
 public class WO implements Serializable
 {

    @Column (name = "time")
    protected String time;
    @Column (name = "ADR1")
    protected String ADR1;
    @Column (name = "CN")
    protected String CN;
    @Column (name = "DEPT")
    protected String DEPT;
    @Column (name = "COUNTER")
    protected String COUNTER;
    @Column (name = "IN2")
    protected String IN2;
    @Column (name = "JDATE")
    protected String JDATE;
    @Column (name = "LAB")
    protected String LAB;
    @Column (name = "LABC")
    protected String LABC;
    @Column (name = "LABC")
    protected String INVDATE;
    @Column (name = "TECH")
    protected String TECH;
    @Column (name = "INVRMK")
    protected String INVRMK;
    @Column (name = "NAME")
    protected String NAME;
    @Column (name = "NAME1")
    protected String NAME1;
    @Column (name = "SUBTOTAL")
    protected String SUBTOTAL;
    @Column (name = "TOTTAX")
    protected String TOTTAX;
    @Column (name = "TOTTAX2")
    protected String TOTTAX2;
    @Column (name = "MAT")
    protected String MAT;
    @Column (name = "MATC")
    protected String MATC;
    @Column (name = "RCVNKey")
    protected String RCVNKey;
    @Column (name = "CITY")
    protected String CITY;
    @Column (name = "STATE")
    protected String STATE;
    @Column (name = "ZIP")
    protected String ZIP;
    @Column (name = "SAL")
    protected String SAL;
    @Column (name = "CST_TEL")
    protected String CST_TEL;
    @Column (name = "WIP_TEL")
    protected String WIP_TEL;
    @Column (name = "WIP_CELL")
    protected String WIP_CELL;
    @Column (name = "EMAIL")
    protected String EMAIL;

    protected List<Item> lineItems;

    protected WOMeta metaData = new WOMeta();


    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getADR1()
    {

       return ADR1 == null ? "" : ADR1;
    }

    public void setADR1(String ADR1)
    {
       this.ADR1 = ADR1;
    }

    public String getCN()
    {
       return CN;
    }

    public void setCN(String CN)
    {
       this.CN = CN;
    }

    public String getDEPT()
    {
       return DEPT;
    }

    public void setDEPT(String DEPT)
    {
       this.DEPT = DEPT;
    }

    public String getCOUNTER()
    {
       return COUNTER;
    }

    public void setCOUNTER(String COUNTER)
    {
       this.COUNTER = COUNTER;
    }

    public String getIN2()
    {
       return IN2;
    }

    public void setIN2(String IN2)
    {
       this.IN2 = IN2;
    }

    public String getJDATE()
    {
       return JDATE;
    }

    public void setJDATE(String JDATE)
    {
       this.JDATE = JDATE;
    }

    public String getLAB()
    {
       return LAB;
    }

    public void setLAB(String LAB)
    {
       this.LAB = LAB;
    }

    public String getLABC()
    {
       return LABC;
    }

    public void setLABC(String LABC)
    {
       this.LABC = LABC;
    }

    public String getINVDATE()
    {
       return INVDATE;
    }

    public void setINVDATE(String INVDATE)
    {
       this.INVDATE = INVDATE;
    }

    public String getTECH()
    {
       return TECH;
    }

    public void setTECH(String TECH)
    {
       this.TECH = TECH;
    }

    public String getINVRMK()
    {
       return INVRMK;
    }

    public void setINVRMK(String INVRMK)
    {
       this.INVRMK = INVRMK;
    }

    public String getNAME()
    {
       return NAME;
    }

    public void setNAME(String NAME)
    {
       this.NAME = NAME;
    }

    public String getNAME1()
    {
       return NAME1;
    }

    public void setNAME1(String NAME1)
    {
       this.NAME1 = NAME1;
    }

    public String getSUBTOTAL()
    {
       return SUBTOTAL;
    }

    public void setSUBTOTAL(String SUBTOTAL)
    {
       this.SUBTOTAL = SUBTOTAL;
    }

    public String getTOTTAX()
    {
       return TOTTAX;
    }

    public void setTOTTAX(String TOTTAX)
    {
       this.TOTTAX = TOTTAX;
    }

    public String getTOTTAX2()
    {
       return TOTTAX2;
    }

    public void setTOTTAX2(String TOTTAX2)
    {
       this.TOTTAX2 = TOTTAX2;
    }

    public String getMAT()
    {
       return MAT;
    }

    public void setMAT(String MAT)
    {
       this.MAT = MAT;
    }

    public String getMATC()
    {
       return MATC;
    }

    public void setMATC(String MATC)
    {
       this.MATC = MATC;
    }

    public String getRCVNKey()
    {
       return RCVNKey;
    }

    public void setRCVNKey(String RCVNKey)
    {
       this.RCVNKey = RCVNKey;
    }

    public List<Item> getLineItems()
    {
       return lineItems;
    }

    public void setLineItems(List<Item> lineItems)
    {
       this.lineItems = lineItems;
    }

    public WOMeta getMetaData()
    {
       return metaData;
    }

    public void setMetaData(WOMeta metaData)
    {
       this.metaData = metaData;
    }

    public String getCITY()
    {
       return CITY == null ? "" : CITY;
    }

    public void setCITY(String CITY)
    {
       this.CITY = CITY;
    }

    public String getSTATE()
    {
       return STATE;
    }

    public void setSTATE(String STATE)
    {
       this.STATE = STATE;
    }

    public String getZIP()
    {
       return ZIP == null ? "" : ZIP;
    }

    public void setZIP(String ZIP)
    {
       this.ZIP = ZIP;
    }

    public String getSAL()
    {
       return SAL;
    }

    public void setSAL(String SAL)
    {
       this.SAL = SAL;
    }

    public String getCST_TEL()
    {
       return CST_TEL;
    }

    public void setCST_TEL(String CST_TEL)
    {
       this.CST_TEL = CST_TEL;
    }

    public String getWIP_TEL()
    {
       return WIP_TEL;
    }

    public void setWIP_TEL(String WIP_TEL)
    {
       this.WIP_TEL = WIP_TEL;
    }

    public String getWIP_CELL()
    {
       return WIP_CELL;
    }

    public void setWIP_CELL(String WIP_CELL)
    {
       this.WIP_CELL = WIP_CELL;
    }

    public String getEMAIL()
    {
       return EMAIL;
    }

    public void setEMAIL(String EMAIL)
    {
       this.EMAIL = EMAIL;
    }

    public String getFullAddress()
    {
       String result = getADR1() + " " + getCITY() + " " + getZIP();
       return convert(result);
    }

    public static String convert(String item)
    {
       if (item == null)
          return item;
       return item.replace("_", " ");
    }

    @Override
    public boolean equals(Object o)
    {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       WO wo = (WO) o;
       return Objects.equals(IN2, wo.IN2);
    }

    @Override
    public int hashCode()
    {

       return Objects.hash(IN2);
    }

    @Override
    public String toString()
    {
       return "WO{" +
              "time='" + time + '\'' +
              ", ADR1='" + ADR1 + '\'' +
              ", CN='" + CN + '\'' +
              ", DEPT='" + DEPT + '\'' +
              ", COUNTER='" + COUNTER + '\'' +
              ", IN2='" + IN2 + '\'' +
              ", JDATE='" + JDATE + '\'' +
              ", LAB='" + LAB + '\'' +
              ", LABC='" + LABC + '\'' +
              ", INVDATE='" + INVDATE + '\'' +
              ", TECH='" + TECH + '\'' +
              ", INVRMK='" + INVRMK + '\'' +
              ", NAME='" + NAME + '\'' +
              ", NAME1='" + NAME1 + '\'' +
              ", SUBTOTAL='" + SUBTOTAL + '\'' +
              ", TOTTAX='" + TOTTAX + '\'' +
              ", TOTTAX2='" + TOTTAX2 + '\'' +
              ", MAT='" + MAT + '\'' +
              ", MATC='" + MATC + '\'' +
              ", RCVNKey='" + RCVNKey + '\'' +
              ", CITY='" + CITY + '\'' +
              ", STATE='" + STATE + '\'' +
              ", ZIP='" + ZIP + '\'' +
              ", SAL='" + SAL + '\'' +
              ", CST_TEL='" + CST_TEL + '\'' +
              ", WIP_TEL='" + WIP_TEL + '\'' +
              ", WIP_CELL='" + WIP_CELL + '\'' +
              ", EMAIL='" + EMAIL + '\'' +
              ", lineItems=" + lineItems +
              ", metaData=" + metaData +
              '}';
    }
 }
