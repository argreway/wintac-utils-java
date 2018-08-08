 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      DailerManager.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.dailer;

 import java.util.List;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sentryfire.persistance.DAOFactory;
import com.sentryfire.model.AccountRecievable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class DailerManager
 {
    private Logger log = LoggerFactory.getLogger(getClass());
    private TwilioDailer twilioDailer = new TwilioDailer();
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public void start()
    {
//       List<AccountRecievable> series= DAOFactory.getArDao().getAllARRecords();
       List<AccountRecievable> arList = DAOFactory.getArDao().getFilteredARRecordsOlderThan2Years();
       for (AccountRecievable ar : arList)
       {
          String dept = ar.getDEPT();
          String balance = ar.getBALANCE();
          String tel = ar.getCSTTEL();

          try
          {
             Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(tel, "US");
             tel = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
             if (phoneNumber.getExtension() != null && !phoneNumber.getExtension().isEmpty())
                log.info("Extension: " + phoneNumber.getExtension());
          }
          catch (NumberParseException e)
          {
             log.error("NumberParseException was thrown: " + e.toString());
          }
          log.info(dept + " : " + balance + " : " + tel + " : " + ar.getCN());
       }
    }

 }
