<?php

//Inciation de la connexion PDO;
Database::instancier();

/**************************************************************************************************************
**                                                                                                           **
**          88                                       88      a8P                           88                **
**          88                                       88    ,88'                            ""                **
**          88                                       88  ,88"                                                **
**          88   ,adPPYba,  ,adPPYYba,  8b,dPPYba,   88,d88'       ,adPPYba,  8b       d8  88  8b,dPPYba,    **
**          88  a8P_____88  ""     `Y8  88P'   `"8a  8888"88,     a8P_____88  `8b     d8'  88  88P'   `"8a   **
**          88  8PP"""""""  ,adPPPPP88  88       88  88P   Y8b    8PP"""""""   `8b   d8'   88  88       88   **
**  88,   ,d88  "8b,   ,aa  88,    ,88  88       88  88     "88,  "8b,   ,aa    `8b,d8'    88  88       88   **
**   "Y8888P"    `"Ybbd8"'  `"8bbdP"Y8  88       88  88       Y8b  `"Ybbd8"'      "8"      88  88       88   **
**                                                                                                           **
**************************************************************************************************************/


class JeanKevin {



	/**
	 * Fonction vérifiant l'existence d'un Jean Kevin dans la base de données
	 * @param identifiant du jean_kevin à rechercher
	 */
	static function existe($identifiant){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//On selectionne le Jean Kevin
		$statement = DataBase::$instance->prepare("SELECT * FROM jean_kevin WHERE identifiant = :identifiant ;");
		$ret = $statement->execute(array(':identifiant' => $identifiant));
		$jk  = $statement->fetch();
		$reponse->existe = ($jk['identifiant'] == $identifiant);
		return $reponse;
	}



	/**
	 * Fonction permettant la connection d'un Jean Kevin à l'application
	 * @param identifiant du jean_kevin à rechercher
	 * @param mdp le mot de passe crypté
	 */
	static function connecter($identifiant, $mdp){

		$reponse  = new stdClass();
		$reponse->connecte = false;
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0 || strlen($mdp)==0 ){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Selection du JK
		$statement = DataBase::$instance->prepare("SELECT * FROM jean_kevin WHERE identifiant = :identifiant "
			."AND mot_de_passe = :mdp ;");
		$ret = $statement->execute(array(":identifiant" => $identifiant,
										":mdp" => $mdp));
		$jk = $statement->fetch();

		//Si le compte n'est pas actif on le signal au client
		if($jk['actif'] == 0){
			$reponse->actif = false;
			return $reponse;
		}
		else {
			$reponse->actif = true;
		}

		$reponse->connecte = ($jk['identifiant'] == $identifiant);
		return $reponse;

	}



	/**
	* Selectionne les données du JK
	* @param identifiant de JK
	*/
	static function selectionner($identifiant){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Selection du JK
		$statement = DataBase::$instance->prepare("SELECT * FROM jean_kevin WHERE identifiant = :identifiant ;");
		$ret       = $statement->execute(array(':identifiant' => $identifiant));
		$reponse->jk = $statement->fetch();
		for($i=0; $i<count($reponse->jk) ;$i++){
			unset($reponse->jk[$i]);
		}
		return $reponse;

	}



	/**
	* Selectionne tous les amis effectifs de JK
	* @param identifiant du JK dont on veu tles amis
	*/
	static function selectionnerAmis($identifiant){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Selection des amis de JK
		$statement = DataBase::$instance->prepare("SELECT jk.nom, jk.prenom, jk.identifiant, jk.mail"
			." FROM jean_kevin jk, r_lier a WHERE (a.identifiant1= :identifiant"
			." OR a.identifiant2= :identifiant ) AND (a.identifiant1=jk.identifiant"
			." OR a.identifiant2=jk.identifiant) AND jk.identifiant<> :identifiant"
			." AND effectif=1;");
		$statement->execute(array(":identifiant" => $identifiant));
		$reponse->amis = $statement->fetchAll();
		//On supprime les doublons du tableau
		foreach($reponse->amis as &$ami){
			for($i=0; $i<count($ami) ;$i++){
				unset($ami[$i]);
			}
		}
		return $reponse;
	}



