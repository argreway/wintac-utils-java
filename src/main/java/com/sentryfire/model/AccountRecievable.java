 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      AccountRecievable.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.model;

 import java.lang.reflect.Field;
 import java.util.List;

 import com.google.common.collect.Lists;
 import org.influxdb.annotation.Column;
 import org.influxdb.annotation.Measurement;

 @Measurement (name = "AR")
 public class AccountRecievable
 {

    @Column (name = "time")
    protected String time;
    @Column (name = "BALANCE")
    protected String BALANCE;
    @Column (name = "CN")
    protected String CN;
    @Column (name = "DEPT")
    protected String DEPT;
    @Column (name = "FRM")
    protected String FRM;
    @Column (name = "IN")
    protected String IN;
    @Column (name = "CSTFNAME")
    protected String CSTFNAME;
    @Column (name = "CSTNAME")
    protected String CSTNAME;
    @Column (name = "CSTTEL")
    protected String CSTTEL;
    @Column (name = "DUEDATE")
    protected String DUEDATE;
    @Column (name = "EMAIL")
    protected String EMAIL;
    @Column (name = "EMAIL2")
    protected String EMAIL2;
    @Column (name = "INVATE")
    protected String INVDATE;
    @Column (name = "PDUE")
    protected String PDUE;
    @Column (name = "RECEIPTS_PAID_AMOUNT")
    protected String RECEIPTS_PAID_AMOUNT;
    @Column (name = "INVRMK")
    protected String INVRMK;
    @Column (name = "NAME")
    protected String NAME;
    @Column (name = "SUBTOTAL")
    protected String SUBTOTAL;
    @Column (name = "TOTTAX")
    protected String TOTTAX;
    @Column (name = "TOTTAX2")
    protected String TOTTAX2;
    @Column (name = "TOTTAX3")
    protected String TOTTAX3;
    @Column (name = "TOTTAX4")
    protected String TOTTAX4;
    @Column (name = "TOTTAX5")
    protected String TOTTAX5;

    public AccountRecievable()
    {
    }

    public String getCSTFNAME()
    {
       return CSTFNAME;
    }

    public void setCSTFNAME(String CSTFNAME)
    {
       this.CSTFNAME = CSTFNAME;
    }

    public String getCSTNAME()
    {
       return CSTNAME;
    }

    public void setCSTNAME(String CSTNAME)
    {
       this.CSTNAME = CSTNAME;
    }

    public String getCSTTEL()
    {
       return CSTTEL;
    }

    public void setCSTTEL(String CSTTEL)
    {
       this.CSTTEL = CSTTEL;
    }

    public String getDUEDATE()
    {
       return DUEDATE;
    }

    public void setDUEDATE(String DUEDATE)
    {
       this.DUEDATE = DUEDATE;
    }

    public String getEMAIL()
    {
       return EMAIL;
    }

    public void setEMAIL(String EMAIL)
    {
       this.EMAIL = EMAIL;
    }

    public String getEMAIL2()
    {
       return EMAIL2;
    }

    public void setEMAIL2(String EMAIL2)
    {
       this.EMAIL2 = EMAIL2;
    }

    public String getINVDATE()
    {
       return INVDATE;
    }

    public void setINVDATE(String INVDATE)
    {
       this.INVDATE = INVDATE;
    }

    public String getPDUE()
    {
       return PDUE;
    }

    public void setPDUE(String PDUE)
    {
       this.PDUE = PDUE;
    }

    public String getRECEIPTS_PAID_AMOUNT()
    {
       return RECEIPTS_PAID_AMOUNT;
    }

    public void setRECEIPTS_PAID_AMOUNT(String RECEIPTS_PAID_AMOUNT)
    {
       this.RECEIPTS_PAID_AMOUNT = RECEIPTS_PAID_AMOUNT;
    }

    public String getTime()
    {
       return time;
    }

    public void setTime(String time)
    {
       this.time = time;
    }

    public String getBALANCE()
    {
       return BALANCE;
    }

    public void setBALANCE(String BALANCE)
    {
       this.BALANCE = BALANCE;
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

    public String getFRM()
    {
       return FRM;
    }

    public void setFRM(String FRM)
    {
       this.FRM = FRM;
    }

    public String getIN()
    {
       return IN;
    }

    public void setIN(String IN)
    {
       this.IN = IN;
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

    public String getTOTTAX3()
    {
       return TOTTAX3;
    }

    public void setTOTTAX3(String TOTTAX3)
    {
       this.TOTTAX3 = TOTTAX3;
    }

    public String getTOTTAX4()
    {
       return TOTTAX4;
    }

    public void setTOTTAX4(String TOTTAX4)
    {
       this.TOTTAX4 = TOTTAX4;
    }

    public String getTOTTAX5()
    {
       return TOTTAX5;
    }

    public void setTOTTAX5(String TOTTAX5)
    {
       this.TOTTAX5 = TOTTAX5;
    }

    public List<String> getColumnNames()
    {
       List<String> columnNames = Lists.newArrayList();
       Field[] allFields = getClass().getDeclaredFields();
       for (Field field : allFields)
       {
          if (field.isAnnotationPresent(Column.class))
          {
             Column annotation = field.getAnnotation(Column.class);
             columnNames.add(annotation.name());
          }
       }
       return columnNames;
    }

    static AccountRecievable instance = new AccountRecievable();
    static Object arToObj = (Object) instance;

    public List<Object> getAllValuesAsList()
    {
       List<Object> values = Lists.newArrayList();
       Field[] allFields = getClass().getDeclaredFields();
       for (Field field : allFields)
       {
          if (field.isAnnotationPresent(Column.class) &&
              field.getType() == String.class)
          {
             try
             {
                Object val = field.get(this);
                values.add(val);
             }
             catch (Exception e)
             {
                e.printStackTrace();
             }
          }
       }
       return values;
    }

    @Override
    public String toString()
    {
       return "AccountRecievable{" +
              "CSTFNAME='" + CSTFNAME + '\'' +
              ", CSTNAME='" + CSTNAME + '\'' +
              ", CSTTEL='" + CSTTEL + '\'' +
              ", DUEDATE='" + DUEDATE + '\'' +
              ", EMAIL='" + EMAIL + '\'' +
              ", EMAIL2='" + EMAIL2 + '\'' +
              ", INVDATE='" + INVDATE + '\'' +
              ", PDUE='" + PDUE + '\'' +
              ", RECEIPTS_PAID_AMOUNT=" + RECEIPTS_PAID_AMOUNT +
              ", time='" + time + '\'' +
              ", BALANCE=" + BALANCE +
              ", CN='" + CN + '\'' +
              ", DEPT='" + DEPT + '\'' +
              ", FRM='" + FRM + '\'' +
              ", IN='" + IN + '\'' +
              ", INVRMK='" + INVRMK + '\'' +
              ", NAME='" + NAME + '\'' +
              ", SUBTOTAL='" + SUBTOTAL + '\'' +
              ", TOTTAX='" + TOTTAX + '\'' +
              ", TOTTAX2='" + TOTTAX2 + '\'' +
              ", TOTTAX3='" + TOTTAX3 + '\'' +
              ", TOTTAX4='" + TOTTAX4 + '\'' +
              ", TOTTAX5='" + TOTTAX5 + '\'' +
              '}';
    }
 }
