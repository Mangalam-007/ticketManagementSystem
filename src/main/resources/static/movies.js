const grid = document.querySelector('#movies');
const search = document.querySelector('#search');
const status = document.querySelector('#status');
const retry = document.querySelector('#retry');
const count = document.querySelector('#count');
let movies = [];

function render() {
  const query = search.value.trim().toLocaleLowerCase();
  const filtered = movies.filter(movie => movie.title.toLocaleLowerCase().includes(query));
  grid.replaceChildren();
  count.textContent = filtered.length;
  status.textContent = !movies.length ? 'No movies in the collection yet. Check back soon.'
    : !filtered.length ? 'No movies match your search. Try a different title.' : '';
  for (const movie of filtered) {
    const card = document.querySelector('#movie-template').content.cloneNode(true);
    card.querySelectorAll('h3').forEach(heading => { heading.textContent = movie.title; });
    card.querySelector('.poster').setAttribute('aria-hidden', 'true');
    grid.append(card);
  }
}

async function loadMovies() {
  retry.hidden = true;
  status.textContent = 'Loading the collection…';
  grid.setAttribute('aria-busy', 'true');
  try {
    const response = await fetch('/api/movies', { headers: { Accept: 'application/json' } });
    if (!response.ok) throw new Error('Movie request failed');
    const data = await response.json();
    if (!Array.isArray(data) || data.some(movie => !movie || typeof movie.title !== 'string')) {
      throw new Error('Invalid movie data');
    }
    movies = data;
    search.disabled = false;
    render();
  } catch (error) {
    status.textContent = 'We couldn’t load the movies. Please try again.';
    retry.hidden = false;
  } finally {
    grid.setAttribute('aria-busy', 'false');
  }
}
search.addEventListener('input', render);
retry.addEventListener('click', loadMovies);
loadMovies();
