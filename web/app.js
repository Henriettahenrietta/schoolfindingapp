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

// ---------- Firebase Auth (real, when configured) ----------
const FB_ENABLED = () => typeof firebase !== 'undefined' && !!window.FIREBASE_CONFIG && !!window.FIREBASE_CONFIG.apiKey;

if (FB_ENABLED()) {
  firebase.initializeApp(window.FIREBASE_CONFIG);
  firebase.auth().onAuthStateChanged(async (user) => {
    if (user) {
      try {
        const me = await api('/me');
        setSession({ uid: me.firebaseUid, name: me.displayName || user.email, role: me.role });
      } catch (e) { /* backend unreachable */ }
    } else {
      localStorage.removeItem('sf_session');
      renderIdentity();
    }
  });
}

// ---------- API helper ----------
async function api(path, opts = {}) {
  const headers = Object.assign({ 'Content-Type': 'application/json' }, opts.headers || {});
  if (FB_ENABLED() && firebase.auth().currentUser) {
    headers['Authorization'] = 'Bearer ' + (await firebase.auth().currentUser.getIdToken());
  } else {
    const s = session();
    if (s) {
      headers['X-Debug-Uid'] = s.uid;
      headers['X-Debug-Name'] = s.name;
      headers['X-Debug-Role'] = s.role;
    }
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
    document.getElementById('signout').onclick = doSignOut;
  } else {
    el.innerHTML = `<button id="openAuth">Sign in</button>`;
    document.getElementById('openAuth').onclick = openAuth;
  }
  const admin = session()?.role === 'ADMIN';
  document.querySelectorAll('.admin-only').forEach((t) => { t.style.display = admin ? '' : 'none'; });
  if (!admin && currentView === 'admin') showView('discover');
}

// ---------- auth modal ----------
let authMode = 'signin';
function openAuth() { document.getElementById('authModal').classList.remove('hidden'); renderAuth(); }
function closeAuth() { document.getElementById('authModal').classList.add('hidden'); }

function renderAuth() {
  const body = document.getElementById('authBody');
  if (FB_ENABLED()) {
    body.innerHTML = `
      <div class="auth-tabs">
        <button class="atab ${authMode === 'signin' ? 'active' : ''}" data-m="signin">Sign in</button>
        <button class="atab ${authMode === 'signup' ? 'active' : ''}" data-m="signup">Create account</button>
      </div>
      <button class="gbtn gbtn-lg" id="aGoogle"><span class="gicon">G</span> Continue with Google</button>
      <div class="auth-or"><span>or ${authMode === 'signin' ? 'sign in' : 'sign up'} with email</span></div>
      <input id="aEmail" type="email" placeholder="Email" class="auth-inp" autocomplete="email">
      <input id="aPass" type="password" placeholder="Password (6+ characters)" class="auth-inp" autocomplete="current-password">
      <button class="btn primary auth-submit" id="aSubmit">${authMode === 'signin' ? 'Sign in' : 'Create account'}</button>
      <div id="aErr" class="auth-err"></div>`;
    body.querySelectorAll('.atab').forEach((b) => (b.onclick = () => { authMode = b.dataset.m; renderAuth(); }));
    document.getElementById('aGoogle').onclick = fbGoogle;
    document.getElementById('aSubmit').onclick = () => fbAuth(authMode === 'signup');
    document.getElementById('aPass').onkeydown = (e) => { if (e.key === 'Enter') fbAuth(authMode === 'signup'); };
  } else {
    body.innerHTML = `
      <p class="muted">Demo sign-in. (Add your Firebase config in <code>web/firebase-config.js</code> to enable real Google / email accounts.)</p>
      <button class="btn primary auth-submit" data-as="student">Continue as student</button>
      <button class="btn auth-submit" data-as="admin">Continue as admin</button>`;
    body.querySelectorAll('button[data-as]').forEach((b) => (b.onclick = () => { setSession(PRESETS[b.dataset.as]); closeAuth(); }));
  }
}

async function fbGoogle() {
  const err = document.getElementById('aErr');
  try {
    await firebase.auth().signInWithPopup(new firebase.auth.GoogleAuthProvider());
    closeAuth();
  } catch (e) { if (err) err.textContent = e.message; else toast(e.message); }
}

