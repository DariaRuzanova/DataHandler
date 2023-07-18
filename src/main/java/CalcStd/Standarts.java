package CalcStd;

public class Standarts {
    String stdSnecma;
    String stdConcepteur;

    public Standarts(String stdSnecma, String stdConcepteur) {
        this.stdSnecma = stdSnecma;
        this.stdConcepteur = stdConcepteur;
    }

    @Override
    public String toString() {
        return "Standarts:" +
                "stdSnecma='" + stdSnecma + '\'' +
                ", stdConcepteur='" + stdConcepteur + '\''+'\n';
    }

    public String getStdSnecma() {
        return stdSnecma;
    }

    public String getStdConcepteur() {
        return stdConcepteur;
    }
}
