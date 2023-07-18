package CalcStd;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImportStd {
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
    public static List<Standarts> importStd(String file) throws IOException {

        List<Standarts>listStd = new ArrayList<>();
        XSSFWorkbook wb= new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet wbSheet = wb.getSheetAt(0);

        for (Row row : wbSheet){
            String stdSnecma = getCellText(row.getCell(0));
            String stdConcepteur = getCellText(row.getCell(2));
            listStd.add(new Standarts(stdSnecma,stdConcepteur));
        }
        listStd.remove(0);
        wb.close();
        return listStd;

    }
}
