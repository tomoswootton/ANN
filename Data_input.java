import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This program reads from XLSX file in Java using Apache POI.
**/
class Data_input {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        readFromExcel("FresnoDataCOC102Student.xlsx");
        System.out.println("running");
    }

    public static void readFromExcel(String file) throws IOException{
      XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
      XSSFSheet myExcelSheet = myExcelBook.getSheet("Birthdays");
      XSSFRow row = myExcelSheet.getRow(0);

      if(row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING){
          String name = row.getCell(0).getStringCellValue();
          System.out.println("NAME : " + name);
      }

      if(row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
          Date birthdate = row.getCell(1).getDateCellValue();
          System.out.println("DOB :" + birthdate);
      }

      myExcelBook.close();

  }
}
