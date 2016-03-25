
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

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

	public static void main(String[] args) {
		
		try {
			
			System.out.println(RequeteServeur.executerRequete(Niveau1.Lieu, Niveau2.selectionner, 
					new JSONArray(new String[]{"3"})));
			
		} 
		catch (JSONException e) {e.printStackTrace();
		}
		
	}

}
