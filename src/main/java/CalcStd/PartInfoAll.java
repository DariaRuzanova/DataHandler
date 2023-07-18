package CalcStd;

public class PartInfoAll {
    public String partNumber;
    public String quantity;
    public String figItem;
    public String figItemSee;
    public final int lineNumber;
    public String alternative;
    public Integer finalQuantity;
    public String [] sees;
    public String[] listALT;
    public int RF;
    public String countInfo;
    public boolean isStandart;

    public PartInfoAll(String partNumber,String quantity, String figItem, String figItemSee, String alternative,int lineNumber) {
        this.partNumber = partNumber;
        this.quantity = quantity;
        this.figItem = figItem;
        this.figItemSee = figItemSee;
        this.lineNumber = lineNumber;
        this.alternative = alternative;
    }
}
