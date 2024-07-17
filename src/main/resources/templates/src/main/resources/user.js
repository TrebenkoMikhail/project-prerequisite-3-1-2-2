document.addEventListener('DOMContentLoaded', function () {
    fetchUser();
});

function fetchUser() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            document.getElementById('user-id').innerText = user.id;
            document.getElementById('user-firstname').innerText = user.firstname;
            document.getElementById('user-lastname').innerText = user.lastname;
            document.getElementById('user-age').innerText = user.age;
            document.getElementById('user-email').innerText = user.email;
            document.getElementById('user-roles').innerText = user.roles.map(role => role.name).join(', ');
        });

}