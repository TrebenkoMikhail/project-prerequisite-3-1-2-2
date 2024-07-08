$(document).ready(function() {
    $('.delete-btn').on('click', function() {
        var id = $(this).data('id');
        var firstName = $(this).data('firstname');
        var lastName = $(this).data('lastname');
        var age = $(this).data('age');
        var email = $(this).data('email');
        var roles = $(this).data('roles');

        $('#deleteUserId').val(id);
        $('#deleteFirstName').val(firstName);
        $('#deleteLastName').val(lastName);
        $('#deleteAge').val(age);
        $('#deleteEmail').val(email);
        $('#deleteRoles').val(roles);

        $('#deleteUserModal').modal('show');
    });

    $('#confirmDelete').on('click', function() {
        $('#deleteUserForm').submit();
    });
});
