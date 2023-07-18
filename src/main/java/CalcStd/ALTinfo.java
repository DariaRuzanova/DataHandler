package CalcStd;

import Common.CommonUtils;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class ALTinfo {

    public static class Key{
        public String partNumber;
        public String figItem;

        public Key(String partNumber, String figItem) {
            this.partNumber = partNumber;
            this.figItem = figItem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return partNumber.equals(key.partNumber) && figItem.equals(key.figItem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partNumber, figItem);
        }
    }
    public static Integer[] findALT(String text) {
        String findStr = "ALT";
        int lastInd = 0;
        List<Integer> positionAlt = new ArrayList<>();
        if(text != null) {
            while (lastInd != -1) {
                lastInd = text.indexOf(findStr, lastInd);
                if (lastInd != -1) {
                    positionAlt.add(lastInd);
                    lastInd += 1;
                }
            }
            positionAlt.add(text.length());
        }
        return positionAlt.toArray(new Integer[0]);
    }
    public static String[] extraxtALT(String text) {
        Integer[] listPositionALT = findALT(text);
        final String ALTMarker = "ALT ";
        List<String> listALT = new ArrayList<>();
        if (text != null) {
            for (int ind = 0; ind < listPositionALT.length - 1; ind++) {
                String ALTBody = text.substring(listPositionALT[ind] + ALTMarker.length(), listPositionALT[ind + 1]);
                listALT.add(ALTBody.trim());
            }
        }
        return listALT.toArray(new String[0]);
    }

    public static String[] extractSee(String text){
        final String seeMarkerBegin = "[SEE ";
        final String seeMarkerEnd = "]";
        List<String>result = new ArrayList<>();
        int startIndex = 0;
        if(text!=null){
            int markerBegin = text.indexOf(seeMarkerBegin,startIndex);
            while (markerBegin!=-1){
                int markerEnd = text.indexOf(seeMarkerEnd,markerBegin);
                String seeBody = text.substring(markerBegin+seeMarkerBegin.length(),markerEnd);
                result.add(seeBody);
                startIndex=markerEnd;
                markerBegin = text.indexOf(seeMarkerBegin,startIndex);
            }
        }
        return result.toArray(new String[0]);
    }
    public static int extractRF(String text){
        final String RFMarkerBegin = "RF(";
        final String RFMarkerEnd = ")";
        int startIndex = 0;

        int markerBegin = text.indexOf(RFMarkerBegin,startIndex);
        String textNumber = null;
        while(markerBegin!=-1) {
            int markerEnd = text.indexOf(RFMarkerEnd, markerBegin);
            textNumber = text.substring(markerBegin + RFMarkerBegin.length(), markerEnd);
            startIndex=markerEnd;
            markerBegin=text.indexOf(RFMarkerBegin,startIndex);
        }
        if(textNumber!=null){
            return Integer.parseInt(textNumber);
        }
        else{
            return 0;
        }
    }

    //преобразует лист к словарю, в качестве ключа используется partNumber и figItem
    public static Map<Key, PartInfoAll> getPartsMap(List<PartInfoAll> partInfoAll) {
        Map<Key, PartInfoAll> infoMap = new HashMap<>();
        for (PartInfoAll i : partInfoAll) {
            infoMap.put(new Key(i.partNumber, i.figItem), i);
        }
        return infoMap;
    }
    // Обновляет коллекцию counts (key(partNumber,figItem),Value = count (qty))
    public static void fillPartsCountByQTY(Map<ALTinfo.Key, PartInfoAll> map, Map<ALTinfo.Key, String> counts) {
        for (Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) { //возвращает набор элементов коллекции
            if (CommonUtils.isNumeric(entry.getValue().quantity)) {
                String count = entry.getValue().quantity;
                ALTinfo.Key key = entry.getKey();
                if (!counts.containsKey(key) || Objects.equals(counts.get(key), count)) {
                    counts.put(key, count);
                } else {
                    throw new RuntimeException("Странная ситуация. Два разных QTY у одной записи partNumber & figItem");
                }
            }
        }
    }
    public static void fillPartsCountByNotStandard(Map<Key, PartInfoAll> map, Map<Key, String> counts, StandardChecker standardChecker) {
        for(Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) {
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            boolean isStandard = standardChecker.isStandard(info.partNumber);
            if (!isStandard) {
                counts.put(key, info.quantity);
            }
        }
    }
    public static void fillPartsCountByALTFromExistingCount(Map<ALTinfo.Key, PartInfoAll> map, Map<ALTinfo.Key, String> counts) {
        for (Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) { //возвращает набор элементов коллекции
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            if(Objects.equals(info.quantity, "ALT")){
                List<Pair<PartInfoAll, String>> subCounts = getAltCounts(info, map, counts);
                List<Pair<PartInfoAll, String>> subCountsExistingCount = subCounts.stream().filter(x -> x.getSecond() != null).toList();
                if (0 < subCountsExistingCount.size()) {
                    String count = subCountsExistingCount.get(0).getSecond();
                    counts.put(key, count);
                }
            }
        }
    }

    public static void fillPartsCountByALTFromRF(Map<ALTinfo.Key, PartInfoAll> map, Map<ALTinfo.Key, String> counts) {
        for (Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) { //возвращает набор элементов коллекции
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            if(!counts.containsKey(key) && Objects.equals(info.quantity, "ALT")){
                List<Pair<PartInfoAll, String>> subCounts = ALTinfo.getAltCounts(info, map, counts);
                if(subCounts.stream().anyMatch(x->x.getFirst()==null && x.getSecond() == null)){
                    System.out.println("Для строки "+info.lineNumber+" нет связанных ссылок с partnumber и figItem. Проверь эту строку!");
                }

                List<Pair<PartInfoAll, String>> subCountsWithRF = subCounts.stream().filter(x -> x.getFirst().quantity != null && x.getFirst().quantity.startsWith("RF(")).toList();
                if(0 < subCountsWithRF.size()) {
                    String count = subCountsWithRF.get(0).getFirst().quantity;
                    counts.put(key, count);
                }
            }
        }
    }

    public static void fillPartsCountByStartWith(Map<Key, PartInfoAll> map, Map<Key, String> counts, List<String> prefixes) {
        for (Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) {
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            if(!counts.containsKey(key) && prefixes.stream().anyMatch(x->info.quantity.startsWith(x))) {
                counts.put(key, info.quantity);
            }
        }
    }

    public static void fillPartsCountByEquals(Map<Key, PartInfoAll> map, Map<Key, String> counts, List<String> prefixes) {
        for (Map.Entry<ALTinfo.Key, PartInfoAll> entry : map.entrySet()) {
            ALTinfo.Key key = entry.getKey();
            PartInfoAll info = entry.getValue();
            if(!counts.containsKey(key) && prefixes.stream().anyMatch(x-> Objects.equals(info.quantity, x))) {
                counts.put(key, info.quantity);
            }
        }
    }


    public static List<Pair<PartInfoAll, String>> getAltCounts(PartInfoAll info, Map<ALTinfo.Key, PartInfoAll> map, Map<ALTinfo.Key, String> counts) {
        List<Pair<PartInfoAll, String>> result = new ArrayList<>();
        for(String alt : info.listALT) {
            ALTinfo.Key altKey = new ALTinfo.Key(alt, info.figItem);
            PartInfoAll altInfo = map.get(altKey);
            String count = counts.getOrDefault(altKey, null);
            result.add(new Pair<>(altInfo, count));
        }
        return result;
    }

}
