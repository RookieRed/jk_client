
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import http.ReponseServeur;
import http.RequeteServeur;
import http.RequeteServeur.NivImg;
import http.RequeteServeur.Niveau1;
import http.RequeteServeur.Niveau2;
import repo.JeanKevin;

public class main {

	public static void main(String[] args) throws Exception {
		
		HashSet<JeanKevin> s = JeanKevin.rechercher("pren");
		Iterator<JeanKevin> i = s.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
		
	}

}
