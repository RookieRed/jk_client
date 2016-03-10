
import org.json.JSONArray;
import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NIVEAU1;
import http.RequeteServeur.NIVEAU2;

public class main {

	public static void main(String[] args) throws Exception {

		JSONArray param = new JSONArray(new String[]{"jk1"});
		//ImageIcon i = new ImageIcon(new URL("avatar.jpg"));
		
		ReponseServeur r = RequeteServeur.transfererImage(null, param);
		System.out.println(r);
		
	}

}