	/**
	* Defini une photo enregistrée dans la base de donnée comme photo de profile de JK
	* @param identifiant du JK
	* @param nom du fichier image 
	*/
	static function definirPhotoProfile($identifiant, $nomImage){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0 || $nomImage == null || strlen($nomImage)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//On vérifie que l'image existe
		if(! (Image::existe(AVATAR.$nomImage)->existe)){
			$reponse->exception = true;
			$reponse->erreur = "L'image n'existe pas dans la bdd";
			return $reponse;
		}

		//On modifie la base de données en lui indiquant le nouveau chemin de la photo de profil
		$statement        = DataBase::$instance->prepare("UPDATE jean_kevin SET photo = :nomImage WHERE identifiant = :identifiant ;");
		$reponse->modifPP = $statement->execute(array(	":identifiant" 	=> $identifiant,
														":nomImage"		=> AVATAR.$nomImage));
		return $reponse;

	}



	/**
	* Supprime un Jean Kévin de la base de données ainsi que toutes ses données enregistrer dans la BdD et le serveur
	* @param identifiant du JK à supprimer
	*/
	static function supprimer($identifiant){
		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//suppression de tous les avatars de JK dans la BdD
		$statement = DataBase::$instance->prepare("DELETE FROM images WHERE identifiant_jk = :identifiant ;");
		$reponse->avatarsSuppr = $statement->execute(array(':identifiant_jk' => $identifiant));
		unset($statement);
		if (!$reponse->avatarsSuppr){
			$reponse->exception = true;
			return $reponse;
		}

		//Suppression des avatars sur le serveur
		exec("rm -rf ".AVATAR.$identifiant);

		//suppression du JK dans la BdD
		$statement = DataBase::$instance->prepare("DELETE FROM jean_kevin WHERE identifiant = :identifiant ;");
		$reponse->suprOK = $statement->execute(array(':identifiant' => $identifiant));
		return $reponse;
	}



	/**
	 * Ajoute un le Jean Kevin dans la base de données s'il n'existe pas déjà
	 * @param nom du JK
	 * @param prenom du JK
	 * @param identifiant du JK
	 * @param mot de passe du JK
	 * @param adresse mail du JK
	 */
	static function preinscrire($nom, $prenom, $identifiant, $psw, $mail){

		$reponse  = new stdClass();
		//Vérification des pararmètres
		if(strlen($nom) == 0 || strlen($prenom) == 0 || strlen($identifiant) == 0 ||  strlen($psw) == 0 
			|| strlen($mail) == 0 || !filter_var($mail, FILTER_VALIDATE_EMAIL)){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("INSERT INTO jean_kevin(nom, prenom, identifiant, mot_de_passe, mail)"
					." VALUES ( :nom , :prenom , :identifiant , :psw , :mail );");
		$reponse->inscriptionOK = $statement->execute(array(	':nom' => $nom,
												':prenom' => $prenom ,
												':identifiant' => $identifiant ,
												':psw' => $psw,
												':mail' => $mail));

		//Génération d'un code de connexion et du mail
		$urlConf = "http://".$_SERVER['HTTP_HOST'].WEBROOT."$identifiant/".md5($identifiant.$nom.$prenom);
		$message = 	"Hello $prenom!\n\r".
					"Merci de t'être inscrit à Jean-Kévin, pour confirmer ton inscrption et commencer à utiliser ".
					"l'application clique sur le lien suivant.\n\r<a href='$urlConf'>$urlConf</a>".
					"\n\r\n\rA bientôt ;)";
		//Envoie du mail de confirmation
		$reponse->mailOK = mail($mail, "[JK] Confirmation d'inscription", $message);
		return $reponse;
	}



	/**
	* Permet de confirmer l'inscription de JK par le lien envoyé à son adresse mail
	* @param l'identifiant de JK
	* @param le code de confirmation d'inscription crée
	*/
	static function confirmerInscription($identifiant, $code){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0 || $code == ''
			|| ! self::existe($identifiant)){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}
		
		//On récupère les données du Jean Kévin
		$statement = DataBase::$instance->prepare("SELECT * FROM jean_kevin WHERE identifiant = :identifiant ;");
		$statement->execute(array(':identifiant' => $identifiant));
		$jk = $statement->fetch();

		//On teste la validité du code de confirmation
		$nouvCode = md5($jk['identifiant'].$jk['nom'].$jk['prenom']);
		if(strcmp($code, $nouvCode) == 0){
			$statement = DataBase::$instance->prepare("UPDATE jean_kevin SET actif=1 WHERE identifiant = :identifiant ;");
			$statement->execute(array(':identifiant' => $identifiant));
			include VIEW."confirmation.php";
		}
	}


