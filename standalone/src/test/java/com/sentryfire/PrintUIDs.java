 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      PrintUIDs.java
  * Created:   9/22/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.sentryfire.config.AppConfiguartion;

 public class PrintUIDs extends ObjectInputStream
 {

    public PrintUIDs(InputStream in) throws IOException
    {
       super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException,
                                                             ClassNotFoundException
    {
       ObjectStreamClass descriptor = super.readClassDescriptor();
       System.out.println("name=" + descriptor.getName());
       System.out.println("serialVersionUID=" + descriptor.getSerialVersionUID());
       return descriptor;
    }

    public static void main(String[] args) throws IOException,
                                                  ClassNotFoundException
    {
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ObjectOutputStream oos = new ObjectOutputStream(baos);
       List<Object> list = Arrays.asList((Object) new Date(), UUID.randomUUID());
       oos.writeObject(list);
       oos.close();


       String fileLocation = AppConfiguartion.getInstance().getDataDirBase() + "/geo-code.ser";
       FileInputStream fileIn = new FileInputStream(fileLocation);
       ObjectInputStream in = new PrintUIDs(fileIn);
       in.readObject();

    }

 }
