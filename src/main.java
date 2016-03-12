
import java.io.File;

import org.json.JSONArray;
import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NIVEAU1;
import http.RequeteServeur.NIVEAU2;

public class main {

	public static void main(String[] args) throws Exception {

		JSONArray param = new JSONArray(new String[]{"jk1"});
		File img = new File("avatar.jpg");
		
		ReponseServeur r = RequeteServeur.transfererImage(img, param);
		System.out.println(r);
		
	}

}
