document.addEventListener('DOMContentLoaded', function() {
    const authorSearchInput = document.getElementById('authorName');
    const authorSuggestions = document.getElementById('author-suggestions');
    const selectedAuthorsContainer = document.getElementById('selectedAuthors');
    const authorIdsInput = document.getElementById('authorIds');
    const addAuthorBtn = document.getElementById('addAuthorBtn');

    // Хранение выбранных авторов
    const selectedAuthors = new Map();

    // Обновление скрытого поля со списком ID авторов
    function updateAuthorIds() {
        authorIdsInput.value = Array.from(selectedAuthors.keys()).join(',');
    }

    // Добавление автора в выбранные
    function addAuthor(id, name) {
        if (selectedAuthors.has(id)) return;
        selectedAuthors.set(id, name);

        const badge = document.createElement('div');
        badge.className = 'badge bg-primary p-2 d-flex align-items-center';
        badge.innerHTML = `
            <span>${name}</span>
            <button type="button" class="btn-close btn-close-white ms-2" aria-label="Удалить" data-author-id="${id}"></button>
        `;

        selectedAuthorsContainer.appendChild(badge);
        updateAuthorIds();

        authorSearchInput.value = '';
        authorSuggestions.innerHTML = '';
    }

    // Удаление автора из выбранных
    selectedAuthorsContainer.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-close')) {
            const id = e.target.getAttribute('data-author-id');
            selectedAuthors.delete(id);
            e.target.closest('.badge').remove();
            updateAuthorIds();
        }
    });

    // Поиск авторов
    authorSearchInput.addEventListener('input', function() {
        const query = this.value.trim();
        if (query.length < 2) {
            authorSuggestions.innerHTML = '';
            return;
        }

        fetch(`/bookshop/api/authors/search?q=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                authorSuggestions.innerHTML = '';

                data.forEach(author => {
                    if (selectedAuthors.has(author.id.toString())) return;
                    const item = document.createElement('a');
                    item.className = 'list-group-item list-group-item-action';
                    const fullName = `${author.firstName} ${author.lastName}`;
                    item.textContent = fullName;
                    item.dataset.authorId = author.id;
                    item.dataset.authorName = fullName;
                    item.href = '#';

                    item.addEventListener('click', function(e) {
                        e.preventDefault();
                        addAuthor(this.dataset.authorId, this.dataset.authorName);
                    });

                    authorSuggestions.appendChild(item);
                });
            })
            .catch(err => console.error('Error searching authors:', err));
    });

    // Добавление автора по кнопке
    addAuthorBtn.addEventListener('click', function() {
        const selectedSuggestion = authorSuggestions.querySelector('a');
        if (selectedSuggestion) {
            addAuthor(selectedSuggestion.dataset.authorId, selectedSuggestion.dataset.authorName);
        }
    });

    // Закрывать список при клике вне его
    document.addEventListener('click', function(e) {
        if (!authorSearchInput.contains(e.target) && !authorSuggestions.contains(e.target)) {
            authorSuggestions.innerHTML = '';
        }
    });
});
