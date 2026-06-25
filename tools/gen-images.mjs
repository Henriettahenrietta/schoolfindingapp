// Generates local, self-contained SVG illustrations for programme fields.
// Cameroon-university themed (flag accent + academic building) so slides are always visible
// with NO external/network images. Run: node tools/gen-images.mjs  → writes web/images/*.svg
import { writeFile, mkdir } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const OUT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', 'web', 'images');

const FIELDS = [
  { key: 'technology',    label: 'Computer & Technology',  c: ['#0d6e6e', '#09504f'], emoji: '💻' },
  { key: 'medical',       label: 'Medical & Health',       c: ['#c0392b', '#7b241c'], emoji: '🩺' },
  { key: 'law',           label: 'Law & Justice',          c: ['#34495e', '#2c3e50'], emoji: '⚖️' },
  { key: 'business',      label: 'Business & Management',   c: ['#1f6f8b', '#16505f'], emoji: '📊' },
  { key: 'engineering',   label: 'Engineering',            c: ['#e67e22', '#a85b11'], emoji: '🛠️' },
  { key: 'communication', label: 'Communication & Media',  c: ['#8e44ad', '#5e2d73'], emoji: '🎙️' },
  { key: 'tourism',       label: 'Tourism & Hotel',        c: ['#16a085', '#0e6655'], emoji: '🏨' },
  { key: 'food',          label: 'Food & Bakery',          c: ['#d35400', '#a04000'], emoji: '🍞' },
  { key: 'beauty',        label: 'Beauty & Cosmetics',     c: ['#d81b60', '#880e4f'], emoji: '💄' },
  { key: 'fashion',       label: 'Fashion & Design',       c: ['#6d4c41', '#4e342e'], emoji: '👗' },
  { key: 'theology',      label: 'Theology',               c: ['#5d4037', '#3e2723'], emoji: '⛪' },
  { key: 'science',       label: 'Science & Data',         c: ['#2980b9', '#1c5980'], emoji: '🔬' },
  { key: 'arts',          label: 'Arts & Letters',         c: ['#7f8c8d', '#5d6d6e'], emoji: '📚' },
  { key: 'government',    label: 'Governance',             c: ['#596275', '#3d4453'], emoji: '🏛️' },
  { key: 'university',    label: 'University',             c: ['#0d6e6e', '#09504f'], emoji: '🎓' },
];

const esc = (s) => s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

// A range of brown skin tones so the illustrated graduates vary (Cameroonian / Black African).
const SKINS = ['#8d5524', '#7a4b2b', '#a3683f', '#5c3a21', '#9c6b3f'];

// A Cameroonian graduate (cap, gown, brown skin) — drawn in vector so it always renders locally.
const graduate = (skin, stole) => `
  <g transform="translate(320,196)">
    <!-- gown -->
    <path d="M-66 108 L-30 0 L30 0 L66 108 Z" fill="#1f2733"/>
    <!-- stole (school colour) -->
    <path d="M-18 2 L-8 104 L0 104 L-8 2 Z" fill="${stole}"/>
    <path d="M18 2 L8 104 L0 104 L8 2 Z" fill="${stole}"/>
    <!-- neck -->
    <rect x="-9" y="-22" width="18" height="26" fill="${skin}"/>
    <!-- head -->
    <circle cx="0" cy="-46" r="31" fill="${skin}"/>
    <circle cx="-30" cy="-46" r="5" fill="${skin}"/>
    <circle cx="30" cy="-46" r="5" fill="${skin}"/>
    <!-- hair -->
    <path d="M-31 -54 Q0 -86 31 -54 Q31 -74 0 -78 Q-31 -74 -31 -54 Z" fill="#241a12"/>
    <!-- mortarboard -->
    <polygon points="0,-92 48,-74 0,-56 -48,-74" fill="#10151c"/>
    <circle cx="0" cy="-74" r="4" fill="#fcd116"/>
    <path d="M0 -74 L44 -74 L44 -44" stroke="#fcd116" stroke-width="2.5" fill="none"/>
    <circle cx="44" cy="-40" r="5" fill="#fcd116"/>
  </g>`;

const svg = (f, i) => `<svg xmlns="http://www.w3.org/2000/svg" width="640" height="360" viewBox="0 0 640 360" role="img" aria-label="${esc(f.label)} — Cameroonian graduate">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0" stop-color="${f.c[0]}"/>
      <stop offset="1" stop-color="${f.c[1]}"/>
    </linearGradient>
  </defs>
  <rect width="640" height="360" fill="url(#g)"/>
  <!-- academic building watermark -->
  <g fill="#ffffff" opacity="0.10" transform="translate(320,118)">
    <polygon points="-150,40 0,-26 150,40"/>
    <rect x="-140" y="40" width="280" height="12"/>
    <rect x="-150" y="190" width="300" height="14"/>
  </g>
  ${graduate(SKINS[i % SKINS.length], f.c[0])}
  <!-- field icon badge -->
  <g transform="translate(486,250)">
    <circle cx="0" cy="0" r="40" fill="#ffffff"/>
    <circle cx="0" cy="0" r="40" fill="none" stroke="${f.c[1]}" stroke-width="3"/>
    <text x="0" y="2" font-size="42" text-anchor="middle" dominant-baseline="central">${f.emoji}</text>
  </g>
  <!-- Cameroon flag accent -->
  <g transform="translate(22,22)">
    <rect x="0" y="0" width="22" height="46" fill="#007a5e"/>
    <rect x="22" y="0" width="22" height="46" fill="#ce1126"/>
    <rect x="44" y="0" width="22" height="46" fill="#fcd116"/>
    <text x="33" y="30" font-size="20" text-anchor="middle" fill="#fcd116">★</text>
  </g>
  <!-- label strip -->
  <rect x="0" y="306" width="640" height="54" fill="#000000" opacity="0.32"/>
  <text x="320" y="340" font-size="28" font-family="Segoe UI, Roboto, Arial, sans-serif" font-weight="700" text-anchor="middle" fill="#ffffff">${esc(f.label)}</text>
</svg>
`;

await mkdir(OUT, { recursive: true });
let i = 0;
for (const f of FIELDS) {
  await writeFile(path.join(OUT, `${f.key}.svg`), svg(f, i++), 'utf8');
  console.log('wrote', `web/images/${f.key}.svg`);
}
console.log('Done:', FIELDS.length, 'images');
