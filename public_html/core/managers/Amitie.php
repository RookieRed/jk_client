<?php


/***************************************************************************
**                                                                        **
**          db                             88           88                **
**         d88b                            ""    ,d     ""                **
**        d8'`8b                                 88                       **
**       d8'  `8b      88,dPYba,,adPYba,   88  MM88MMM  88   ,adPPYba,    **
**      d8YaaaaY8b     88P'   "88"    "8a  88    88     88  a8P_____88    **
**     d8""""""""8b    88      88      88  88    88     88  8PP"""""""    **
**    d8'        `8b   88      88      88  88    88,    88  "8b,   ,aa    **
**   d8'          `8b  88      88      88  88    "Y888  88   `"Ybbd8"'    **
**                                                                        **
***************************************************************************/

//Inciation de la connexion PDO;
Database::instancier();

/**
* Contient les fonctions permettant de gérer les liens d'amitié entre les Jean-Kévins
*/
class Amitie {
	

	/**
	 * Envoie une demande en ami au jean_kevin2 passé en paramètre
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	static function ajouter($jk1, $jk2) {

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($jk1)==0 || strlen($jk2)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("INSERT INTO r_lier(identifiant1, identifiant2, effectif)"
					." VALUES( :jk1 , :jk2 , 0 );");
		$reponse->ajoutOK = $statement->execute(array(	":jk1"	=> $jk1,
														":jk2"	=> $jk2));
		return $reponse;
	}



	/**
	 * Supprime l'amitié entre deux 2 Jean-Kévins 
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	static function supprimer($jk1, $jk2){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($jk1)==0 || strlen($jk2)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}


		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("DELETE FROM r_lier WHERE (identifiant1="
					." :jk1 AND identifiant2= :jk2 ) OR (identifiant2="
					." :jk1 AND identifiant1= :jk2 );");
		$reponse->suppressionOK = $statement->execute(array(	":jk1"	=> $jk1,
														":jk2"	=> $jk2));
		return $reponse;

	}



	/**
	 * Permet d'accepter une demande en amitié entre 2 Jean Kevins
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 */
	static function accepter($jk1, $jk2){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($jk1)==0 || strlen($jk2)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("UPDATE r_lier SET `effectif`=1 WHERE (identifiant1="
					." :jk1 AND identifiant2= :jk2 ) OR (identifiant2="
					." :jk1 AND identifiant1= :jk2 );");
		$reponse->acceptee = $statement->execute(array(	":jk1"	=> $jk1,
														":jk2"	=> $jk2));
		return $reponse;

	}


	/**
	 * Vérifie si il existe une relation entre les 2 JK et si elle est effective
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return vrai si la demande a été acceptée, faux sinon
	 */
	static function estEffictive($jk1, $jk2){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($jk1)==0 || strlen($jk2)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("SELECT * FROM r_lier WHERE (identifiant1="
					." :jk1 AND identifiant2= :jk1 ) OR (identifiant2="
					." :jk2 AND identifiant1= :jk2 )"
					." AND effectif=1;");
		$reponse->estEffective = $statement->execute(array(	":jk1"	=> $jk1,
														":jk2"	=> $jk2));
		return $reponse;
	}


	/**
	 * Vérifie si il existe une relation entre les 2 JK
	 * @param jk1 le login du premier JK
	 * @param jk2 le login du 2e jk
	 * @return vrai si une demande a été émise, faux sinon
	 */
	static function existe($jk1, $jk2){

		$reponse  = new stdClass();
		//Vérification du paramètre en entrée
		if(strlen($jk1)==0 || strlen($jk2)==0){
			$reponse->exception = true;
			$reponse->erreur    = "Erreur de paramètres";
			return $reponse;
		}

		//Exécution de la requête MySQL
		$statement = Database::$instance->prepare("SELECT * FROM r_lier WHERE (identifiant1="
					." :jk1 AND identifiant2= :jk1 ) OR (identifiant2="
					." :jk2 AND identifiant1= :jk2 )");
		$reponse->existe = $statement->execute(array(	":jk1"	=> $jk1,
														":jk2"	=> $jk2));
		return $reponse;
	}


}