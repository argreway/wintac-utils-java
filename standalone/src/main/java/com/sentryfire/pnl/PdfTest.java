 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      PdfTest.java
  * Created:   9/6/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.pnl;

 import java.io.File;

 import org.apache.pdfbox.pdmodel.PDDocument;
 import org.apache.pdfbox.text.PDFTextStripper;
 import org.apache.pdfbox.text.PDFTextStripperByArea;

 public class PdfTest
 {
    static String file = "/tmp/test.pdf";

    public static void main(String[] args)
    {
       run();
    }

    static void run()
    {

       try (PDDocument document = PDDocument.load(new File(file)))
       {

          document.getClass();

          if (!document.isEncrypted())
          {

             PDFTextStripperByArea stripper = new PDFTextStripperByArea();
             stripper.setSortByPosition(true);

             PDFTextStripper tStripper = new PDFTextStripper();

             String pdfFileInText = tStripper.getText(document);
             //System.out.println("Text:" + st);

             // split by whitespace
             String lines[] = pdfFileInText.split("\\r?\\n");
             for (String line : lines)
             {
                System.out.println(line);
             }

          }
       }
       catch (Exception e)
       {
          e.printStackTrace();
          System.out.println("error");
       }

    }
 }
