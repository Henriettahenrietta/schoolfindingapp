const API = 'http://localhost:8080/api/v1';

// ---------- session (dev auth, mirrors the backend X-Debug-* convention) ----------
const PRESETS = {
  guest: null,
  student: { uid: 'student-1', name: 'Ada N.', role: 'STUDENT' },
  admin: { uid: 'admin-dev', name: 'Platform Admin', role: 'ADMIN' },
};
function session() {
  try { return JSON.parse(localStorage.getItem('sf_session')); } catch { return null; }
}
function setSession(s) {
  if (s) localStorage.setItem('sf_session', JSON.stringify(s));
  else localStorage.removeItem('sf_session');
  renderIdentity();
  if (currentView === 'favorites') loadFavorites();
}

// ---------- API helper ----------
async function api(path, opts = {}) {
  const s = session();
  const headers = Object.assign({ 'Content-Type': 'application/json' }, opts.headers || {});
  if (s) {
    headers['X-Debug-Uid'] = s.uid;
    headers['X-Debug-Name'] = s.name;
    headers['X-Debug-Role'] = s.role;
  }
  const res = await fetch(API + path, Object.assign({}, opts, { headers }));
  if (res.status === 204 || res.status === 201) return null;
  const body = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(body.message || `HTTP ${res.status}`);
  return body;
}

