<?php


/********************************************************************************************************
**                                                                                                     **
**    88888888ba,                                     88                                               **
**    88      `"8b                 ,d                 88                                               **
**    88        `8b                88                 88                                               **
**    88         88  ,adPPYYba,  MM88MMM  ,adPPYYba,  88,dPPYba,   ,adPPYYba,  ,adPPYba,   ,adPPYba,   **
**    88         88  ""     `Y8    88     ""     `Y8  88P'    "8a  ""     `Y8  I8[    ""  a8P_____88   **
**    88         8P  ,adPPPPP88    88     ,adPPPPP88  88       d8  ,adPPPPP88   `"Y8ba,   8PP"""""""   **
**    88      .a8P   88,    ,88    88,    88,    ,88  88b,   ,a8"  88,    ,88  aa    ]8I  "8b,   ,aa   **
**    88888888Y"'    `"8bbdP"Y8    "Y888  `"8bbdP"Y8  8Y"Ybbd8"'   `"8bbdP"Y8  `"YbbdP"'   `"Ybbd8"'   **
**                                                                                                     **
**                                                                                                     **
*********************************************************************************************************/

/**
* Database ne contient qu'une instance static de l'objet PDO
* C'est le fichier de configuration de connexion à la BdD
* Pour l'utiliser il faut instancier la connexion et utiliser l'objet PDO
*/
class Database {
	
	//Attribus de la classe

	private static $dsn;
	private static $user;
	private static $psw;
	public  static $instance = null;


	//Méthodes de classe

	public static function instancier(){

		if(self::$instance==null){

			//Instanciation des données de connection
			if($_SERVER['HTTP_HOST'] == "localhost"){
				self::$dsn  = "mysql:dbname=jean_kevin;host=localhost;charset=UTF8";
				self::$user = "root";
				self::$psw  = "";
			}
			else {
				self::$dsn  = "mysql:dbname=db618325086;host=db618325086.db.1and1.com;charset=UTF8";
				self::$user = "dbo618325086";
				self::$psw  = "q2lG3Nk6a4";
			}

			//On lance la connexion (objet PDO)
			try {
				self::$instance = new PDO(self::$dsn, self::$user, self::$psw);
			}
			catch (Execption $e){
				echo "Erreur connexion BdD:\n".$e->getMessage();
			}
		}
		//Si la connexion échou on arrête le processus
		if(self::$instance==null)
			die("\nConnexion à la base impossible");
	}

}

?>