
import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;
import repo.JeanKevin;

public class main {

	public static void main(String[] args) throws Exception {
		
		JeanKevin jk = JeanKevin.connexion("jk1", "yolo");
		System.out.println(jk);
		File img = new File("avatar.jpg");
		System.out.println(jk.ajouterPhotoProfil(img));
	}

}
