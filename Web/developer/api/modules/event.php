<?php
// -------------------------------------- EVENT --------------------------------------- //
	
	if ($method === "getEvents") {

		$result = resourceForQuery(
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
				`event`.`zipCode`
			FROM
				`event`
			ORDER BY
				`event`.`name` ASC
		");

		echo printInformation("event", $result, true, 'json');

	} else

	// if ($method === "requestEnrollment") {

	// 	$tokenID = getToken();

	// 	if (isset($_GET['personID']) && $_GET['personID'] != "null") {

	// 		if ($core->workAtEvent) {
	// 			$personID = getAttribute($_GET['personID']);

	// 			// If the personID is 0, we must create an anonymous member named "Pessoa"
	// 			if ($personID == 0) $personID = createMember("Pessoa", "", "", "", "", 1);
	// 		} else {
	// 			http_status_code(401);
	// 		}
	// 	} else {
	// 		$personID = $core->memberID;
	// 	}

	// 	if (isset($_GET["activityID"])) {

	// 		// Get some properties
	// 		$activityID = getAttribute($_GET['activityID']);

	// 		$result = resourceForQuery(
	// 			"SELECT
	// 				`eventMember`.`id`
	// 			FROM
	// 				`eventMember`
	// 			INNER JOIN
	// 				`activity` ON `activity`.`eventID` = `eventMember`.`eventID`
	// 			WHERE 1
	// 				AND `activity`.`id` = $activityID
	// 				AND `member`.`id` = $personID
	// 		");

	// 		if (mysql_num_rows($result) > 0) {
				
	// 			$insert = resourceForQuery(
	// 				"INSERT INTO
	// 					`activityMember`
	// 					(`activityID`, `memberID`, `approved`, `present`)
	// 				VALUES
	// 					($activityID, $personID, 1, 0)
	// 			");

	// 			if ($update) {
	// 				// Return its data
	// 				if ($format == "json") {
	// 					$data["activityID"] = $activityID;
	// 					echo json_encode($data);
	// 				} elseif ($format == "html") {

	// 				} else {
	// 					http_status_code(405);	
	// 				}
	// 			} else {
	// 				http_status_code(500);
	// 			}

	// 		} else {
	// 			http_status_code(406);
	// 		}

	// 	} else {
	// 		http_status_code(400);
	// 	}
		
	// } else

	if ($method === "grantPermission" || $method === "revokePermission") {

		$tokenID = getToken();

		if (isset($_GET["eventID"]) && isset($_GET["personID"])) {

			// Get some properties
			$eventID = getAttribute($_GET['eventID']);
			$personID = getAttribute($_GET['personID']);

			if ($core->workAtEvent) {

				// See which field we want to update
				$attribute = ($method === "grantPermission") ? ROLE_COORDINATOR : ROLE_ATTENDEE;

				// Update based on the attribute
				$update = resourceForQuery(
					"UPDATE
						`eventMember`
					SET
						`eventMember`.`roleID` = $attribute
					WHERE 1
						AND `eventMember`.`eventID` = $eventID
						AND `eventMember`.`memberID` = $personID
				");

				if (mysql_affected_rows() > 0) {
					// Return its data
					if ($format == "json") {
						$data["eventID"] = $eventID;
						echo json_encode($data);
					} elseif ($format == "html") {
						$data["eventID"] = $eventID;
						echo json_encode($data);
					} else {
						http_status_code(405);	
					}
				} else {
					http_status_code(500);
				}
			} else {
				http_status_code(401);
			}
		} else {
			http_status_code(400);
		}
		
	} else

	if ($method === "getPeople") {

		$tokenID = getToken();

		if (isset($_GET["eventID"]) && isset($_GET["selection"])) {

			// Get some properties
			$eventID = getAttribute($_GET['eventID']);
			$selection = getAttribute($_GET['selection']);

			switch ($selection) {
				case 'approved':
					$complement = "AND `eventMember`.`approved` = 1";
					break;

				case 'denied':
					$complement = "AND `eventMember`.`approved` = 0";
					break;

				case 'unseen':
					$complement = "AND `eventMember`.`id` = 0"; // Don't need to implement it yet
					break;

				case 'all':
					$complement = "";
					break;

				default:
					http_status_code(405);
					break;
			}

			// The query
			$result = getPeopleAtEventQuery($eventID, $complement);

			// Return its data
			if ($format == "json") {
				echo printInformation("eventMember", $result, true, 'json');
			} elseif ($format == "html") {
				printPeopleAtEvent($result);
			} else {
				http_status_code(405);	
			}

		} else {
			http_status_code(400);
		}
		
	} else

	if ($method === "getActivities") {

		if (isset($_GET["eventID"])) {

			// Get some properties
			$eventID = getAttribute($_GET['eventID']);

			$result = resourceForQuery(
				"SELECT
					`activity`.`id`,
					`activity`.`groupID`,
					`activity`.`name`,
					`activity`.`description`,
					`activity`.`location`,
					UNIX_TIMESTAMP(MAKEDATE(YEAR(`activity`.`dateBegin`), DAYOFYEAR(`activity`.`dateBegin`))) AS `day`,
					UNIX_TIMESTAMP(`activity`.`dateBegin`) AS `dateBegin`,
	                UNIX_TIMESTAMP(`activity`.`dateEnd`) AS `dateEnd`,
					`activity`.`capacity`,
					`activity`.`general`,
					`activity`.`highlight`
				FROM
					`activity`
				WHERE
					`activity`.`eventID` = $eventID
	            ORDER BY
	                `activity`.`dateBegin` ASC,
	                `activity`.`dateEnd` ASC
			");

			$data = printInformation("eventMember", $result, true, 'object');
			echo json_encode(groupActivitiesInDays($data));

		} else {
			http_status_code(400);
		}
		
	} else

	if ($method === "getSchedule") {

		$tokenID = getToken();

		if (isset($_GET["eventID"])) {

			// Get some properties
			$eventID = getAttribute($_GET['eventID']);

			$result = resourceForQuery(
	            "SELECT
	                `activity`.`id`,
	                `activity`.`name`,
	                `activity`.`description`,
	                `activity`.`location`,
	                `activity`.`highlight`,
	                UNIX_TIMESTAMP(MAKEDATE(YEAR(`activity`.`dateBegin`), DAYOFYEAR(`activity`.`dateBegin`))) AS `day`,
	                UNIX_TIMESTAMP(`activity`.`dateBegin`) AS `dateBegin`,
	                UNIX_TIMESTAMP(`activity`.`dateEnd`) AS `dateEnd`,
	                IF(`activityMember`.`memberID` = $core->memberID, `activityMember`.`approved`, 0) AS `approved`
	            FROM
	                `activity`
	            LEFT JOIN
	                `activityMember` ON `activity`.`id` = `activityMember`.`activityID`
	            WHERE 1
	                AND `activity`.`eventID` = $eventID
	                AND `activityMember`.`memberID` = $core->memberID
	            GROUP BY
	                `activity`.`id`
	            ORDER BY
	                `activity`.`dateBegin` ASC,
	                `activity`.`dateEnd` ASC
	        ");

			$data = printInformation("eventMember", $result, true, 'object');
			echo json_encode(groupActivitiesInDays($data));

		} else {
			http_status_code(400);
		}
		
	} else

// ------------------------------------------------------------------------------------------- //
			
	{ http_status_code(501); }
	
?>