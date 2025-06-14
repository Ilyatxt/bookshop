document.addEventListener('DOMContentLoaded', function() {
    const genreSearchInput = document.getElementById('genreSearch');
    const genreSuggestions = document.getElementById('genreSuggestions');
    const selectedGenresContainer = document.getElementById('selectedGenres');
    const genreIdsInput = document.getElementById('genreIds');
    const addGenreBtn = document.getElementById('addGenreBtn');

    // Хранение выбранных жанров
    const selectedGenres = new Map();

    // Функция для обновления скрытого поля с ID жанров
    function updateGenreIdsInput() {
        genreIdsInput.value = Array.from(selectedGenres.keys()).join(',');
    }

    // Функция для добавления жанра в выбранные
    function addGenre(id, name) {
        if (selectedGenres.has(id)) return;

        selectedGenres.set(id, name);

        const genreTag = document.createElement('div');
        genreTag.className = 'badge bg-success p-2 d-flex align-items-center';
        genreTag.innerHTML = `
            <span>${name}</span>
            <button type="button" class="btn-close btn-close-white ms-2" aria-label="Удалить" data-genre-id="${id}"></button>
        `;

        selectedGenresContainer.appendChild(genreTag);
        updateGenreIdsInput();

        // Очистить поле поиска
        genreSearchInput.value = '';
        genreSuggestions.innerHTML = '';
    }

    // Обработчик для удаления жанра
    selectedGenresContainer.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-close')) {
            const genreId = e.target.getAttribute('data-genre-id');
            selectedGenres.delete(genreId);
            e.target.closest('.badge').remove();
            updateGenreIdsInput();
        }
    });

    // Обработчик поиска жанров
    genreSearchInput.addEventListener('input', function() {
        const query = this.value.trim();

        if (query.length < 2) {
            genreSuggestions.innerHTML = '';
            return;
        }

        fetch(`/bookshop/api/genres/search?q=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                genreSuggestions.innerHTML = '';

                data.forEach(genre => {
                    // Не показывать уже выбранные жанры
                    if (selectedGenres.has(genre.id.toString())) return;

                    const item = document.createElement('a');
                    item.className = 'list-group-item list-group-item-action';
                    item.textContent = genre.name;
                    item.setAttribute('data-genre-id', genre.id);
                    item.setAttribute('data-genre-name', genre.name);
                    item.href = '#';

                    item.addEventListener('click', function(e) {
                        e.preventDefault();
                        addGenre(this.getAttribute('data-genre-id'), this.getAttribute('data-genre-name'));
                        genreSuggestions.innerHTML = '';
                    });

                    genreSuggestions.appendChild(item);
                });
            })
            .catch(error => console.error('Error searching genres:', error));
    });

    // Добавление жанра по клику на кнопку
    addGenreBtn.addEventListener('click', function() {
        const selectedSuggestion = genreSuggestions.querySelector('a');
        if (selectedSuggestion) {
            addGenre(
                selectedSuggestion.getAttribute('data-genre-id'),
                selectedSuggestion.getAttribute('data-genre-name')
            );
        }
    });

    // Закрывать список при клике вне
    document.addEventListener('click', function(e) {
        if (!genreSearchInput.contains(e.target) && !genreSuggestions.contains(e.target)) {
            genreSuggestions.innerHTML = '';
        }
    });
});
