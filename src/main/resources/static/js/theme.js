// Anti-FOUC: Theme sofort beim Laden anwenden (auch als Inline-Script in <head> eingebunden)
(function () {
    const saved = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-bs-theme', saved);
})();

function toggleTheme() {
    const html = document.documentElement;
    const current = html.getAttribute('data-bs-theme') || 'light';
    const next = current === 'dark' ? 'light' : 'dark';
    html.setAttribute('data-bs-theme', next);
    localStorage.setItem('theme', next);
    updateToggleBtn();
}

function updateToggleBtn() {
    const btn = document.getElementById('themeToggleBtn');
    if (!btn) return;
    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    btn.innerHTML = isDark ? '&#9728;&#65039; Hell' : '&#127769; Dunkel';
    btn.title = isDark ? 'Zum hellen Design wechseln' : 'Zum dunklen Design wechseln';
}

document.addEventListener('DOMContentLoaded', updateToggleBtn);

