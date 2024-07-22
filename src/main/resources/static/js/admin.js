document.addEventListener('DOMContentLoaded', function () {
    fetchUser();
    fetchRoles();

    document.getElementById('add-user-form').addEventListener('submit', addUser);
    document.getElementById('edit-user-form').addEventListener('submit', editUser);
});

function fetchUsers() {
    fetch('/api/admin/allUsers')
        .then(response => response.json())
        .then(users => {
            const usersTable = document.getElementById('users-table');
            usersTable.innerHTML = '';
            users.forEach(user => {
                const row =usersTable.insertRow();
                row.insertCell(0).textContent = user.id;
                row.insertCell(1).textContent = user.username;
                row.insertCell(2).textContent = user.email;
                row.insertCell(3).textContent = user.roles.map(role => role.name).join(', ');
                const editButton = document.createElement('button');
                editButton.textContent = 'Edit';
                editButton.onclick = () => showEditForm(user.id);
                row.insertCell(4).appendChild(editButton);
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Delete';
                deleteButton.onclick = () => deleteUser(user.id);
                row.insertCell(5).appendChild(deleteButton);
            });
        });
}

function fetchRoles() {
    fetch('/api/admin/roles')
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

function addUser(event) {
    event.preventDefault();

    const user = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        email: document.getElementById('email').value,
        roles: Array.from(document.getElementById('roles').selectedOptions).map(option => ({id: option.value}))
    };
    fetch('/api/admin/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application-json'
        },
        body: JSON.stringify(user)
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            fetchUsers();
        })
        .catch(error => console.error('Error:', error));
}

function showEditUserForm(id) {
    fetch(`/api/admin/edit/${id}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('edit-id').value = user.id;
            document.getElementById('edit-username').value = user.username;
            document.getElementById('edit-email').value = user.email;
            const rolesSelect = document.getElementById('edit-roles');
            Array.from(rolesSelect.options).forEach(option => {
                option.selected = user.roles.some(role => role.id === parseInt(option.value));
            });
            document.getElementById('edit-user-form').style.display = 'block';
        })
}

function editUser(event) {
    event.preventDefault();

    const id = document.getElementById('edit-id').value;
    const user = {
        username: document.getElementById('edit-username').value,
        email: document.getElementById('edit-email').value,
        roles: Array.from(document.getElementById('edit-roles').selectedOptions).map(option => ({ id: option.value}))
    };
    fetch(`/api/admin/edit/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            fetchUsers()
        })
        .catch(error => console.error('Error', error))
}

function deleteUser(id) {
    fetch(`/api/admin/delete/${id}`, {
        method: 'DELETE',
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            fetchUsers();
        })
        .catch(error => console.error('Error', error));
}