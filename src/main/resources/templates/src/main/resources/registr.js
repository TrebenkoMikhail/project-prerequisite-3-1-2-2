document.addEventListener('DOMContentLoaded', function () {
    fetchRoles();
    document.getElementById('register-form').addEventListener('submit', registerUser)
});

function fetchRoles() {
    fetch('/api/roles')
        .then(response => response.json())
        .then(roles => {
            const rolesSelect = document.getElementById('roles');
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.text = role.name;
                rolesSelect.appendChild(option);
            });
        });
}

function registerUser(event) {
    event.preventDefault();

    const user = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        firstname: document.getElementById('firstname').value,
        lastname: document.getElementById('lastname').value,
        age: document.getElementById('age').value,
        email: document.getElementById('email').value,
        roles: Array.from(document.getElementById('roles').selectedOptions).map(option => ({id:option.value }))
    };

    fetch ('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            window.location.href = '/login';
        })
        .catch(error => console.error('Error: ', error))
}