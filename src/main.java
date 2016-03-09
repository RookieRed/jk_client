

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import org.json.JSONArray;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NIVEAU1;
import mysql.BdD;

public class main {

	public static void main(String[] args) throws Exception {
		
		JSONArray param = new JSONArray();
		ImageIcon i = new ImageIcon(new URL("avatar.jpg"));
		
		ReponseServeur r = RequeteServeur.envoyerRequete(NIVEAU1.AVATAR, "ajouter", param);
		
	}

}
