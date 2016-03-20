<?php


Database::instancier();

/*********************************************************************
**                                                                  **
**   88                                                             **
**   88                                                             **
**   88                                                             **
**   88  88,dPYba,,adPYba,   ,adPPYYba,   ,adPPYb,d8   ,adPPYba,    **
**   88  88P'   "88"    "8a  ""     `Y8  a8"    `Y88  a8P_____88    **
**   88  88      88      88  ,adPPPPP88  8b       88  8PP"""""""    **
**   88  88      88      88  88,    ,88  "8a,   ,d88  "8b,   ,aa    **
**   88  88      88      88  `"8bbdP"Y8   `"YbbdP"Y8   `"Ybbd8"'    **
**                                        aa,    ,88                **
**                                         "Y8bbdP"                 **
**                                                                  **
*********************************************************************/

class Image {


				/////**************\\\\\
				////                \\\\
				///     ATTRIBUS     \\\
				//....................\\

	/**
	* Numéro de port d'écoute pour le transgfert d'images
	*/
	private static $port = 9997;
	/**
	* Taille du buffer pour la communication client - serveur
	*/
	private static $tailleBfr = 2048;



				/////**************\\\\\
				////                \\\\
				///     METHODES     \\\
				//....................\\
	


    /**
    * Fonction privée permettant de se connecter sur un nouveau socket avec un client
    * @return le descripteur de fichier du socket de communication 
    */
    private static function connecterSocket(){

            // Autorise l'exécution infinie du script, en attente de connexion.
            set_time_limit(0);

            //Création du socket
            if(($sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) == false){
                die("Erreur création : ".socket_strerror(socket_last_error()));
            }

            //Post du socket
            $addrLoc = ($_SERVER['HTTP_HOST']=="localhost")?"127.0.0.1":$_SERVER['SERVER_ADDR'];
            if(!socket_bind($sock, $addrLoc, self::$port)){
                die("Erreur publication socket : ".socket_strerror(socket_last_error()));
            }

            //Ecoute du socket
            if(!socket_listen($sock)){
                die("Erreur listen : ".socket_strerror(socket_last_error()));
            }
            //Acceptation de la connexion
            if(($sockClient = socket_accept($sock))==false){
                die("Erreur accept : ".socket_strerror(socket_last_error()));
            }
            socket_close($sock);
            socket_set_nonblock($sockClient);

            //On retourne le socket crée
            return $sockClient;
    }



	/**
	* Permet d'ajouter un avatar au JeanKevin dont l'identifiant est spécifié
	* et de l'enregistré par défaut
	* @param $jean_kevin correspond à l'identifiant de JK
	* @param $nomImage le nom de l'image à enregistrer
	*/
	static function ajouterAvatar($jean_kevin, $nomImage) {

		$reponse = new stdClass();
        //Vérification des paramètres
        if($jean_kevin == null){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }
        
        //On vérifie que le JK existe
        if(JeanKevin::existe($jean_kevin)){

            $path = substr(AVATAR.$jean_kevin,0);
            //On vérifie que le dossier du jean_kevin existe
            if(!is_dir($path)){
                mkdir($path, 0755, false);
            } 


            //Création de la socket serveur
			$sockClient = self::connecterSocket();

            //Création du fichier image
            $i = 1;
            while(file_exists(AVATAR.$jean_kevin."/$nomImage")){
            	$nomParse = explode('.', $nomImage);
            	$nomImage = substr($nomParse[0], 0, strlen($nomParse[0]) - (($i!=1)?strlen($i-1):0) )."$i.$nomParse[1]";
            	$i++;
            }
            $img = fopen(AVATAR.$jean_kevin."/$nomImage", 'a+');

            //Reception de l'image
            while ( ($bfr = socket_read($sockClient, self::$tailleBfr, PHP_BINARY_READ)) != false ){
           		//Ecriture dans le fichier image
           		fwrite($img, $bfr);
            }
            fclose($img);
            socket_close($sockClient);
            $reponse->finCommuncation = true;

            //On ajoute une image à la base de données
            $statement = Database::$instance->prepare("INSERT INTO image (chemin, identifiant_jk)"
                ." VALUES( :path, :jean_kevin );");
            $reponse->ajoutBD = $statement->execute(array(":path" => AVATAR.$jean_kevin."/$nomImage",
                                        ":jean_kevin" => $jean_kevin));
            return $reponse;
        }

        else {
            $reponse->exception = true;
            $reponse->error = "Jean Kévin non trouvé";
        }
        return $reponse;

    }



