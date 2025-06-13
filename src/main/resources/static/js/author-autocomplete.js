const nameInput = document.getElementById('authorName');
const suggestions = document.getElementById('author-suggestions');
const authorIdField = document.getElementById('authorId');

nameInput.addEventListener('input', async () => {
    const query = nameInput.value.trim();
    authorIdField.value = '';
    if (query.length < 2) {
        suggestions.innerHTML = '';
        return;
    }
    try {
        const response = await fetch(`/authors/search-fragment?query=${encodeURIComponent(query)}`);
        if (response.ok) {
            const html = await response.text();
            suggestions.innerHTML = html;
            suggestions.querySelectorAll('li').forEach(li => {
                li.addEventListener('click', () => {
                    nameInput.value = li.textContent;
                    authorIdField.value = li.dataset.id;
                    suggestions.innerHTML = '';
                });
            });
        }
    } catch (e) {
        console.error('Ошибка при получении авторов', e);
    }
});

document.addEventListener('click', (e) => {
    if (!e.target.closest('#author-suggestions') && e.target !== nameInput) {
        suggestions.innerHTML = '';
    }
});

