package CalcStd;

import Common.CommonUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main2 {
    public static void main(String[] args) throws IOException {
        String exportFileName1 = "d:\\Daria\\S7\\CFM56\\Catalog\\Standarts_all_CFM56\\REST\\2_EIPC_CFM56-7B_FIG_ITEM_update_indC_rest_v2export1_ss_del.xlsx";
        String exportFileName2 = "d:\\Daria\\S7\\CFM56\\Catalog\\Standarts_all_CFM56\\REST\\3_EIPC_CFM56-7B_FIG_ITEM_update_indC_rest_v2_export2.xlsx";

        List<PartInfoAll> partInfoAll = ExcelImportPartInfo.parse(exportFileName1);

        partInfoAll.stream().filter(x -> CommonUtils.isNumeric(x.quantity)).forEach(x->x.finalQuantity=(int) Double.parseDouble(x.quantity));
        //для текста из столбца see делаем лист массивов
        partInfoAll.forEach(x->x.sees=ALTinfo.extractSee(x.figItemSee));

        List<PartInfoAll> infosWithQuantity = partInfoAll.stream().filter(x-> x.finalQuantity !=null).toList();
        for(PartInfoAll info:infosWithQuantity){
            List<PartInfoAll> items = partInfoAll.stream().filter(x-> Arrays.stream(x.sees).anyMatch(y-> Objects.equals(y,info.figItem))).toList();
            if(items.size()!=0){
                items.forEach(x->x.finalQuantity=0);
            }
        }
//        for(PartInfoAll infoAll:partInfoAll){
//            if(infoAll.finalQuantity==null){
//                if(infoAll.quantity==null){
//                    System.out.println(infoAll.lineNumber);
//                    break;
//                }
//                infoAll.finalQuantity=ALTinfo.extractRF(infoAll.quantity);
//            }
//        }


        partInfoAll.stream().filter(x->x.finalQuantity==null).forEach(x->x.finalQuantity = ALTinfo.extractRF(x.quantity));

        //partInfoAll.stream().filter(x-> x.quantity.startsWith("(RF")).forEach(x->x.finalQuantity = ALTinfo.extractRF(x.quantity));

        ExcelImportPartInfo.export2(exportFileName1,exportFileName2,partInfoAll);

        int t=0;
    }
}
