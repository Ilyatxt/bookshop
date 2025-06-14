const nameInput = document.getElementById('authorName');
const suggestions = document.getElementById('author-suggestions');
const authorIdField = document.getElementById('authorIds');

nameInput.addEventListener('input', async () => {
    const query = nameInput.value.trim();
    authorIdField.value = '';
    if (query.length < 2) {
        suggestions.innerHTML = '';
        return;
    }
    try {
        const response = await fetch(`/bookshop/api/authors/search?q=${encodeURIComponent(query)}`);
        if (response.ok) {
            const authors = await response.json();
            suggestions.innerHTML = '';
// Элементы интерфейса
            const authorNameInput = document.getElementById('authorName');
            const authorSuggestions = document.getElementById('author-suggestions');
            const addAuthorBtn = document.getElementById('addAuthorBtn');
            const selectedAuthors = document.getElementById('selectedAuthors');
            const authorIdsInput = document.getElementById('authorIds');

// Хранилище выбранных авторов
            let selectedAuthorsList = [];

// Инициализация при загрузке страницы
            function initSelectedAuthors() {
                const authorIdsValue = authorIdsInput.value;
                if (authorIdsValue) {
                    // Если есть предварительно выбранные авторы, получаем их данные
                    fetch(`/api/authors/batch?ids=${authorIdsValue}`)
                        .then(response => response.json())
                        .then(authors => {
                            authors.forEach(author => {
                                addAuthorToSelection(author);
                            });
                        })
                        .catch(error => console.error('Ошибка при получении авторов:', error));
                }
            }

// Поиск авторов при вводе текста
            authorNameInput.addEventListener('input', async () => {
                const query = authorNameInput.value.trim();

                if (query.length < 2) {
                    authorSuggestions.innerHTML = '';
                    return;
                }

                try {
                    const response = await fetch(`/api/authors/search?q=${encodeURIComponent(query)}`);
                    if (response.ok) {
                        const authors = await response.json();
                        authorSuggestions.innerHTML = '';

                        if (authors.length > 0) {
                            const ul = document.createElement('ul');
                            ul.classList.add('list-group');

                            authors.forEach(author => {
                                // Проверяем, не выбран ли уже этот автор
                                const isSelected = selectedAuthorsList.some(selected => selected.id === author.id);
                                if (!isSelected) {
                                    const li = document.createElement('li');
                                    li.classList.add('list-group-item', 'list-group-item-action');
                                    li.textContent = `${author.firstName} ${author.lastName}`;
                                    li.dataset.id = author.id;
                                    li.dataset.firstName = author.firstName;
                                    li.dataset.lastName = author.lastName;

                                    li.addEventListener('click', () => {
                                        addAuthorToSelection({
                                            id: author.id,
                                            firstName: author.firstName,
                                            lastName: author.lastName
                                        });
                                        authorNameInput.value = '';
                                        authorSuggestions.innerHTML = '';
                                    });

                                    ul.appendChild(li);
                                }
                            });

                            if (ul.children.length > 0) {
                                authorSuggestions.appendChild(ul);
                            } else {
                                showNoResults();
                            }
                        } else {
                            showNoResults();
                        }
                    }
                } catch (error) {
                    console.error('Ошибка при получении авторов:', error);
                }
            });

// Показать сообщение об отсутствии результатов
            function showNoResults() {
                authorSuggestions.innerHTML = '';
                const noResults = document.createElement('div');
                noResults.classList.add('list-group-item', 'text-muted');
                noResults.textContent = 'Авторы не найдены';
                authorSuggestions.appendChild(noResults);
            }

// Добавление автора по кнопке
            addAuthorBtn.addEventListener('click', () => {
                const query = authorNameInput.value.trim();
                if (query.length > 0) {
                    // Пытаемся найти автора с точным совпадением имени
                    fetch(`/api/authors/search?q=${encodeURIComponent(query)}`)
                        .then(response => response.json())
                        .then(authors => {
                            if (authors.length > 0) {
                                // Берем первое совпадение
                                const author = authors[0];
                                if (!selectedAuthorsList.some(selected => selected.id === author.id)) {
                                    addAuthorToSelection(author);
                                }
                            } else {
                                // Если автор не найден, предлагаем создать нового
                                if (confirm(`Автор "${query}" не найден. Хотите создать нового автора?`)) {
                                    // Здесь можно добавить логику создания нового автора через API
                                    console.log('Создание нового автора:', query);
                                }
                            }
                            authorNameInput.value = '';
                            authorSuggestions.innerHTML = '';
                        })
                        .catch(error => console.error('Ошибка при поиске автора:', error));
                }
            });

// Добавление автора в список выбранных
            function addAuthorToSelection(author) {
                // Проверяем, не выбран ли уже этот автор
                if (selectedAuthorsList.some(selected => selected.id === author.id)) {
                    return;
                }

                // Добавляем автора в массив
                selectedAuthorsList.push(author);

                // Создаем элемент для отображения
                const badge = document.createElement('div');
                badge.classList.add('badge', 'bg-primary', 'd-flex', 'align-items-center', 'p-2', 'fs-6');
                badge.dataset.id = author.id;

                const authorName = document.createElement('span');
                authorName.textContent = `${author.firstName} ${author.lastName}`;
                badge.appendChild(authorName);

                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.classList.add('btn-close', 'btn-close-white', 'ms-2', 'small');
                removeBtn.setAttribute('aria-label', 'Удалить');
                removeBtn.addEventListener('click', () => removeAuthorFromSelection(author.id));
                badge.appendChild(removeBtn);

                selectedAuthors.appendChild(badge);

                // Обновляем скрытое поле с ID авторов
                updateAuthorIds();
            }

// Удаление автора из списка выбранных
            function removeAuthorFromSelection(authorId) {
                // Удаляем из массива
                selectedAuthorsList = selectedAuthorsList.filter(author => author.id !== authorId);

                // Удаляем элемент из DOM
                const badge = selectedAuthors.querySelector(`[data-id="${authorId}"]`);
                if (badge) {
                    badge.remove();
                }

                // Обновляем скрытое поле с ID авторов
                updateAuthorIds();
            }

// Обновление скрытого поля с ID авторов
            function updateAuthorIds() {
                authorIdsInput.value = selectedAuthorsList.map(author => author.id).join(',');
            }

// Закрытие выпадающего списка при клике вне его
            document.addEventListener('click', (e) => {
                if (!e.target.closest('#author-suggestions') && e.target !== authorNameInput) {
                    authorSuggestions.innerHTML = '';
                }
            });

// Запуск инициализации при загрузке страницы
            document.addEventListener('DOMContentLoaded', initSelectedAuthors);
            if (authors.length > 0) {
                const ul = document.createElement('ul');
                ul.classList.add('author-list');

                authors.forEach(author => {
                    const li = document.createElement('li');
                    li.textContent = author.fullName;
                    li.dataset.id = author.id;
                    li.addEventListener('click', () => {
                        nameInput.value = author.fullName;
                        authorIdField.value = author.id;
                        suggestions.innerHTML = '';
                    });
                    ul.appendChild(li);
                });

                suggestions.appendChild(ul);
            }
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