async function fbAuth(register) {
  const email = document.getElementById('aEmail')?.value.trim();
  const pass = document.getElementById('aPass')?.value;
  const err = document.getElementById('aErr');
  if (err) err.textContent = '';
  if (!email || !pass) { if (err) err.textContent = 'Enter your email and password.'; return; }
  try {
    if (register) await firebase.auth().createUserWithEmailAndPassword(email, pass);
    else await firebase.auth().signInWithEmailAndPassword(email, pass);
    closeAuth();
  } catch (e) { if (err) err.textContent = e.message; else toast(e.message); }
}

function doSignOut() {
  if (FB_ENABLED() && firebase.auth().currentUser) firebase.auth().signOut();
  else setSession(null);
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
    [/(software|comput|\bit\b|information tech|network|cloud|database|cyber|web|e-?commerce|digital|systems|graphics)/, { key: 'technology', icon: '💻', c: ['#0d6e6e', '#09504f'] }],
    [/(nurs|midwif|medical|pharmac|health|physio|medicine|biomed|laborator)/, { key: 'medical', icon: '🩺', c: ['#c0392b', '#7b241c'] }],
    [/(law|magistr|legal)/, { key: 'law', icon: '⚖️', c: ['#34495e', '#2c3e50'] }],
    [/(account|bank|financ|econom|market|business|management|administration|bba|mba|commerce|logistic|transport|shipping|project|human resource)/, { key: 'business', icon: '📊', c: ['#1f6f8b', '#16505f'] }],
    [/(engineer|civil|electric|telecom|mechanic)/, { key: 'engineering', icon: '🛠️', c: ['#e67e22', '#a85b11'] }],
    [/(journal|communicat|advertis|public relation|media)/, { key: 'communication', icon: '🎙️', c: ['#8e44ad', '#5e2d73'] }],
    [/(tourism|hotel|travel|catering|hospitality)/, { key: 'tourism', icon: '🏨', c: ['#16a085', '#0e6655'] }],
    [/(bakery|food)/, { key: 'food', icon: '🍞', c: ['#d35400', '#a04000'] }],
    [/(beauty|cosmetic|esthetic|hairdress)/, { key: 'beauty', icon: '💄', c: ['#d81b60', '#880e4f'] }],
    [/(fashion|clothing|design)/, { key: 'fashion', icon: '👗', c: ['#6d4c41', '#4e342e'] }],
    [/(theolog|religio)/, { key: 'theology', icon: '⛪', c: ['#5d4037', '#3e2723'] }],
    [/(statistic|demograph|mathematic|physic|data|science)/, { key: 'science', icon: '🔬', c: ['#2980b9', '#1c5980'] }],
    [/(english|letters|arts|language)/, { key: 'arts', icon: '📚', c: ['#7f8c8d', '#5d6d6e'] }],
    [/(political|international relation|public administr|customs|treasury|governance)/, { key: 'government', icon: '🏛️', c: ['#596275', '#3d4453'] }],
  ];
  for (const [re, v] of map) if (re.test(t)) return v;
  return { key: 'university', icon: '🎓', c: ['#0d6e6e', '#09504f'] };
}

