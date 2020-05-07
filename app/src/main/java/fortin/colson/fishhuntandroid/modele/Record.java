package fortin.colson.fishhuntandroid.modele;

import java.io.Serializable;

/**
 * Cette classe est une structure pour garder les données des scores des joueurs
 * en mémoire.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class Record implements Serializable, Comparable<Record> {

    String nom;
    int score;

    /**
     * Construit un record avec un nom et un score.
     * @param nom   Le nom.
     * @param score Le score.
     */
    public Record(String nom, int score) {
        this.nom = nom;
        this.score = score;
    }

    /**
     * Accesseur du nom du record.
     * @return  Le nom du record.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Accesseur du score du record.
     * @return  Le score du record.
     */
    public int getScore() {
        return score;
    }

    /**
     * Mutateur du score du record.
     * @param score Le score du record.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Compare deux records entre eux. On compare les records entre eux.
     * @param record    L'autre record.
     * @return          La différence entre les deux records.
     */
    @Override
    public int compareTo(Record record) {
        int diff = record.score - score;
        return diff == 0 ? nom.compareTo(record.nom) : diff;
    }

    /**
     * Permet d'afficher la représentation d'un record.
     * @return  La chaine de caractères représentant le record.
     */
    @Override
    public String toString() {
        return nom + " - " + score;
    }
}
