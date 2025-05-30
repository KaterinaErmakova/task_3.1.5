console.log("JS подключен");

async function loadUsers() {
    const response = await fetch('/api/admin/users');
    const users = await response.json();
    const tbody = document.querySelector("#usersTable tbody");
    tbody.innerHTML = '';
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.surname}</td>
            <td>${user.age}</td>
            <td>${user.phoneNumber}</td>
            <td>${user.roles.map(r => r.name).join(', ')}</td>
            <td>
                <button class="btn btn-sm btn-info edit-btn">Edit</button>
                <button class="btn btn-sm btn-danger delete-btn">Delete</button>
            </td>`;

        const editBtn = row.querySelector('.edit-btn');
        const deleteBtn = row.querySelector('.delete-btn');

        editBtn.addEventListener('click', () => openEditModal(user));
        deleteBtn.addEventListener('click', () => deleteUser(user.id));

        tbody.appendChild(row);
    });
}

function openEditModal(user) {
    document.getElementById('editId').value = user.id;
    document.getElementById('editName').value = user.name;
    document.getElementById('editSurname').value = user.surname;
    document.getElementById('editAge').value = user.age;
    document.getElementById('editPhone').value = user.phoneNumber;
    document.getElementById('editPassword').value = user.password || "";

    fetch('/api/admin/roles')
        .then(resp => resp.json())
        .then(allRoles => {
            const container = document.getElementById('editRolesContainer');
            container.innerHTML = '';
            allRoles.forEach(role => {
                const checked = user.roles.some(r => r.name === role.name) ? 'checked' : '';
                container.innerHTML += `
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="${role.name}" id="role_${role.name}" ${checked}>
                            <label class="form-check-label" for="role_${role.name}">${role.name}</label>
                        </div>`;
            });
            new bootstrap.Modal(document.getElementById('editUserModal')).show();
        });
}

document.getElementById('editUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('editId').value;
    const roles = Array.from(document.querySelectorAll('#editRolesContainer input:checked')).map(r => ({name: r.value}));

    const user = {
        id,
        name: document.getElementById('editName').value,
        surname: document.getElementById('editSurname').value,
        age: parseInt(document.getElementById('editAge').value),
        phoneNumber: document.getElementById('editPhone').value,
        password: document.getElementById('editPassword').value,
        roles
    };

    await fetch(`/api/admin/update_user/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(user)
    });

    bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
    loadUsers();
});

async function deleteUser(id) {
    if (!confirm('Delete this user?')) return;
    await fetch(`/api/admin/delete_user/${id}`, {method: 'DELETE'});
    loadUsers();
}

loadUsers();

// Открыть модалку и отрисовать роли
function openAddUserModal() {
    fetch('/api/admin/roles')
        .then(resp => resp.json())
        .then(roles => {
            const container = document.getElementById('addRolesContainer');
            container.innerHTML = '';
            roles.forEach(role => {
                container.innerHTML += `
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${role.name}" id="addRole_${role.name}">
                        <label class="form-check-label" for="addRole_${role.name}">${role.name}</label>
                    </div>`;
            });
            new bootstrap.Modal(document.getElementById('addUserModal')).show();
        });
}

// Отправка нового пользователя
document.getElementById('addUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const roles = Array.from(document.querySelectorAll('#addRolesContainer input:checked')).map(r => ({name: r.value}));

    const newUser = {
        name: document.getElementById('addName').value,
        surname: document.getElementById('addSurname').value,
        age: parseInt(document.getElementById('addAge').value),
        phoneNumber: document.getElementById('addPhone').value,
        password: document.getElementById('addPassword').value,
        roles
    };

    const response = await fetch('/api/admin/save_user', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(newUser)
    });

    if (response.ok) {
        bootstrap.Modal.getInstance(document.getElementById('addUserModal')).hide();
        document.getElementById('addUserForm').reset();
        loadUsers(); // Перерисовать таблицу
    } else {
        alert('Error creating user');
    }
});
