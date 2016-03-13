
import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONArray;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;

public class main {

	public static void main(String[] args) throws Exception {

		JSONArray param = new JSONArray(new String[]{"jk1"});
		File img = new File("avatar.jpg");
		
		RequeteServeur.recevoirImage(NivImg.Avatar, "rcv.jpg", param);
		//ReponseServeur r = RequeteServeur.executerRequete(Niveau1.JeanKevin, Niveau2.selectionner, param);
//		System.out.println(r.getCorps());
		
	}

}
