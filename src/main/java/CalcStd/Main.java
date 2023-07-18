package CalcStd;

import Common.CommonUtils;
import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileNameStd = "d:\\Daria\\S7\\CFM56\\Catalog\\Ref_FAB_std_11aout2010_pour_java.xlsx";
        StandardChecker standardChecker = new StandardChecker();
        standardChecker.init(fileNameStd);

        String fileName = "d:\\Daria\\S7\\CFM56\\Catalog\\Standarts_all_CFM56\\EIPC_CFM56-7B_FIG_ITEM_update_indC_rest_v2.xlsx";
        String exportFileName1 = "d:\\Daria\\S7\\CFM56\\Catalog\\Standarts_all_CFM56\\2_EIPC_CFM56-7B_FIG_ITEM_update_indC_rest_v2export1.xlsx";
       // String exportFileName2 = "d:\\Daria\\S7\\CFM56\\Catalog\\Standarts_all_CFM56\\SCREW\\3_SCREW_export2.xlsx";
        List<PartInfoAll> partInfoAll = ExcelImportPartInfo.parse(fileName);
        partInfoAll.forEach(x->x.figItem=x.figItem.replace("_","-"));
        partInfoAll.forEach(x -> x.isStandart = standardChecker.isStandard(x.partNumber));

        partInfoAll.forEach(x -> x.listALT = ALTinfo.extraxtALT(x.alternative));
        partInfoAll.forEach(x->x.sees=ALTinfo.extractSee(x.figItemSee));

        Map<ALTinfo.Key, PartInfoAll> infoMap = ALTinfo.getPartsMap(partInfoAll);
        Map<ALTinfo.Key, String> counts = new HashMap<>();

        ALTinfo.fillPartsCountByQTY(infoMap, counts);
        ALTinfo.fillPartsCountByNotStandard(infoMap, counts, standardChecker);
        ALTinfo.fillPartsCountByALTFromExistingCount(infoMap, counts);
        ALTinfo.fillPartsCountByStartWith(infoMap, counts, List.of("RF("));
        ALTinfo.fillPartsCountByEquals(infoMap, counts, List.of("REF", "AR"));
        ALTinfo.fillPartsCountByALTFromRF(infoMap, counts);

        for(Map.Entry<ALTinfo.Key, PartInfoAll> entry : infoMap.entrySet()) {
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            if(counts.containsKey(key)) {
                info.countInfo = counts.get(key);
            }
        }

        for(PartInfoAll info : partInfoAll) {
            if(info.countInfo == null) {
                ALTinfo.Key key = new ALTinfo.Key(info.partNumber, info.figItem);
                if(infoMap.containsKey(key) && infoMap.get(key).countInfo != null) {
                    info.countInfo = "D";
                }
            }        }

        ExcelImportPartInfo.export1(fileName,exportFileName1,partInfoAll);

//        partInfoAll.stream().filter(x -> CommonUtils.isNumeric(x.countInfo)).forEach(x->x.finalQuantity=(int) Double.parseDouble(x.countInfo));
//        //для текста из столбца see делаем лист массивов
//        partInfoAll.forEach(x->x.sees=ALTinfo.extractSee(x.figItemSee));
//
//        List<PartInfoAll>infosWithQuantity = partInfoAll.stream().filter(x-> x.finalQuantity!=null).toList();
//        for(PartInfoAll info:infosWithQuantity){
//            List<PartInfoAll> items = partInfoAll.stream().filter(x-> Arrays.stream(x.sees).anyMatch(y-> Objects.equals(y,info.figItem))).toList();
//            if(items.size()!=0){
//                items.forEach(x->x.finalQuantity=0);
//            }
//        }
//        partInfoAll.stream().filter(x-> x.countInfo.startsWith("(RF")).forEach(x->x.finalQuantity = ALTinfo.extractRF(x.countInfo));
//
//        ExcelImportPartInfo.export2(exportFileName1,exportFileName2,partInfoAll);
//
        int t = 0;

    }

}
