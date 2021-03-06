<?php

	/**
	 * Create a new member inside the platform
	 * @param  string $name      name of the person
	 * @param  string $password  password of the person
	 * @param  string $cpf       cpf of the person
	 * @param  string $telephone telephone of the person
	 * @param  string $email     email of the person
	 * @param  string $anonymous anonymous or not?
	 * @return integer           memberID
	 */
	function createMember($name, $password, $cpf, $telephone, $email, $anonymous) {

		// Insert the name 
		$insert = resourceForQuery(
			"INSERT INTO
				`member`
				(`name`, `anonymous`)
			VALUES 
				('$name', $anonymous)
		");

		$memberID = mysql_insert_id();

		if ($anonymous == 0) {
			$insert = resourceForQuery(
				"INSERT INTO
					`memberDetail`
					(`id`, `password`, `cpf`, `telephone`, `email`)
				VALUES
					($memberID, '$password', '$cpf', '$telephone', '$email')
			");
		}

		return $memberID;
	}

	/**
	 * Get all the events inside as an array
	 * @param  int  	$memberID 	id of the member
	 * @return array           		companies
	 */
	function getMemberEvents($memberID) {

		$result = resourceForQuery(
		// echo (replaceConstantForQuery(
			"SELECT
				`event`.`id`,
				`event`.`name`,
				`event`.`description`,
				UNIX_TIMESTAMP(`event`.`dateBegin`) AS `dateBegin`,
				UNIX_TIMESTAMP(`event`.`dateEnd`) AS `dateEnd`,
				`event`.`latitude`,
				`event`.`longitude`,
				`event`.`address`,
				`event`.`city`,
				`event`.`state`,
				`event`.`zipCode`,
				`eventMember`.`roleID`,
				`memberRole`.`constant`,
				`memberRole`.`title`,
				`eventMember`.`approved`
			FROM
				`event`
			INNER JOIN
				`eventMember` ON `event`.`id` = `eventMember`.`eventID`
			INNER JOIN
				`memberRole` ON `eventMember`.`roleID` = `memberRole`.`id`
			WHERE 1
				AND `eventMember`.`memberID` = $memberID
				AND (`eventMember`.`roleID` = @(ROLE_STAFF) OR `eventMember`.`roleID` = @(ROLE_COORDINATOR))
		");

		return printInformation("eventMember", $result, true, 'object');
	}

?>