<?php
// ----------------------------------------- PERSON ----------------------------------------- //

	if ($method === "signIn") {

		if (isset($_GET["password"])) {

			$email = "";

			// Get some properties
			if (isset($_GET["email"])) {
				$email = getAttribute($_GET['email']);
			} elseif (isset($_GET["member"])) {
				$member = getAttribute($_GET['member']);

				$result = resourceForQuery(
					"SELECT
						`member`.`email`
					FROM
						`member`
					WHERE 1
						AND BINARY `member`.`name` = '$member'
				");

				if (mysql_num_rows($result) > 0) {	
					$email = mysql_result($result, 0, "email");
				}

			}
			$password = getAttribute($_GET['password']);

			// Return the desired data
			$data = processLogIn($email, $password);
			echo json_encode($data);
		} else {
			http_status_code(400);
		}
				
	} else

	if ($method === "signInWithFacebook") {

		if (isset($_GET["facebookToken"])) {

			$facebookToken = getAttribute($_GET["facebookToken"]);

			$facebook->setAccessToken($facebookToken);
			$userID = $facebook->getUser();

			if ($userID) {
				// We have a user ID, so probably a logged in user.
				// If not, we'll get an exception, which we handle below.
				try {
					$userProfile = $facebook->api('/me?fields=email,name', 'GET');

					$name = $userProfile['name'];
					$email = $userProfile['email'];

					// We now see if the current member has a profile with us
					$result = resourceForQuery(
					// echo (
						"SELECT
							`member`.`id`,
							`member`.`name`,
							COALESCE(`memberSessions`.`sessionKey`, '') AS `sessionKey`
						FROM
							`member`
						INNER JOIN
							`memberDetail` ON `memberDetail`.`id` = `member`.`id`
						LEFT JOIN
							`memberSessions` ON `memberSessions`.`memberID` = `member`.`id`
						WHERE 0
							OR BINARY `member`.`name` = '$name'
							OR BINARY `memberDetail`.`email` = '$email'
						ORDER BY
							`memberSessions`.`id` DESC
					");

					// Member already has a profile with us
					if (mysql_num_rows($result) > 0) {

						$name = mysql_result($result, 0, "name");
						$memberID = mysql_result($result, 0, "id");
						$tokenID = mysql_result($result, 0, "sessionKey");

						$events = getMemberEvents($memberID);

						// Return some information
						$data["name"] = $name;
						$data["memberID"] = $memberID;
						$data["events"] = $events["data"];
						$data["tokenID"] = $tokenID;

						echo json_encode($data);

					} else {

						// Create a random password for the newly created member
						$password = md5((string)rand());
						// Create the member
						$memberID = createMember($name, $password, "", "", $email, 0);

						if ($memberID != 0) {
							// Return the desired data
							$data = processLogIn($email, $password);
							echo json_encode($data);
						} else {
							http_status_code(500);
						}
					}
				} catch(FacebookApiException $e) {
					// If the user is logged out, you can have a 
					// user ID even though the access token is invalid.
					// In this case, we'll get an exception, so we'll
					// just ask the user to login again here.
					http_status_code(503);
				}
			} else {
				// No user, return a non authenticated code
				http_status_code(401);
			}
		} else {
			http_status_code(400);
		}
				
	} else

	if ($method === "register") {

		if (isset($_POST["name"]) && isset($_POST["password"]) && isset($_POST["email"])) {

			// Make sure that the user is not creating multiple accounts
			// include_once("../../includes/registrationCheck.php");

			// Get the provided data
			// Required
			$name = getAttribute($_POST["name"]);
			$password = getAttribute($_POST["password"]);
			$email = getAttribute($_POST["email"]);

			// Optional
			$cpf = (isset($_POST["cpf"])) ? getEmptyAttribute($_POST["cpf"]) : "";
			$rg = (isset($_POST["rg"])) ? getEmptyAttribute($_POST["rg"]) : "";
			$city = (isset($_POST["city"])) ? getEmptyAttribute($_POST["city"]) : "";
			$university = (isset($_POST["university"])) ? getEmptyAttribute($_POST["university"]) : "";
			$course = (isset($_POST["course"])) ? getEmptyAttribute($_POST["course"]) : "";
			$telephone = (isset($_POST["telephone"])) ? getEmptyAttribute($_POST["telephone"]) : "";
			$usp = (isset($_POST["usp"])) ? getEmptyAttribute($_POST["usp"]) : "";

			$result = resourceForQuery(
				"SELECT
					`member`.`name`
				FROM
					`member`
				WHERE 0
					OR BINARY `member`.`email` = '$email'
			");

			if (mysql_num_rows($result) == 0) {

				// Insert member details
				$insert = resourceForQuery(
					"INSERT INTO
						`member`
						(`name`, `password`, `cpf`, `rg`, `usp`, `telephone`, `city`, `email`, `university`, `course`)
					VALUES 
						('$name', '" . Bcrypt::hash($password) . "', '$cpf', '$rg', '$usp', '$telephone', '$city', '$email', '$university', '$course')
				");

				$memberID = mysql_insert_id();


				///// TEMPORARY, REMOVE THIS SHIT //////
				$insert = resourceForQuery(
					"INSERT INTO
						`eventMember`
						(`eventID`, `memberID`, `roleID`, `approved`)
					VALUES
						(2, $memberID, 1, 1)
				");
				////////////////////////////////////////


				// Insert all the activities that are general
				$insert = resourceForQuery(
					"INSERT INTO
						`activityMember`
						(`activityID`, `memberID`, `approved`, `present`)
					SELECT
						`activity`.`id`,
						$memberID,
						1,
						0
					FROM
						`activity`
					WHERE
						`activity`.`general` = 1
				");

				if ($memberID != 0) {
					// Return the desired data
					$data = processLogIn($email, $password);
					echo json_encode($data);
				} else {
					http_status_code(500);
				}
			} else {
				http_status_code(409);
			}
		} else {
			http_status_code(400);
		}

	} else

	if ($method === "getEvents") {

		$tokenID = getToken();

		echo json_encode(getMemberEvents($core->memberID));

	} else

	{ http_status_code(501); }

// ------------------------------------------------------------------------------------------- //
?>