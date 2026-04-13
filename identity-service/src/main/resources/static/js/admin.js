document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            if (sidebar) sidebar.classList.toggle('active');
            if (sidebarOverlay) sidebarOverlay.classList.toggle('active');
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', () => {
            if (sidebar) sidebar.classList.remove('active');
            if (sidebarOverlay) sidebarOverlay.classList.remove('active');
        });
    }

    // Notification Modal Logic
    const notificationModal = document.getElementById('notificationModal');
    if (notificationModal) {
        const modal = new bootstrap.Modal(notificationModal);
        const successMsg = document.getElementById('successMessageField');
        const errorMsg = document.getElementById('errorMessageField');
        const iconContainer = document.getElementById('modalIconContainer');
        const successIcon = document.getElementById('successIcon');
        const errorIcon = document.getElementById('errorIcon');
        const modalTitle = document.getElementById('modalTitle');
        const modalMessage = document.getElementById('modalMessage');

        if (successMsg || errorMsg) {
            if (successMsg) {
                iconContainer.style.backgroundColor = '#d1e7dd';
                iconContainer.style.color = '#0f5132';
                successIcon.classList.remove('d-none');
                errorIcon.classList.add('d-none');
                modalTitle.innerText = 'Thành công';
                modalMessage.innerText = successMsg.innerText;
                modal.show();
            } else if (errorMsg) {
                iconContainer.style.backgroundColor = '#f8d7da';
                iconContainer.style.color = '#842029';
                errorIcon.classList.remove('d-none');
                successIcon.classList.add('d-none');
                modalTitle.innerText = 'Lỗi';
                modalMessage.innerText = errorMsg.innerText;
                modal.show();
            }
        }
    }
    
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-success, .alert-danger');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});
