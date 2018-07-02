 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      Item.java
  * Created:   6/20/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import org.influxdb.annotation.Column;
 import org.influxdb.annotation.Measurement;


 @Measurement (name = "ITEM")
 public class Item
 {
    @Column (name = "time")
    protected String time;

    @Column (name = "ACC1")
    protected String ACC1;

    @Column (name = "ACC2")
    protected String ACC2;

    @Column (name = "CN")
    protected String CN;

    @Column (name = "COST")
    protected Double COST;

    @Column (name = "COUNTER")
    protected String COUNTER;

    @Column (name = "CSDATE")
    protected String CSDATE;

    @Column (name = "DEPT")
    protected String DEPT;

    @Column (name = "HQ")
    protected Integer HQ;

    @Column (name = "HQ2")
    protected Integer HQ2;

    @Column (name = "IC")
    protected String IC;

    @Column (name = "IDATE")
    protected String IDATE;

    @Column (name = "IN")
    protected String IN;

    @Column (name = "INOTE")
    protected String INOTE;

    @Column (name = "MISC1")
    protected String MISC1;

    @Column (name = "NAME")
    protected String NAME;

    @Column (name = "PAGENUM")
    protected String PAGENUM;

    @Column (name = "POCOST")
    protected Double POCOST;

    @Column (name = "RP")
    protected Double RP;

    @Column (name = "TOTCOST")
    protected Double TOTCOST;

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getACC1()
    {
       return ACC1;
    }

    public void setACC1(String ACC1)
    {
       this.ACC1 = ACC1;
    }

    public String getACC2()
    {
       return ACC2;
    }

    public void setACC2(String ACC2)
    {
       this.ACC2 = ACC2;
    }

    public String getCN()
    {
       return CN;
    }

    public void setCN(String CN)
    {
       this.CN = CN;
    }

    public Double getCOST()
    {
       return COST;
    }

    public void setCOST(Double COST)
    {
       this.COST = COST;
    }

    public String getCOUNTER()
    {
       return COUNTER;
    }

    public void setCOUNTER(String COUNTER)
    {
       this.COUNTER = COUNTER;
    }

    public String getCSDATE()
    {
       return CSDATE;
    }

    public void setCSDATE(String CSDATE)
    {
       this.CSDATE = CSDATE;
    }

    public String getDEPT()
    {
       return DEPT;
    }

    public void setDEPT(String DEPT)
    {
       this.DEPT = DEPT;
    }

    public Integer getHQ()
    {
       return HQ;
    }

    public void setHQ(Integer HQ)
    {
       this.HQ = HQ;
    }

    public Integer getHQ2()
    {
       return HQ2;
    }

    public void setHQ2(Integer HQ2)
    {
       this.HQ2 = HQ2;
    }

    public String getIC()
    {
       return IC;
    }

    public void setIC(String IC)
    {
       this.IC = IC;
    }

    public String getIDATE()
    {
       return IDATE;
    }

    public void setIDATE(String IDATE)
    {
       this.IDATE = IDATE;
    }

    public String getIN()
    {
       return IN;
    }

    public void setIN(String IN)
    {
       this.IN = IN;
    }

    public String getINOTE()
    {
       return INOTE;
    }

    public void setINOTE(String INOTE)
    {
       this.INOTE = INOTE;
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

    public String getPAGENUM()
    {
       return PAGENUM;
    }

    public void setPAGENUM(String PAGENUM)
    {
       this.PAGENUM = PAGENUM;
    }

    public Double getPOCOST()
    {
       return POCOST;
    }

    public void setPOCOST(Double POCOST)
    {
       this.POCOST = POCOST;
    }

    public Double getRP()
    {
       return RP;
    }

    public void setRP(Double RP)
    {
       this.RP = RP;
    }

    public Double getTOTCOST()
    {
       return TOTCOST;
    }

    public void setTOTCOST(Double TOTCOST)
    {
       this.TOTCOST = TOTCOST;
    }

    @Override
    public String toString()
    {
       return "Item{" +
              "time='" + time + '\'' +
              ", ACC1='" + ACC1 + '\'' +
              ", ACC2='" + ACC2 + '\'' +
              ", CN=" + CN +
              ", COST=" + COST +
              ", COUNTER=" + COUNTER +
              ", CSDATE='" + CSDATE + '\'' +
              ", DEPT='" + DEPT + '\'' +
              ", HQ=" + HQ +
              ", HQ2=" + HQ2 +
              ", IC='" + IC + '\'' +
              ", IDATE='" + IDATE + '\'' +
              ", IN=" + IN +
              ", INOTE='" + INOTE + '\'' +
              ", MISC1='" + MISC1 + '\'' +
              ", NAME='" + NAME + '\'' +
              ", PAGENUM=" + PAGENUM +
              ", POCOST=" + POCOST +
              ", RP=" + RP +
              ", TOTCOST=" + TOTCOST +
              '}';
    }
 }