	/**
	* Permet de modifier le mot de passe d'un JK
	* @param l'identifiant de JK
	* @param le nouveau mot de passe de JK
	*/
	static function modifierMotDePasse($identifiant, $psw){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if($identifiant == null || strlen($identifiant)==0 || strlen($psw)==0 ){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Modification du mdp
		$statement = Database::$instance->prepare("UPDATE jean_kevin SET mot_de_passe = :mdp WHERE identifiant = :identifiant");
		$reponse->modifMDP = $statement->execute(array(	":mdp" => $psw,
														":identifiant" => $identifiant));
		return $reponse;
	}

	/**
	 * Permet de modifier les inforamtions personnelles de Jean-Kévin
	 * @param idenetifiant du JK ciblé
	 * @param nom le nouveau nom de JK
	 * @param prenom le nouveau prénom de JK
	 */
	static function modifier($identifiant, $nom, $prenom){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($identifiant)==0 || strlen($nom)==0 || strlen($prenom)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Modification des infos de JK
		$statement = Database::$instance->prepare("UPDATE jean_kevin SET nom = :nom,"
			." prenom = :prenom WHERE identifiant = :identifiant");
		$reponse->modif = $statement->execute(array(	":nom" => $nom,
														":prenom" => $prenom,
														":identifiant" => $identifiant));
		return $reponse;
	}


	/**
	 * Permet de modifier l'adresse mail d'un JK dont le compte n'est pas encore actif et
	 * envoie un mail sur la nouvelle adresse pour la confirmer
	 * @param idenetifiant du JK ciblé
	 * @param mail la nouvelle adresse mail à enregistrer
	 */
	static function modifierMail($identifiant, $mail){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($identifiant)==0 || strlen($mail)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}
		//On sélectionne le JK s'il existe
		$rep = self::selectionner($identifiant);
		//Si Jean Kévin existe et que son compte n'est aps encore actif on modifie son mail
		if(isset($rep->jk) && isset($rep->actif) && !$rep->actif){

			//Modification du mail dans la BDD
			$statement = Database::$instance->prepare("UPDATE jean_kevin SET mail = :mail WHERE identifiant = :identifiant");
			$reponse->modifMail = $statement->execute(array(":mail" => $mail,
															":identifiant" => $identifiant));

			//Génération d'un code de connexion et du mail
			$urlConf = "http://".$_SERVER['HTTP_HOST'].WEBROOT."$identifiant/".md5($identifiant.$rep->jk['nom'].$rep->jk['prenom']);
			$message = 	"Hello $rep->jk['prenom']!\n\r".
						"Merci de t'être inscrit(e) sur Jean-Kévin, pour confirmer ton inscrption et commencer à utiliser ".
						"l'application clique sur le lien suivant:\n\r<a href='$urlConf'>$urlConf</a>".
						"\n\r\n\rA bientôt ;)";
			//Envoie du mail de confirmation
			$reponse->mailOK = mail($mail, "[JK] Confirmation d'inscription", $message);
		}
		else {
			$reponse->exception = true;
			$reponse->erreur = "Erreur : JK n'existe pas ou son compte est déjà actif";
		}

		return $reponse;
	}

	static function rechercher($mot_cle){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($mot_cle)<=2){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Sélection des JK correspondants
		$statement = Database::$instance->prepare("SELECT identifiant, nom, prenom, mail FROM jean_kevin".
				." WHERE nom LIKE %:mot_cle% OR nom LIKE :mot_cle% OR nom LIKE %:mot_cle "
				." OR prenom LIKE %:mot_cle% OR prenom LIKE :mot_cle% OR prenom LIKE %:mot_cle "
				." OR identifiant LIKE %:mot_cle% OR identifiant LIKE :mot_cle% OR identifiant LIKE %:mot_cle "
				." ORDER BY nom, prenom, identifiant ");
		$statement->execute(array(":mot_cle" => $mot_cle));
		$reponse->resultats = $statement->fetchAll();
		//On supprime les doublons du tableau
		foreach($reponse->resultats as &$jk){
			for($i=0; $i<count($jk) ;$i++){
				unset($jk[$i]);
			}
		}

		return $reponse;
	}
	
}

?>