 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DBConnect.java
  * Created:   6/26/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.persistance.dao.sql;

 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.Vector;

 import javax.swing.table.DefaultTableModel;

 import com.sentryfire.model.WO;
 import org.joda.time.DateTime;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class DBConnect
 {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Connection cnn;

    protected DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static boolean isConnected = false;

    public void connectToDB(String server,
                            String db,
                            String user,
                            String password)
    {
       synchronized (this)
       {
          if (isConnected)
          {
             return;
          }

          try
          {
             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");


             String connectionString = "jdbc:sqlserver://" + server + ";DatabaseName=" + db;
             log.info("Connecting to the DB: " + connectionString);

             cnn = DriverManager.getConnection(connectionString, user, password);
             String message = "Connection was successful!";
             isConnected = true;
             log.info(message);
          }
          catch (Exception ex)
          {
             String message = "Can not open connection !";
             log.error(message, ex);
          }
       }
    }

    public DefaultTableModel getCustomerList()
    {
       String query = "select * from cst;";
       return getDataTable(query);
    }

    public DefaultTableModel searchCustomerCN(String searchString)
    {
       long cn = -1L;
       try
       {
          cn = Long.parseLong(searchString);
       }
       catch (Exception e)
       {
          return null;
       }

       String selectString = "SELECT * FROM cst WHERE cn = " + cn;

       return getDataTable(selectString);
    }

    public DefaultTableModel searchCustomer(String searchString)
    {
       String selectString =
          "SELECT * FROM cst WHERE " +
          "REPLACE(REPLACE(REPLACE(REPLACE(name,  '#',''), '*', ''), '\"', ''), char(39), '') like '" + searchString + "%'" +
          " or REPLACE(REPLACE(REPLACE(REPLACE(adr1,  '#',''), '*', ''), '\"', ''), char(39), '') like '" + searchString + "%'" +
          " or REPLACE(REPLACE(REPLACE(REPLACE(adr2,  '#',''), '*', ''), '\"', ''), char(39), '') like '" + searchString + "%'";

       return getDataTable(selectString);
    }

    public DefaultTableModel getUserActivityLog(DateTime start,
                                                DateTime end)
    {
       String selectString = "select * from LOG  WHERE " +
                             "(LOG.DATE >= { d '" + formatter.print(start) + "'} ) AND (LOG.DATE <= { d '" + formatter.print(end) + "'} )";
       return getDataTable(selectString);
    }


    public DefaultTableModel getItemsForWorkOrder(DateTime start,
                                                  DateTime end)
    {
       String selectString = "SELECT " +
                             "CASE RPG.COST " +
                             "  WHEN 0 THEN RPG.POCOST " +
                             "  ELSE RPG.POCOST* RPG.HQ2 " +
                             "END AS TOTCOST, " +
                             "RPG.[COUNTER],RPG.CN,RPG.[IN],RPG.PAGENUM,RPG.POCOST,RPG.IC, " +
                             "RPG.NAME,RPG.HQ,RPG.HQ2,RPG.RP,RPG.COST,RPG.IDATE,RPG.CSDATE, " +
                             "RPG.DEPT,RPG.ACC1,RPG.ACC2,RPG.INOTE,RPG.MISC1,RPG.TYPE, RCV.DEPT as DIVISION, RCV.IN2 " +
                             //",RPG.POCOST * RPG.HQ2 as TOTCOST " +
                             "FROM RPG,RCV,CST,RCVT WHERE(((((((((({ fn length(RPG.IC )}> 0 ) " +
                             "AND NOT((RPG.IC LIKE 'H_FLATRATE%' ))) " +

//                             "AND(RPG.IDATE like '%201%' ))))" +


                             "AND (RPG.IDATE >= { d '" + formatter.print(start) + "'} ) AND (RPG.IDATE <= { d '" + formatter.print(end) + "'} )))) " +

                             "AND(RPG.TYPE IN(1, 2, 3, 4)) ) AND " +
                             "((RCV.CN = RPG.CN) AND(RCV.[IN] = RPG.[IN]))) AND " +
                             "((RCV.FRM = 1) AND (RCV.AUTOWIP = 0))) " +
                             "AND(CST.CN = RCV.CN) ) AND(RCV.COUNTER = RCVT.RCVPK))";

       return getDataTable(selectString);
    }

    public DefaultTableModel getItemsForWorkOrder(String in2)
    {
       String selectString = "SELECT " +
                             "CASE RPG.COST " +
                             "  WHEN 0 THEN RPG.POCOST " +
                             "  ELSE RPG.POCOST* RPG.HQ2 " +
                             "END AS TOTCOST, " +
                             "RPG.[COUNTER],RPG.CN,RPG.[IN],RPG.PAGENUM,RPG.POCOST,RPG.IC, " +
                             "RPG.NAME,RPG.HQ,RPG.HQ2,RPG.RP,RPG.COST,RPG.IDATE,RPG.CSDATE, " +
                             "RPG.DEPT,RPG.ACC1,RPG.ACC2,RPG.INOTE,RPG.MISC1, RPG.TYPE, RCV.DEPT as DIVISION, RCV.IN2 " +
                             //",RPG.POCOST * RPG.HQ2 as TOTCOST " +
                             "FROM RPG,RCV,CST,RCVT WHERE(((((((((({ fn length(RPG.IC )}> 0 ) " +
                             "AND NOT((RPG.IC LIKE 'H_FLATRATE%' ))) " +

                             "AND(RPG.IDATE like '%201%' ))))" +


//                             "AND (RPG.IDATE >= { d '" + formatter.print(start) + "'} ) AND (RPG.IDATE <= { d '" + formatter.print(end) + "'}) AND(RPG.MISC1 LIKE 'ID%')))) " +

                             "AND(RPG.TYPE IN(1, 2, 3, 4)) ) AND " +
                             "((RCV.CN = RPG.CN) AND(RCV.[IN] = RPG.[IN]))) AND " +
                             "((RCV.FRM = 1) AND (RCV.AUTOWIP = 0))) " +
                             "AND(CST.CN = RCV.CN) ) AND(RCV.COUNTER = RCVT.RCVPK) AND RCV.IN2 = '" + in2 + "')";

       return getDataTable(selectString);
    }

    public DefaultTableModel getInvoiceItemTable()
    {
       try
       {
          //String selectString = "SELECT RPG.COUNTER,RPG.RPG.CN,RPG.[IN],RPG.PAGENUM,RPG.POCOST,RPG.IC," +
          //   "RPG.NAME,RPG.HQ,RPG.HQ2,RPG.RP,RPG.COST,RPG.IDATE,RPG.CSDATE," +
          //  "RPG.DEPT,RPG.ACC1,RPG.ACC2,RPG.INOTE,RPG.MISC1,RPG.POCOST * RPG.HQ2 as TOTCOST" +
          // " FROM RPG,RCV,CST,RCVT WHERE IDATE IS NOT NULL AND IDATE like '%2017%' AND RPG.COST > 0 AND RPG.HQ2 > 0 " +
          // "AND (RPG.TYPE IN (1 ,2 ,3 ,4 ) AND NOT(RPG.IC LIKE 'H_FLATRATE%' )";

          String selectString = "SELECT " +
                                "CASE RPG.COST " +
                                "  WHEN 0 THEN RPG.POCOST " +
                                "  ELSE RPG.POCOST* RPG.HQ2 " +
                                "END AS TOTCOST, " +
                                "RPG.[COUNTER],RPG.CN,RPG.[IN],RPG.PAGENUM,RPG.POCOST,RPG.IC, " +
                                "RPG.NAME,RPG.HQ,RPG.HQ2,RPG.RP,RPG.COST,RPG.IDATE,RPG.CSDATE, " +
                                "RPG.DEPT,RPG.ACC1,RPG.ACC2,RPG.INOTE,RPG.MISC1, RPG.TYPE, RCV.DEPT as DIVISION, RCV.IN2 " +
                                //",RPG.POCOST * RPG.HQ2 as TOTCOST " +
                                "FROM RPG,RCV,CST,RCVT WHERE(((((((((({ fn length(RPG.IC )}> 0 ) " +
                                "AND NOT((RPG.IC LIKE 'H_FLATRATE%' ))) " +

                                "AND(RPG.IDATE like '%201%' ))))" +

                                //"AND (RPG.IDATE >= { d '2017-10-15'} ) AND (RPG.IDATE <= { d '2017-10-16'}) AND(RPG.MISC1 LIKE 'ID%')))) " +

                                "AND(RPG.TYPE IN(1, 2, 3, 4)) ) AND " +
                                "((RCV.CN = RPG.CN) AND(RCV.[IN] = RPG.[IN]))) AND " +
                                "((RCV.FRM = 2) AND (RCV.AUTOWIP = 0))) " +
                                "AND(CST.CN = RCV.CN) ) AND(RCV.COUNTER = RCVT.RCVPK))";
          //" FROM dbo.RPG WHERE((CN = 6699) AND([IN] = 123)) " +
          //" ORDER BY dbo.RPG.CN ,dbo.RPG.[IN], dbo.RPG.PAGENUM";

          return getDataTable(selectString);
       }
       catch (Exception e)
       {
          log.error("Error querying for customers.", e);
       }
       return null;
    }

    public DefaultTableModel getPayrollDataTable(DateTime start,
                                                 DateTime end)
    {
       String selectString = "SELECT PAY.EN , PAY.NAME , PAY.CHKNUM , PAY.CHKDATE , PAY.NHOURS , PAY.OHOURS , PAY.SHOURS , PAY.VHOURS , PAY.HOURS5 , PAY.HOURS6 , PAY.HOURS7 , PAY.HOURS8 , PAY.HOURS9 , PAY.HOURS10 , " +
                             "PAY.GROSS , PAY.TIPS , PAY.FED_WH , PAY.ST_WH , PAY.LOC_WH , PAY.SOC , PAY.MED , PAY.BEN , PAY.ATSAV , PAY.BTSAV , PAY.REIMB , PAY.MISC1 , PAY.DED5 , PAY.DED6 , PAY.DED7 , PAY.DED8 , PAY.DED9 , PAY.DED10 , PAY.SDI , PAY.FLI , PAY.SUI " +
                             "FROM PAY, EMP " +
                             "WHERE((PAY.CHKDATE BETWEEN { d '" + formatter.print(start) + "'} AND { d '" + formatter.print(end) + "'} ) " +
                             "AND (PAY.EN = EMP.EN) )";
       return getDataTable(selectString);
    }

    /// <summary>
    ///
    /// <param name="rcvForm"></param>  =
    /// 1 = WO
    /// 2 = Invoice
    /// 3 = PO?
    /// <returns></returns>
    public DefaultTableModel getRCVDataTable(DateTime start,
                                             DateTime end,
                                             int rcvForm)
    {
       String selectString = "SELECT CST.CN, CST.SAL, CST.EMAIL, CST.NAME, CST.TEL as CST_TEL, RCV.TEL as WIP_TEL, RCV.FAX as WIP_CELL, RCV.CITY, RCV.STATE, RCV.ZIP, RCV.ADR1, RCV.INVRMK, RCV.DEPT, RCV.NAME, RCV.IN2, RCV.JDATE, RCV.COUNTER, RCVN.RCVNKey, RCVT.TECH, RCV.SUBTOTAL, RCV.TOTTAX, RCV.TOTTAX2, RCV.MAT, RCV.LAB, RCV.MATC, RCV.LABC " +
                             "FROM RCV, RCVT, RCVN, CST " +
                             "WHERE (" +
                             "(((((RCV.FRM = " + rcvForm + " ) AND (NOT((RCV.JSTAT = '*' )) OR (RCV.JSTAT IS NULL ))) " +
                             "AND (RCV.COUNTER = RCVT.RCVPK )) " +
                             "AND (RCVT.DATE BETWEEN {d '" + start.toString("yyyy'-'MM'-'dd") + "'} AND {d '" + end.toString("yyyy'-'MM'-'dd") + "'} ) ) " +
                             "AND ((RCV.\"IN\" = RCVN.\"IN\" ) AND (RCV.CN = RCVN.CN ))) " + "AND (CST.CN = RCV.CN )) AND RCV.AUTOWIP = 0";

       return getDataTable(selectString);
    }

    public DefaultTableModel getAllRCVValues(String cn,
                                             String in)
    {
       String selectString = "SELECT * FROM RCV WHERE cn = " + cn + " AND [in] = " + in;
       return getDataTable(selectString);
    }

    public DefaultTableModel getARDataTable(DateTime end)
    {
       String selectString =
          "Select RCV.CN, RCV.[IN], RCV.IN2, RCV.[NAME],RCV.DEPT, RCV.MISC1, RCV.INVRMK, RCV.FRM, RCV.INVDATE, RCV.SUBTOTAL," +
          "RCV.TOTTAX, RCV.TOTTAX2, RCV.TOTTAX3, RCV.TOTTAX4, RCV.TOTTAX5, RCV.MISC1, RCV.PDUE, RCV.INVRMK, " +
          "RCV.DUEDATE, RCV.BALANCE, CST.[EMAIL], CST.[EMAIL2], CST.[NAME] AS CSTNAME, CST.[FNAME] AS CSTFNAME, CST.[TEL] AS CSTTEL," +
          " ISNULL(" +
          " (select SUM(ISNULL(RCT.AMOUNT, 0))" +
          "       + SUM(ISNULL(RCT.TAXPD, 0))" +
          "       + SUM(ISNULL(RCT.DISC, 0))" +
          "   from RCT" +
          "   where ((RCT.CN = RCV.CN and RCT.[IN] = RCV.[IN])) " +
          "   And(DateDiff(\"D\", RCT.[DATE], '" + formatter.print(end) + "') >= 0)" +
          "    ), 0) AS RECEIPTS_PAID_AMOUNT FROM RCV" +
          "    INNER JOIN CST ON RCV.CN = CST.CN" +
          "      Where((RCV.FRM = 2)  " +
          //"      AND(RCV.[DEPT] LIKE 'CO SPRINGS%') " +
          "      AND(RCV.[INVDATE] <= '" + formatter.print(end) + "')" +
          "      And((RCV.SUBTOTAL + RCV.TOTTAX + RCV.TOTTAX2 + RCV.TOTTAX3 + RCV.TOTTAX4 + RCV.TOTTAX5) - (ISNULL((select SUM(ISNULL(RCT.AMOUNT, 0))+ SUM(ISNULL(RCT.TAXPD, 0))+SUM(ISNULL(RCT.DISC, 0)) from RCT where((RCT.CN= RCV.CN and RCT.[IN]= RCV.[IN])) " +
          "      And(DateDiff(\"D\", RCT.[DATE], '" + formatter.print(end) + "') >= 0)), 0)) <> 0) ) Order by RCV.INVDATE, RCV.[NAME]";

       return getDataTable(selectString);
    }

    public boolean updateWOTech(String tech,
                                WO wo)
    {
       boolean success = false;

       try
       {
          String updateRCVString = "update RCV set MISC1 = '" + tech + "' where IN2 = '" + wo.getIN2() + "'";
          updateTable(updateRCVString);
          String updateRCVTString = "update RCVT set TECH = '" + tech + "' where RCVPK = '" + wo.getCOUNTER() + "'";
          updateTable(updateRCVTString);
          success = true;
       }
       catch (Exception e)
       {
          log.error("Failed to update " + wo.getIN2() + " " + wo.getRCVNKey() + " " + tech);
       }
       return success;
    }

    public DefaultTableModel getDataTable(String selectString)
    {
       try
       {
          log.debug("GetDataTable selectString: " + selectString);
          Statement stmt = cnn.createStatement();
          ResultSet rs = stmt.executeQuery(selectString);
          DefaultTableModel dataTable = buildTableModel(rs);


          log.debug("Found: " + dataTable.getRowCount());

          return dataTable;
       }
       catch (Exception e)
       {
          log.error("Failed to execute " + selectString, e);
       }
       return null;
    }

    private void updateTable(String updateString)
    {
       try
       {
          log.debug("Update String: " + updateString);
          Statement stmt = cnn.createStatement();
          boolean pass = stmt.execute(updateString);

          log.debug("Result: " + pass);
       }
       catch (Exception e)
       {
          log.error("Failed to execute " + updateString, e);
       }
    }

    public DefaultTableModel getWorkOrdersByTime(DateTime start,
                                                 DateTime end)
    {
       return getRCVDataTable(start, end, 1);
    }

    public DefaultTableModel getInvoices(DateTime start,
                                         DateTime end)
    {
       return getRCVDataTable(start, end, 2);
    }

    public DefaultTableModel getPurchaseOrders(DateTime start,
                                               DateTime end)
    {
       return getRCVDataTable(start, end, 5);
    }

    public DefaultTableModel getProposals(DateTime start,
                                          DateTime end)
    {
       return getRCVDataTable(start, end, 3);
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
       throws SQLException
    {

       ResultSetMetaData metaData = rs.getMetaData();

       // names of columns
       Vector<String> columnNames = new Vector<String>();
       int columnCount = metaData.getColumnCount();
       for (int column = 1; column <= columnCount; column++)
       {
          columnNames.add(metaData.getColumnName(column));
       }

       // data of the table
       Vector<Vector<Object>> data = new Vector<Vector<Object>>();
       while (rs.next())
       {
          Vector<Object> vector = new Vector<Object>();
          for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
          {
             vector.add(rs.getObject(columnIndex));
          }
          data.add(vector);
       }

       return new DefaultTableModel(data, columnNames);
    }


    public void closeDBConnection()
    {
       synchronized (this)
       {
          try
          {
             if (cnn != null)
                cnn.close();
          }
          catch (Exception e)
          {
             log.error("Failed to close connection to DB!", e);
             return;
          }
          log.info("Closed connection to DB!");
          isConnected = false;
       }
    }

 }
