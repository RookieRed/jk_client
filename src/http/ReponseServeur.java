package http;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author RookieRed
 *
 */
public class ReponseServeur implements Serializable {
	
	private static final long serialVersionUID = -4070470035924528165L;
	private boolean exception;
	private JSONObject corps;
	
	protected ReponseServeur(ArrayList<String> reponse) throws JSONException{
		String json = "";
		for (String ligne : reponse){
			json = json+ligne+"\n";
		}
		this.corps = new JSONObject(json);
		this.exception = this.corps.getBoolean("exception");
		this.corps.remove("exception");
	}
	
	protected ReponseServeur(boolean exception) {
		this.exception = exception;
		this.corps = new JSONObject();
		if(exception){
			try {
				this.corps.put("error", "Le serveur a rencontré un problème");
			} catch (JSONException e) {}
		}
	}

	public boolean estOK() {
		return (this.exception)?false:true;
	}

	public JSONObject getCorps() {
		return this.corps;
	}
	
	protected void setException(boolean b) {
		this.exception = b;
	}

	@Override
	public String toString() {
		return ((this.exception)?"[!] Exception levée : ":"")+corps.toString();
	}
	
	
}
