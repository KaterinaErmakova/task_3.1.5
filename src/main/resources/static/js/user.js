async function loadUser() {
    const response = await fetch('/api/user');
    const user = await response.json();

    // Info bar
    const roleNames = user.roles.map(r => r.name.replace("ROLE_", "")).join(", ");
    document.getElementById('userInfo').textContent = `${user.name} ${user.surname} with roles ${roleNames}`;

    // Table row
    document.getElementById('userTableBody').innerHTML = `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.surname}</td>
                <td>${user.age}</td>
                <td>${user.phoneNumber}</td>
                <td>${user.roles.map(r => r.name).join(', ')}</td>
            </tr>
        `;

    // Hide admin link if user is not admin
    const isAdmin = user.roles.some(r => r.name === 'ROLE_ADMIN');
    if (!isAdmin) {
        document.getElementById('adminLink').style.display = 'none';
    }
}

loadUser();