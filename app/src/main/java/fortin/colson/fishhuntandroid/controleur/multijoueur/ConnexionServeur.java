package fortin.colson.fishhuntandroid.controleur.multijoueur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cette classe permet de garder une seule connexion vers le serveur. Elle lance la connexion
 * lorsque l'instance est appelée la première fois et ferme le flux lorsque la méthode
 * ConnexionServeur.ferme() est appelée.
 * @author Fortin-Leblanc, Gabriel
 * @author Colson-Ratelle, Antoine
 */
public class ConnexionServeur {

    private static int port = 1337;
    private static String adresse = "192.168.100.191";

    private Socket client;
    private PrintWriter output;
    private BufferedReader input;

    private static ConnexionServeur instance;

    /**
     * Construit une instance de ConnexionServeur. Le socket est créé à ce moment.
     * @throws IOException  Si un erreur se produit lors de la connexion.
     */
    private ConnexionServeur() throws IOException{
        client = new Socket(adresse, port);
        output = new PrintWriter(client.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    /**
     * Mutateur de l'instance de la connexion.
     * @return              La connexion vers le serveurs.
     * @throws IOException  Si un erreur se produit lors de la connexion.
     */
    public static ConnexionServeur getInstance() throws IOException{
        if(instance == null)
            instance = new ConnexionServeur();

        return instance;
    }

    /**
     * Accesseur de l'outil d'écriture vers le serveur.
     * @return  L'outil d'écriture.
     */
    public PrintWriter getOutput() {
        return output;
    }

    /**
     * Accesseur de l'outil de lecture du serveur.
     * @return  L'outil de lecture.
     */
    public BufferedReader getInput() {
        return input;
    }

    /**
     * Accesseur de l'adresse de connexion.
     * @return  L'adresse de connexion.
     */
    public static String getAdresse() {
        return ConnexionServeur.adresse;
    }

    /**
     * Mutateur de l'adresse de connexion.
     * @param adresse   L'adresse de connexion.
     */
    public static void setAdresse(String adresse) {
        ConnexionServeur.adresse = adresse;
    }

    /**
     * Accesseur du port de connexion.
     * @return  Le port de connexion.
     */
    public static int getPort() {
        return ConnexionServeur.port;
    }

    /**
     * Mutateur du port de connexion.
     * @param port  Le port de connexion
     */
    public static void setPort(int port) {
        ConnexionServeur.port = port;
    }

    /**
     * Ferme le flux de données.
     */
    public synchronized void ferme() {
        try {
            input.close();
            output.close();
            client.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        instance = null;
    }
}
