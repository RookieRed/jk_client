

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
		
		JSONArray param = new JSONArray(new int[]{2,3});
		ImageIcon i = new ImageIcon("avatar.jpg");
		
		ReponseServeur r = RequeteServeur.envoyerRequete(NIVEAU1.AVATAR, "ajouter", param);
		System.out.println(r);
		
	}

}