// ---------- formatting ----------
const fmtMoney = (n, c) => (n == null ? 'N/A' : new Intl.NumberFormat('en-US').format(n) + ' ' + (c === 'XAF' ? 'FCFA' : c || ''));
const stars = (avg) => '★'.repeat(Math.round(avg)) + '☆'.repeat(5 - Math.round(avg));
const catLabel = (c) => ({ UNIVERSITY: 'University', HIGH_SCHOOL: 'High School', SECONDARY: 'Secondary', VOCATIONAL: 'Vocational', PRIMARY: 'Primary' }[c] || c);
const esc = (s) => (s == null ? '' : String(s).replace(/[&<>"]/g, (m) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[m])));

// ---------- toast ----------
let toastT;
function toast(msg) {
  const el = document.getElementById('toast');
  el.textContent = msg; el.classList.remove('hidden');
  clearTimeout(toastT); toastT = setTimeout(() => el.classList.add('hidden'), 2600);
}

// ---------- compare selection ----------
let compareIds = JSON.parse(localStorage.getItem('sf_compare') || '[]');
function saveCompare() {
  localStorage.setItem('sf_compare', JSON.stringify(compareIds));
  document.getElementById('compareCount').textContent = compareIds.length;
}
function toggleCompare(id) {
  id = Number(id);
  const i = compareIds.indexOf(id);
  if (i >= 0) compareIds.splice(i, 1);
  else { if (compareIds.length >= 4) { toast('Compare up to 4 schools'); return; } compareIds.push(id); }
  saveCompare();
  if (currentView === 'compare') loadCompare();
}

// ---------- identity bar ----------
function renderIdentity() {
  const s = session();
  const el = document.getElementById('identity');
  if (s) {
    el.innerHTML = `<span class="who">👤 ${esc(s.name)} · ${esc(s.role)}</span><button id="signout">Sign out</button>`;
    document.getElementById('signout').onclick = () => setSession(null);
  } else {
    el.innerHTML = `
      <span>Browsing as guest ·</span>
      <button data-as="student">Sign in as student</button>
      <button data-as="admin">Sign in as admin</button>`;
    el.querySelectorAll('button').forEach((b) => (b.onclick = () => setSession(PRESETS[b.dataset.as])));
  }
}

// ---------- school card ----------
function cardHtml(s) {
  const checked = compareIds.includes(s.id) ? 'checked' : '';
  return `
  <div class="card">
    <span class="badge">${catLabel(s.category)}</span>
    <h3>${esc(s.name)}</h3>
    <div class="muted">📍 ${esc(s.city || '—')}${s.region ? ', ' + esc(s.region) : ''}</div>
    <div class="row">
      <span class="stars" title="${s.averageRating} / 5">${stars(s.averageRating)} <span class="muted">(${s.ratingCount})</span></span>
      <span class="price">${fmtMoney(s.tuitionFee, s.currency)}</span>
    </div>
    <div class="card-actions">
      <button class="btn primary" onclick="openDetail(${s.id})">Details</button>
      <button class="btn heart" title="Favourite" onclick="toggleFav(${s.id}, this)">🤍</button>
      <label class="cmp"><input type="checkbox" ${checked} onchange="toggleCompare(${s.id})"> Compare</label>
    </div>
  </div>`;
}

// ---------- discover ----------
async function loadSchools() {
  const params = new URLSearchParams();
  const q = document.getElementById('q').value.trim();
  const sort = document.getElementById('sort').value;
  if (q) params.set('q', q);
  if (sort) params.set('sort', sort);
  params.set('category', 'UNIVERSITY'); // app lists universities only
  const meta = document.getElementById('resultMeta');
  const results = document.getElementById('results');
  results.innerHTML = '<p class="muted">Loading…</p>';
  try {
    const page = await api('/schools?' + params.toString());
    meta.textContent = `${page.totalElements} school(s) found`;
    results.innerHTML = page.content.length ? page.content.map(cardHtml).join('') : '<div class="empty">No schools match your filters.</div>';
  } catch (e) {
    results.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}<br><small>Is the API server running on :8080?</small></div>`;
  }
}

// ---------- favourites ----------
async function toggleFav(id, btn) {
  if (!session()) { toast('Sign in to save favourites'); return; }
  const isFav = btn.textContent.trim() === '❤️';
  try {
    await api('/favorites/' + id, { method: isFav ? 'DELETE' : 'POST' });
    btn.textContent = isFav ? '🤍' : '❤️';
    toast(isFav ? 'Removed from favourites' : 'Added to favourites');
    if (currentView === 'favorites') loadFavorites();
  } catch (e) { toast(e.message); }
}
async function loadFavorites() {
  const wrap = document.getElementById('favorites');
  const hint = document.getElementById('favHint');
  if (!session()) { hint.textContent = 'Sign in (top right) to save and view favourites.'; wrap.innerHTML = ''; return; }
  hint.textContent = '';
  wrap.innerHTML = '<p class="muted">Loading…</p>';
  try {
    const list = await api('/favorites');
    wrap.innerHTML = list.length ? list.map(cardHtml).join('') : '<div class="empty">No favourites yet — tap 🤍 on a school.</div>';
  } catch (e) { wrap.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}

// ---------- compare ----------
async function loadCompare() {
  const wrap = document.getElementById('compareResult');
  if (compareIds.length < 2) { wrap.innerHTML = '<div class="empty">Select at least 2 schools to compare.</div>'; return; }
  wrap.innerHTML = '<p class="muted">Loading…</p>';
  try {
    const data = await api('/schools/compare?ids=' + compareIds.join(','));
    const rows = [
      ['Category', (s) => catLabel(s.category)],
      ['City', (s) => esc(s.city || '—')],
      ['Region', (s) => esc(s.region || '—')],
      ['Tuition', (s) => fmtMoney(s.tuitionFee, s.currency), 'cheapestSchoolId'],
      ['Rating', (s) => `${stars(s.averageRating)} (${s.ratingCount})`, 'highestRatedSchoolId'],
      ['Programs', (s) => s.programs.map((p) => esc(p.name)).join('<br>') || '—'],
    ];
    let html = '<table class="compare"><tr><th>Field</th>' + data.schools.map((s) => `<th>${esc(s.name)}</th>`).join('') + '</tr>';
    for (const [label, fn, bestKey] of rows) {
      html += '<tr><th>' + label + '</th>';
      for (const s of data.schools) {
        const best = bestKey && data[bestKey] === s.id;
        html += `<td class="${best ? 'best' : ''}">${fn(s)}${best ? '<span class="tag-best">✓ best</span>' : ''}</td>`;
      }
      html += '</tr>';
    }
    html += '</table>';
    html += '<p class="muted" style="margin-top:10px">Cheapest and highest-rated are highlighted.</p>';
    html += '<button class="btn" style="margin-top:8px" onclick="clearCompare()">Clear selection</button>';
    wrap.innerHTML = html;
  } catch (e) { wrap.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}
function clearCompare() { compareIds = []; saveCompare(); loadCompare(); if (currentView === 'discover') loadSchools(); }

// Pick a topical image keyword, icon and colour for a programme based on its field.
function progField(text) {
  const t = (text || '').toLowerCase();
  const map = [
    [/(software|comput|\bit\b|information tech|network|cloud|database|cyber|web|e-?commerce|digital|systems|graphics)/, { kw: 'computer,technology', icon: '💻', c: ['#0d6e6e', '#09504f'] }],
    [/(nurs|midwif|medical|pharmac|health|physio|medicine|biomed|laborator)/, { kw: 'medical,hospital', icon: '🩺', c: ['#c0392b', '#7b241c'] }],
    [/(law|magistr|legal)/, { kw: 'law,courthouse', icon: '⚖️', c: ['#34495e', '#2c3e50'] }],
    [/(account|bank|financ|econom|market|business|management|administration|bba|mba|commerce|logistic|transport|shipping|project|human resource)/, { kw: 'business,office', icon: '📊', c: ['#1f6f8b', '#16505f'] }],
    [/(engineer|civil|electric|telecom|mechanic)/, { kw: 'engineering,construction', icon: '🛠️', c: ['#e67e22', '#a85b11'] }],
    [/(journal|communicat|advertis|public relation|media)/, { kw: 'journalism,microphone', icon: '🎙️', c: ['#8e44ad', '#5e2d73'] }],
    [/(tourism|hotel|travel|catering|hospitality)/, { kw: 'hotel,tourism', icon: '🏨', c: ['#16a085', '#0e6655'] }],
    [/(bakery|food)/, { kw: 'bakery,food', icon: '🍞', c: ['#d35400', '#a04000'] }],
    [/(beauty|cosmetic|esthetic|hairdress)/, { kw: 'beauty,salon', icon: '💄', c: ['#d81b60', '#880e4f'] }],
    [/(fashion|clothing|design)/, { kw: 'fashion,tailor', icon: '👗', c: ['#6d4c41', '#4e342e'] }],
    [/(theolog|religio)/, { kw: 'church', icon: '⛪', c: ['#5d4037', '#3e2723'] }],
    [/(statistic|demograph|mathematic|physic|data|science)/, { kw: 'science,laboratory', icon: '🔬', c: ['#2980b9', '#1c5980'] }],
    [/(english|letters|arts|language)/, { kw: 'books,library', icon: '📚', c: ['#7f8c8d', '#5d6d6e'] }],
    [/(political|international relation|public administr|customs|treasury|governance)/, { kw: 'government,parliament', icon: '🏛️', c: ['#596275', '#3d4453'] }],
  ];
  for (const [re, v] of map) if (re.test(t)) return v;
  return { kw: 'university,campus', icon: '🎓', c: ['#0d6e6e', '#09504f'] };
}

// Programmes as an image slideshow (carousel).
function renderPrograms(s) {
  if (!s.programs.length) return '';
  const slides = s.programs.map((p) => {
    const f = progField(p.name + ' ' + (p.faculty || ''));
    const img = `https://loremflickr.com/640/360/${f.kw}?lock=${p.id}`;
    const meta = [p.level, p.durationMonths ? p.durationMonths + ' months' : null, fmtMoney(p.tuitionFee, s.currency)].filter(Boolean).join(' · ');
    return `
      <div class="slide">
        <div class="slide-img" style="background:linear-gradient(135deg, ${f.c[0]}, ${f.c[1]})">
          <span class="slide-emoji">${f.icon}</span>
          <img src="${img}" alt="${esc(p.name)}" loading="lazy" onerror="this.remove()">
          ${p.faculty ? `<span class="slide-fac">${esc(p.faculty)}</span>` : ''}
        </div>
        <div class="slide-body">
          <h4>${esc(p.name)}</h4>
          <div class="slide-meta">${esc(meta)}</div>
        </div>
      </div>`;
  }).join('');
  return `
    <div class="section-title">Programmes <span class="muted" style="font-weight:400">(${s.programs.length} — swipe ›)</span></div>
    <div class="carousel">
      <button type="button" class="car-btn prev" onclick="carScroll(this,-1)">‹</button>
      <div class="car-track">${slides}</div>
      <button type="button" class="car-btn next" onclick="carScroll(this,1)">›</button>
    </div>`;
}

let carTimer;
function carScroll(btn, dir) {
  const track = btn.parentElement.querySelector('.car-track');
  track.scrollBy({ left: dir * track.clientWidth * 0.9, behavior: 'smooth' });
}
function wireCarousel() {
  clearInterval(carTimer);
  const track = document.querySelector('#modalBody .car-track');
  if (!track) return;
  const advance = () => {
    const max = track.scrollWidth - track.clientWidth - 4;
    if (track.scrollLeft >= max) track.scrollTo({ left: 0, behavior: 'smooth' });
    else track.scrollBy({ left: track.clientWidth * 0.9, behavior: 'smooth' });
  };
  carTimer = setInterval(advance, 3500);
  track.addEventListener('mouseenter', () => clearInterval(carTimer));
  track.addEventListener('mouseleave', () => { clearInterval(carTimer); carTimer = setInterval(advance, 3500); });
}

// ---------- detail modal ----------
async function openDetail(id) {
  const modal = document.getElementById('modal');
  const body = document.getElementById('modalBody');
  body.innerHTML = '<p class="muted">Loading…</p>';
  modal.classList.remove('hidden');
  try {
    const s = await api('/schools/' + id);
    const reviewsPage = await api(`/schools/${id}/reviews`);
    const maps = s.latitude != null ? `https://www.google.com/maps/search/?api=1&query=${s.latitude},${s.longitude}` : null;
    body.innerHTML = `
      <span class="badge">${catLabel(s.category)}</span>
      <h2>${esc(s.name)}</h2>
      <div class="stars">${stars(s.averageRating)} <span class="muted">${s.averageRating} · ${s.ratingCount} review(s)</span></div>
      <p>${esc(s.description || '')}</p>
      <dl class="kv">
        <dt>Location</dt><dd>${esc(s.address || s.city || '—')}${s.region ? ', ' + esc(s.region) : ''}</dd>
        <dt>Tuition</dt><dd>${fmtMoney(s.tuitionFee, s.currency)}</dd>
        ${s.website ? `<dt>Website</dt><dd><a class="link" href="${esc(s.website)}" target="_blank">${esc(s.website)}</a></dd>` : ''}
        ${s.phone ? `<dt>Phone</dt><dd>${esc(s.phone)}</dd>` : ''}
        ${s.email ? `<dt>Email</dt><dd>${esc(s.email)}</dd>` : ''}
        ${maps ? `<dt>Navigate</dt><dd><a class="link" href="${maps}" target="_blank">Open in Google Maps ↗</a></dd>` : ''}
      </dl>
      ${s.history ? `<div class="section-title">History</div><p>${esc(s.history)}</p>` : ''}
      ${renderPrograms(s)}
      <div class="section-title">Reviews</div>
      <div id="reviewList">${reviewsPage.content.length ? reviewsPage.content.map(reviewHtml).join('') : '<p class="muted">No reviews yet.</p>'}</div>
      ${reviewFormHtml(id)}
    `;
    wireReviewForm(id);
    wireCarousel();
  } catch (e) { body.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}
function reviewHtml(r) {
  return `<div class="review"><div class="stars">${stars(r.rating)}</div><div>${esc(r.comment || '')}</div><div class="meta">— ${esc(r.userDisplayName || 'Anonymous')}</div></div>`;
}
function reviewFormHtml(id) {
  if (!session()) return '<p class="muted">Sign in (top right) to leave a review.</p>';
  return `
    <div class="reviewForm">
      <div class="section-title">Write a review</div>
      <div class="rate-input" id="rateInput">${[1, 2, 3, 4, 5].map((n) => `<span data-n="${n}">☆</span>`).join('')}</div>
      <textarea id="reviewComment" rows="2" placeholder="Share your experience…"></textarea>
      <button class="btn primary" id="submitReview">Submit review</button>
    </div>`;
}
function wireReviewForm(id) {
  const rate = document.getElementById('rateInput');
  if (!rate) return;
  let chosen = 0;
  const paint = (n) => rate.querySelectorAll('span').forEach((sp) => (sp.textContent = Number(sp.dataset.n) <= n ? '★' : '☆', sp.classList.toggle('on', Number(sp.dataset.n) <= n)));
  rate.querySelectorAll('span').forEach((sp) => {
    sp.onmouseenter = () => paint(Number(sp.dataset.n));
    sp.onclick = () => { chosen = Number(sp.dataset.n); paint(chosen); };
  });
  rate.onmouseleave = () => paint(chosen);
  document.getElementById('submitReview').onclick = async () => {
    if (!chosen) { toast('Pick a star rating'); return; }
    try {
      await api(`/schools/${id}/reviews`, { method: 'POST', body: JSON.stringify({ rating: chosen, comment: document.getElementById('reviewComment').value.trim() || null }) });
      toast('Review submitted ✓');
      openDetail(id);
      if (currentView === 'discover') loadSchools();
    } catch (e) { toast(e.message); }
  };
}

// ---------- view switching ----------
let currentView = 'discover';
function showView(v) {
  currentView = v;
  document.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t.dataset.view === v));
  document.querySelectorAll('.view').forEach((s) => s.classList.toggle('active', s.id === 'view-' + v));
  if (v === 'discover') loadSchools();
  if (v === 'compare') loadCompare();
  if (v === 'favorites') loadFavorites();
}

// ---------- init ----------
document.querySelectorAll('.tab').forEach((t) => (t.onclick = () => showView(t.dataset.view)));
document.getElementById('searchForm').onsubmit = (e) => { e.preventDefault(); loadSchools(); };
function closeModal() { clearInterval(carTimer); document.getElementById('modal').classList.add('hidden'); }
document.getElementById('modalClose').onclick = closeModal;
document.getElementById('modal').onclick = (e) => { if (e.target.id === 'modal') closeModal(); };

renderIdentity();
saveCompare();
loadSchools();
