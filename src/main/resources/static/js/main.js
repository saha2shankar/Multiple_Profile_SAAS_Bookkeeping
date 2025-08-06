
    document.addEventListener('DOMContentLoaded', function() {
        // Make table rows clickable (example functionality)
        const tableRows = document.querySelectorAll('tbody tr');
        tableRows.forEach(row => {
            row.addEventListener('click', function() {
                // Add your click handler here
                console.log('Row clicked:', this);
            });
        });
    });

	function togglePassword(fieldId) {
	    const field = document.getElementById(fieldId);
	    const icon = field.nextElementSibling.querySelector('i');
	    
	    if (field.type === 'password') {
	        field.type = 'text';
	        icon.classList.replace('fa-eye', 'fa-eye-slash');
	    } else {
	        field.type = 'password';
	        icon.classList.replace('fa-eye-slash', 'fa-eye');
	    }
	}

	// Password confirmation validation
	document.querySelector('form').addEventListener('submit', function(e) {
	    const password = document.getElementById('password');
	    const confirmPassword = document.getElementById('confirmPassword');
	    
	    if (password.value !== confirmPassword.value) {
	        e.preventDefault();
	        alert('Passwords do not match!');
	        confirmPassword.focus();
	    }
	});