
import org.json.JSONArray;
import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NIVEAU1;
import http.RequeteServeur.NIVEAU2;

public class main {

	public static void main(String[] args) throws Exception {

		JSONArray param = new JSONArray(new String[]{"jk1"});
		//ImageIcon i = new ImageIcon(new URL("avatar.jpg"));
		
		ReponseServeur r = RequeteServeur.envoyerRequete(NIVEAU1.JeanKevin, NIVEAU2.existe, param);
		System.out.println(r);
		
	}

}
