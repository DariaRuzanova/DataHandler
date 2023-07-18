package CalcStd;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardChecker {

    private Map<String, String> snecmaStandards;
    private Map<String, String> concepteurStandards;
    private List<String> listStandarts;

    public void init(String fileName) {
        try {
            List<Standarts> infosStd = ExcelImportStd.importStd(fileName);
            snecmaStandards = new HashMap<>();
            concepteurStandards = new HashMap<>();
            for(Standarts entry : infosStd) {
                snecmaStandards.put(entry.getStdSnecma(), entry.getStdSnecma());
                concepteurStandards.put(entry.getStdConcepteur(), entry.getStdConcepteur());
            }

            listStandarts = Arrays.asList("AN", "AS", "EN", "M", "MS", "NAS", "SP90", "TU", "212", "221", "222", "224", "233");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isStandard(String partNumber) {
        boolean result = snecmaStandards.containsKey(partNumber);
        result = result || concepteurStandards.containsKey(partNumber);
        result = result || listStandarts.stream().anyMatch(partNumber::startsWith);
        return  result;
    }
}
