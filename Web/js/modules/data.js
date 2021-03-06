$(document).ready(function() {

// --------------------------------------- DATA -------------------------------------- //

	/**
	 * Page initialization
	 * @return {null}
	 */
	$("#dataContent").live("hashDidLoad", function() {

		var $parent = $(this);
		var $form = $parent.find(".dataForm");

		// Populate the old fields
		var registrationData = JSON.parse(localStorage.getItem("registrationData")) || {};
		if (Object.keys(registrationData).length != 0) {
			var vector = Object.keys(registrationData);
			for (var i = 0; i<vector.length; i++) {
				if (registrationData[vector[i]] != "") {
					var $element = $form.find("#" + vector[i]);
					if ($element.attr("readonly") != "readonly") {
						$element.val(registrationData[vector[i]]);
					}
				}
			}
		}

		// Create the basic fields
		$form.find(".cpf").mask("999.999.999-99");
		$form.find(".telephone").np("telephoneVerification");

		var validator = $parent.find(".dataForm").validate({
	        rules: {
	            name: {
					required: true,
					minlength: 10
				},
				cpf: "cpf",
				rg: {
	                required: true,
	                minlength: 8
	            },
				telephone: {
	                required: true, 
	                minlength: 10
	            },
				email: {
	                required: true, 
	                email: true
	            },
	            university: {
	                required: true, 
	                minlength: 3
	            },
	            course: {
	                required: true, 
	                minlength: 6
	            },
				password: { 
	                required: true, 
	                minlength: 6  
	            }, 
	            passwordConfirm: { 
	                required: true, 
	                minlength: 6,
	                equalTo: "#password"
	            },
				agreement: {
					required: true
				},
				newsletter: {
					required: false
				},
	        }, 
	        messages: {
	            name: {
					required: "Insira um nome válido",
					minlength: jQuery.format("Insira pelo menos {0} caracteres")
				},
				cpf: {
					required: "Insira um CPF válido",
					minlength: jQuery.format("Insira pelo menos {0} caracteres")
				},
				rg: {
					required: "Insira um RG válido",
					minlength: jQuery.format("Insira pelo menos {0} caracteres")
				},
				telephone: {
					required: "Insira um CPF válido",
					minlength: jQuery.format("Insira pelo menos {0} caracteres")
				},
				email: { 
	                required: "Insira um email válido", 
	                minlength: jQuery.format("Insira pelo menos {0} caracteres")
	            },
	            university: { 
	                required: "Insira uma universidade válida", 
	                minlength: jQuery.format("Insira pelo menos {0} caracteres")
	            },
	            course: { 
	                required: "Insira um curso válido", 
	                minlength: jQuery.format("Insira pelo menos {0} caracteres")
	            },
				password: {
	                required: "Insira uma senha", 
	                minlength: jQuery.format("Insira pelo menos {0} caracteres")
	            }, 
	            passwordConfirm: {
	                required: "Insira uma senha", 
	                minlength: jQuery.format("Insira pelo menos {0} caracteres"),
	                equalTo: "Insira a mesma senha"
	            },
				agreement: {
					required: "Concorde com os termos para efetuar o registro",
				},
				newsletter: {}
	        }, 
	        // the errorPlacement has to take the table layout into account 
	        errorPlacement: function(error, element) { 
	            if ( element.is(":radio") ) 
	                error.appendTo( element.parent().next().next() ); 
	            else if ( element.is(":checkbox") ) 
	                error.appendTo ( element.next() ); 
	            else 
	                error.appendTo( element.parent() ); 
	        }, 
	        // set this class to error-labels to indicate valid fields 
	        success: function(label) { 
	            // set   as text for IE 
	            label.html("").addClass("checked"); 
	        }
	    });
	});

	/**
	 * Cancel the default behavior of the form
	 * @return {null} 
	 */
	$("#dataContent .dataForm").live("submit", function() {
		return false;
	});

	/**
	 * Trigger the form validator
	 * @return {null}
	 */
	$("#dataContent .navigator li, #dataContent .sequenceContent li").live("click", function() {

		var $parent = $(this).parents("#dataContent");

		if ($parent.find(".dataForm").valid()) {

			var temp = $parent.find(".dataForm").serializeArray();

			var registrationData = JSON.parse(localStorage.getItem("registrationData")) || {};

			for (var i = 0; i < temp.length; i++) {
				if (temp[i].name != "") {
					registrationData[temp[i].name] = temp[i].value;
				}
			}

			localStorage.setItem("registrationData", JSON.stringify(registrationData));

			// Release the anchor and load the new page
			$(this).closest("a").attr("data-lock", "no").trigger("click");
		}
	});

});