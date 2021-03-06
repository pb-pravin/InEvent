$(document).ready(function() {

// --------------------------------- REGISTER ------------------------------------ //

	/**
	 * Page initialization
	 * @return {null}
	 */
	$("#registerContent").live("hashDidLoad", function() {
		
		// Hold the current content
		var $content = $(this);

		// Get the saved information
		var data = JSON.parse(localStorage.getItem("registrationData")) || {};

		// We send the data to the server
		$.post('developer/api/?' + $.param({
			method: "person.register",
			format: "html"
		}), {
			name: data.name,
			password: data.password,
			email: data.email,
			cpf: data.cpf,
			rg: data.rg,
			telephone: data.telephone,
			university: data.university,
			course: data.course,
			usp: data.usp
		},
		function(data, textStatus, jqXHR) {

			if (jqXHR.status == 200) {
				// Show the sucess message
				$content.find(".registrationComplete").fadeIn(0).delay(5000).fadeOut(300);

				// Remove the registration data
				localStorage.removeItem("registrationData");
			}

		}, 'html').fail(function(jqXHR, textStatus, errorThrown) {

			// Case the company or member is already registered
			if (jqXHR.status == 409) {
				$content.find(".registrationConflict").fadeIn(0).delay(5000).fadeOut(300);
			} else {
				$content.find(".registrationFailed").fadeIn(0);
				$content.find(".box").fadeOut(0);
			}
		});

	});

});