// Programmes as an image slideshow (carousel).
function renderPrograms(s) {
  if (!s.programs.length) return '';
  const slides = s.programs.map((p) => {
    const f = progField(p.name + ' ' + (p.faculty || ''));
    const img = `images/${f.key}.svg`; // local, always available
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

// ---------- school photo gallery + admin upload (Cloudinary) ----------
let currentDetailId = null;

function renderGallery(s) {
  if (!s.images || !s.images.length) return '';
  const admin = session()?.role === 'ADMIN';
  return '<div class="gallery">' + s.images.map((im) =>
    `<div class="gthumb"><img src="${im.url}" alt="${esc(im.caption || '')}" loading="lazy">` +
    (admin ? `<button class="gdel" title="Remove" onclick="deleteImage(${im.id})">✕</button>` : '') +
    '</div>').join('') + '</div>';
}

function uploadFormHtml() {
  return `
    <div class="uploadbox">
      <div class="section-title" style="margin-top:6px">Add photo → Cloudinary</div>
      <input type="file" id="imgFile" accept="image/*">
      <input type="text" id="imgCaption" placeholder="Caption (optional)">
      <label class="ck"><input type="checkbox" id="imgCover"> Set as cover</label>
      <button class="btn primary" id="imgUpload">Upload</button>
      <span id="imgStatus" class="muted"></span>
    </div>`;
}

function wireImageUpload(id) {
  const btn = document.getElementById('imgUpload');
  if (!btn) return;
  btn.onclick = () => {
    const f = document.getElementById('imgFile').files[0];
    if (!f) { toast('Choose an image first'); return; }
    const status = document.getElementById('imgStatus');
    status.textContent = 'Uploading…';
    const reader = new FileReader();
    reader.onload = async () => {
      try {
        await api(`/admin/schools/${id}/images`, {
          method: 'POST',
          body: JSON.stringify({
            file: reader.result,
            caption: document.getElementById('imgCaption').value.trim() || null,
            setCover: document.getElementById('imgCover').checked,
          }),
        });
        toast('Photo uploaded to Cloudinary ✓');
        openDetail(id);
      } catch (e) { status.textContent = ''; toast(e.message); }
    };
    reader.readAsDataURL(f);
  };
}

async function deleteImage(imgId) {
  try {
    await api(`/admin/schools/images/${imgId}`, { method: 'DELETE' });
    toast('Photo removed');
    if (currentDetailId != null) openDetail(currentDetailId);
  } catch (e) { toast(e.message); }
}

// ---------- detail modal ----------
async function openDetail(id) {
  currentDetailId = id;
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
      ${renderGallery(s)}
      ${session()?.role === 'ADMIN' ? uploadFormHtml() : ''}
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
    wireImageUpload(id);
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

// ---------- admin dashboard ----------
let adminSub = 'analytics';

function loadAdmin() {
  document.querySelectorAll('.asub').forEach((b) => b.classList.toggle('active', b.dataset.asub === adminSub));
  const panel = document.getElementById('adminPanel');
  panel.innerHTML = '<p class="muted">Loading…</p>';
  ({ analytics: adminAnalytics, schools: adminSchools, users: adminUsers, reviews: adminReviews }[adminSub] || adminAnalytics)(panel);
}

async function adminAnalytics(panel) {
  try {
    const a = await api('/admin/analytics');
    panel.innerHTML = `
      <div class="stat-grid">
        <div class="stat"><div class="stat-n">${a.totalSchools}</div><div class="stat-l">Universities</div></div>
        <div class="stat"><div class="stat-n">${a.totalUsers}</div><div class="stat-l">Users</div></div>
        <div class="stat"><div class="stat-n">${a.totalReviews}</div><div class="stat-l">Reviews</div></div>
      </div>
      <div class="section-title">Universities by category</div>
      <ul>${Object.entries(a.schoolsByCategory).map(([k, v]) => `<li>${esc(k)}: <b>${v}</b></li>`).join('')}</ul>`;
  } catch (e) { panel.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}

async function adminSchools(panel) {
  try {
    const pg = await api('/schools?category=UNIVERSITY&size=100&sort=name');
    const rows = pg.content.map((s) => `<tr><td>${s.id}</td><td>${esc(s.name)}</td><td>${esc(s.city || '')}</td><td>${fmtMoney(s.tuitionFee, s.currency)}</td><td><button class="btn" onclick="openDetail(${s.id})">View</button> <button class="btn danger" onclick="adminDeleteSchool(${s.id})">Delete</button></td></tr>`).join('');
    panel.innerHTML = `
      <div class="section-title">Add university</div>
      <div class="adminform">
        <input id="ns_name" placeholder="Name *">
        <input id="ns_city" placeholder="City" value="Yaoundé">
        <input id="ns_tuition" type="number" placeholder="Tuition (FCFA)">
        <input id="ns_desc" placeholder="Description">
        <button class="btn primary" id="ns_add">Add</button>
      </div>
      <div class="section-title">Universities (${pg.totalElements})</div>
      <div class="tablewrap"><table class="atable"><tr><th>ID</th><th>Name</th><th>City</th><th>Tuition</th><th></th></tr>${rows}</table></div>`;
    document.getElementById('ns_add').onclick = async () => {
      const name = document.getElementById('ns_name').value.trim();
      if (!name) { toast('Name required'); return; }
      try {
        await api('/admin/schools', { method: 'POST', body: JSON.stringify({
          name,
          category: 'UNIVERSITY',
          city: document.getElementById('ns_city').value.trim() || 'Yaoundé',
          tuitionFee: Number(document.getElementById('ns_tuition').value) || null,
          description: document.getElementById('ns_desc').value.trim() || null,
        }) });
        toast('University added');
        loadAdmin();
      } catch (e) { toast(e.message); }
    };
  } catch (e) { panel.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}
async function adminDeleteSchool(id) {
  if (!confirm('Delete this university and its programmes/reviews?')) return;
  try { await api('/admin/schools/' + id, { method: 'DELETE' }); toast('Deleted'); loadAdmin(); } catch (e) { toast(e.message); }
}

async function adminUsers(panel) {
  try {
    const list = await api('/admin/users');
    const rows = list.map((u) => `<tr><td>${u.id}</td><td>${esc(u.displayName || '')}</td><td>${esc(u.email || '')}</td><td>${u.role}</td><td>${u.active ? '✅' : '🚫'}</td><td>
      <button class="btn" onclick="adminSetRole(${u.id},'${u.role === 'ADMIN' ? 'STUDENT' : 'ADMIN'}')">${u.role === 'ADMIN' ? 'Make student' : 'Make admin'}</button>
      <button class="btn" onclick="adminSetActive(${u.id},${!u.active})">${u.active ? 'Deactivate' : 'Activate'}</button></td></tr>`).join('');
    panel.innerHTML = `<div class="section-title">Users (${list.length})</div><div class="tablewrap"><table class="atable"><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Active</th><th></th></tr>${rows}</table></div>`;
  } catch (e) { panel.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}
async function adminSetRole(id, role) { try { await api(`/admin/users/${id}/role?role=${role}`, { method: 'PUT' }); toast('Role updated'); loadAdmin(); } catch (e) { toast(e.message); } }
async function adminSetActive(id, active) { try { await api(`/admin/users/${id}/active?active=${active}`, { method: 'PUT' }); toast('Updated'); loadAdmin(); } catch (e) { toast(e.message); } }

async function adminReviews(panel) {
  try {
    const pg = await api('/admin/reviews/pending');
    if (!pg.content.length) { panel.innerHTML = '<div class="empty">No pending reviews 🎉</div>'; return; }
    panel.innerHTML = `<div class="section-title">Pending reviews (${pg.totalElements})</div>` + pg.content.map((r) => `
      <div class="review">
        <div class="stars">${stars(r.rating)}</div>
        <div>${esc(r.comment || '')}</div>
        <div class="meta">${esc(r.schoolName || '')} — ${esc(r.userDisplayName || 'Anonymous')}</div>
        <div style="margin-top:6px">
          <button class="btn primary" onclick="adminModerate(${r.id},'APPROVED')">Approve</button>
          <button class="btn danger" onclick="adminModerate(${r.id},'REJECTED')">Reject</button>
        </div>
      </div>`).join('');
  } catch (e) { panel.innerHTML = `<div class="empty">⚠️ ${esc(e.message)}</div>`; }
}
async function adminModerate(id, status) { try { await api(`/admin/reviews/${id}/status?status=${status}`, { method: 'PUT' }); toast('Review ' + status.toLowerCase()); loadAdmin(); } catch (e) { toast(e.message); } }

// ---------- view switching ----------
let currentView = 'discover';
function showView(v) {
  currentView = v;
  document.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t.dataset.view === v));
  document.querySelectorAll('.view').forEach((s) => s.classList.toggle('active', s.id === 'view-' + v));
  if (v === 'discover') loadSchools();
  if (v === 'compare') loadCompare();
  if (v === 'favorites') loadFavorites();
  if (v === 'admin') loadAdmin();
}

// ---------- init ----------
document.querySelectorAll('.tab').forEach((t) => (t.onclick = () => showView(t.dataset.view)));
document.querySelectorAll('.asub').forEach((b) => (b.onclick = () => { adminSub = b.dataset.asub; loadAdmin(); }));
document.getElementById('searchForm').onsubmit = (e) => { e.preventDefault(); loadSchools(); };
function closeModal() { clearInterval(carTimer); document.getElementById('modal').classList.add('hidden'); }
document.getElementById('modalClose').onclick = closeModal;
document.getElementById('modal').onclick = (e) => { if (e.target.id === 'modal') closeModal(); };
document.getElementById('authClose').onclick = closeAuth;
document.getElementById('authModal').onclick = (e) => { if (e.target.id === 'authModal') closeAuth(); };

renderIdentity();
saveCompare();
loadSchools();
