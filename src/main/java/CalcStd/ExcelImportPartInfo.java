package CalcStd;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImportPartInfo{
    private static String getCellText(Cell cell) {
        String result;
        if(cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                result = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue() + "";
                } else {
                    result = cell.getNumericCellValue() + "";
                }
                break;
            case BOOLEAN:
                result = cell.getBooleanCellValue() + "";
                break;
            case FORMULA:
                result = cell.getCellFormula() + "";
                break;
            default:
                result = null;
                break;
        }
        return result;
    }
    public static List<PartInfoAll> parse(String file) throws IOException {
        List<PartInfoAll>resultTotal = new ArrayList<>();
        try(FileInputStream fis = new FileInputStream(file)){
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet wbSheet = wb.getSheetAt(0);
            int lineNumber = 0;
            for (Row row : wbSheet) {
                String partNumber = getCellText(row.getCell(9));
                String quantity = getCellText(row.getCell(28));
                String alternative = getCellText(row.getCell(21));
                String figItemSee = getCellText(row.getCell(23));
                String figItem = getCellText(row.getCell(26));

                resultTotal.add(new PartInfoAll(partNumber,quantity,figItem,figItemSee,alternative,lineNumber));
                lineNumber++;
            }
            resultTotal.remove(0);
            wb.close();
        }

        return resultTotal;
    }
    public static void export2(String fileName, String fileNameRes,List<PartInfoAll>infos) throws IOException{
        XSSFWorkbook wb= new XSSFWorkbook(new FileInputStream(fileName));
        XSSFSheet wbSheet = wb.getSheetAt(0);
        int colNumber = 29;
        for (PartInfoAll info:infos) {
            if(info.finalQuantity!=null){
                int rowNumber= info.lineNumber;
                XSSFRow row = wbSheet.getRow(rowNumber);
                XSSFCell cell = row.createCell(colNumber);
                cell.setCellValue(info.finalQuantity);
            }
        }
        try{
            FileOutputStream out = new FileOutputStream(fileNameRes);
            wb.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void export1(String fileName, String fileNameRes, List<PartInfoAll>infos) throws IOException{
        try(FileInputStream fis = new FileInputStream(fileName);
            FileOutputStream out = new FileOutputStream(fileNameRes)){
            XSSFWorkbook wb= new XSSFWorkbook(fis);
            XSSFSheet wbSheet = wb.getSheetAt(0);
            int colNumber = 28;
            int isStandartColumnNumber = 30;
            for (PartInfoAll info:infos) {
                if (info.countInfo != null) {
                    int rowNumber = info.lineNumber;
                    XSSFRow row = wbSheet.getRow(rowNumber);
                    XSSFCell cell = row.createCell(colNumber);
                    cell.setCellValue(info.countInfo);
                    XSSFCell cellStandart = row.createCell(isStandartColumnNumber);
                    cellStandart.setCellValue(info.isStandart);
                }
            }
            wb.write(out);
        }
    }

} 