    /**
    * Selectionne l'avatar actuel de JK et le retourne dans le socket de communication
    * @param identifiant de JK
    */
    static function selectionnerAvatar($jean_kevin){

        $reponse = new stdClass();
        //Vérification des paramètres
        if($jean_kevin == null || strlen($jean_kevin)==0){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }

        //On selectionne le Jean Kevin et le champs "photo"
        $statement = DataBase::$instance->prepare("SELECT photo FROM jean_kevin WHERE identifiant = :identifiant ;");
        $ret = $statement->execute(array(':identifiant' => $jean_kevin));
        $path  = $statement->fetch()['photo'];

        //Si la BDD nous retourne null
        if($path == null || strlen($path)==0 || !file_exists($path)){
            $reponse->erreur = "Pas de photo de profil enregistrée, ou mauvais identifiant";
            return $reponse;
        }
        $img = fopen($path, "r");

        //On lance la connection avec le client
        $sockClient = self::connecterSocket();
        $bfr = fread($img, filesize($path));
        socket_write($sockClient, $bfr);
        socket_close($sockClient);

        //Retour d ela réponse
        $reponse->finTransfert = true;
        return $reponse;

    }


    /**
    * 
    */
    static function selectionner($chemin){

        $reponse = new stdClass();
        //Vérification des paramètres
        if($chemin == null || strlen($chemin)==0){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }

        //On vérifie que l'image existe dans la base de données
        $statement = DataBase::$instance->prepare("SELECT * FROM image WHERE chemin = :chemin ;");
        $statement->execute(array(':chemin' => $chemin));
        $img = $statement->fetch();

        //Si l'image n'existe pas on retourne un message d'erreur
        if(!$img['chemin'] === $chemin && file_exists($chemin)){
            $reponse->exception = true;
            $reponse->erreur = "L'image n'existe pas";
            return $reponse;
        }

        //Ouverture du socket de communication et envoie de l'image
        $sockClient = self::connecterSocket();
        $fichier = fopen($chemin, 'r');
        $bfr = fread($fichier, filesize($chemin));
        socket_write($sockClient, $bfr);
        //fermeture socket / fichier
        socket_close($sockClient);
        fclose($fichier);

        //Retour de la réponse
        $reponse->finTransfert = true;
        return $reponse;
    }


    static function selectionnerNoms($jean_kevin) {

        $reponse = new stdClass();
        //Vérification des paramètres
        if($jean_kevin == null || strlen($jean_kevin)==0){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }

        //On vérifie que l'image existe dans la base de données
        $statement = DataBase::$instance->prepare("SELECT * FROM image WHERE identifiant_jk = :jean_kevin ;");
        $statement->execute(array(':jean_kevin' => $jean_kevin));
        $reponse->chemins = $statement->fetchAll();
        //On supprime les cases inutiles et on réécrit le chemin
        foreach ($reponse->chemins as &$chemin ) {
            unset($chemin[0]);
            unset($chemin[1]);
            unset($chemin['identifiant_jk']);
            $chemin['chemin'] = substr($chemin['chemin'], strlen(AVATAR."/$jean_kevin"));
        }
        
        return $reponse;
    }

    /**
    *
    */
    static function ajouterCarte(){
    	
    }


    /**
    * Supprime une image de la BD et du serveur
    * @param nom de l'image que l'on souhaite supprimer
    * @param (optionnel) identifiant du JK à spécifier si l'image n'est pas une carte 
    */
    static function supprimer($nomImage, $jean_kevin=null){

        $reponse = new stdClass();
        //Vérification des paramètres
        if($nomImage == null || strlen($nomImage)==0){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }

        //S'il y a un identifiant indiqué on recherche l'image parmis les avatars
        if($jean_kevin !== null){
            $nomImage = AVATAR."$jean_kevin/$nomImage";
        }
        //Sinon on cherche parmi les cartes
        else {
            $nomImage = CARTE.$nomImage;
        }

        //Suppression de l'image dans la BdD
        $statement = DataBase::$instance->prepare("DELETE FROM image WHERE chemin = :chemin ;");
        $reponse->suppressionOK = $statement->execute(array(':chemin' => $nomImage));

        //Suppression du serveur
        $reponse->suppressionOK = $reponse->suppressionOK && unlink($nomImage);
        return $reponse;
    }


    /**
    * Vérifie si une image existe et dans la BD et sur le serveur
    * @param nom de l'image dont on cherceh l'existence
    * @param (optionnel) identifiant du JK à spécifier s'il on ne recherche pas une carte 
    */
    static function existe($nomImage, $jean_kevin=null){

        $reponse = new stdClass();
        //Vérification des paramètres
        if($nomImage == null || strlen($nomImage)==0){
            $reponse->exception = true;
            $reponse->error = "Erreur de parramètres";
            return $reponse;
        }

        //S'il y a un identifiant indiqué on recherche l'image parmis les avatars
        if($jean_kevin !== null){
            $nomImage = AVATAR."$jean_kevin/$nomImage";
        }
        //Sinon on cherche parmi les cartes
        else {
            $nomImage = CARTE.$nomImage;
        }

        //On selectionne l'image
        $statement = DataBase::$instance->prepare("SELECT * FROM image WHERE chemin = :chemin ;");
        $ret = $statement->execute(array(':chemin' => $nomImage));
        $jk  = $statement->fetch();
        $reponse->existe = ($jk['chemin'] == $nomImage);

        //On vérifie que l'image existe aussi sur le serveur
        $reponse->existe = $reponse->existe && file_exists($nomImage);
        return $reponse;
    }

}

?>