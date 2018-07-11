 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      HistoryDataUtils.java
  * Created:   7/5/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.history;

 import com.sentryfire.business.history.finance.QueryAggregator;
 import com.sentryfire.business.history.workorder.WOHistoryManager;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class HistoryDataUtils
 {
    private static Logger log = LoggerFactory.getLogger(HistoryDataUtils.class);

    static WOHistoryManager woHistoryManager = new WOHistoryManager();
    static QueryAggregator queryAggregator = new QueryAggregator();

    public static void insertAllHistoryStats()
    {
       updateWOCounts();
       updateLaborEfficency();
       updateHistoryWOAndItems();
    }

    public static void updateHistoryWOAndItems()
    {
       log.info("Updating History WO Items.");
       woHistoryManager.updateWOAndItems();
    }

    public static void updateLaborEfficency()
    {
       log.info("Updating Labor Efficency Ratios");
       queryAggregator.laborEfficencyRatiosYearly();
    }

    public static void updateWOCounts()
    {
       log.info("Updating WO Counts in History.");
       woHistoryManager.updateMonthlyWOCount();
    }

 }
