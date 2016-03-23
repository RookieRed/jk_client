
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

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
		
//		JeanKevin jk = JeanKevin.connexion("jk1", "yolo");
//		System.out.println(jk);
//		File img = new File("avatar.jpg");
//		if(jk.ajouterPhotoProfil(img)){
//			jk.ajouterPhotoProfil(img);
//			System.out.println(jk.selectionnerTousAvatars());
//		}

//		RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.existe, 
//				new JSONArray(new String[]{"j-k2"}));
		RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.modifierMail, 
				new JSONArray(new String[]{"j-k2", "pass", "newgamer1396@gmail.com"}));
	}

}
