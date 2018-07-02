 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      ExcelWritter.java
  * Created:   6/1/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.excel;

 import java.io.FileOutputStream;
 import java.io.OutputStream;
 import java.util.List;

 import org.apache.poi.ss.usermodel.Cell;
 import org.apache.poi.ss.usermodel.CellStyle;
 import org.apache.poi.ss.usermodel.CreationHelper;
 import org.apache.poi.ss.usermodel.FillPatternType;
 import org.apache.poi.ss.usermodel.IndexedColors;
 import org.apache.poi.ss.usermodel.Row;
 import org.apache.poi.ss.usermodel.Sheet;
 import org.apache.poi.ss.usermodel.Workbook;
 import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class ExcelWritter
 {
    Logger log = LoggerFactory.getLogger(getClass());

    protected String FILE = "/tmp/ar-collections.xls";

    public void writeSpreadSheet(List<String> titles,
                                 List<List<Object>> rows)
    {
       log.info("running...");
       Workbook wb = new XSSFWorkbook();

       CreationHelper createHelper = wb.getCreationHelper();
       Sheet sheet = wb.createSheet("Accounts Receivable");


       int rowIdx = 0;
       int colIdx = 0;

       Row row = sheet.createRow(rowIdx);
       CellStyle backgroundStyle = wb.createCellStyle();
       backgroundStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
       backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


       for (String cellValue : titles)
       {
          Cell cell = row.createCell(colIdx);
          cell.setCellValue(cellValue);
          cell.setCellStyle(backgroundStyle);
          colIdx++;
       }

       rowIdx++;

       for (List<Object> rowValues : rows)
       {
          row = sheet.createRow(rowIdx);

          colIdx = 0;
          for (Object cellValue : rowValues)
          {
             Cell cell = row.createCell(colIdx);
             if (cellValue instanceof String)
                cell.setCellValue((String) cellValue);
             else if (cellValue instanceof Double)
                cell.setCellValue((Double) cellValue);
             else if (cellValue == null)
                cell.setCellValue("");
             else
                log.info("Unknown type for excel writer.");

             colIdx++;
          }

          rowIdx++;
       }

       sheet.createFreezePane(0, 1);

       try
       {
          OutputStream fileOut = new FileOutputStream("/Users/anthonygreway/Desktop/workbook.xlsx");
          wb.write(fileOut);
       }
       catch (Exception e)
       {
          log.error("Failed to write workbook.", e);
       }
       log.info("done...");
    }

 }
