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

const svg = (f) => `<svg xmlns="http://www.w3.org/2000/svg" width="640" height="360" viewBox="0 0 640 360" role="img" aria-label="${f.label}">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0" stop-color="${f.c[0]}"/>
      <stop offset="1" stop-color="${f.c[1]}"/>
    </linearGradient>
  </defs>
  <rect width="640" height="360" fill="url(#g)"/>
  <!-- academic building watermark -->
  <g fill="#ffffff" opacity="0.13" transform="translate(320,120)">
    <polygon points="-120,42 0,-28 120,42"/>
    <rect x="-112" y="42" width="224" height="14"/>
    <rect x="-100" y="62" width="18" height="120"/>
    <rect x="-62" y="62" width="18" height="120"/>
    <rect x="-24" y="62" width="18" height="120"/>
    <rect x="14" y="62" width="18" height="120"/>
    <rect x="52" y="62" width="18" height="120"/>
    <rect x="90" y="62" width="18" height="120"/>
    <rect x="-114" y="184" width="228" height="16"/>
  </g>
  <!-- Cameroon flag accent -->
  <g transform="translate(22,22)">
    <rect x="0" y="0" width="22" height="46" fill="#007a5e"/>
    <rect x="22" y="0" width="22" height="46" fill="#ce1126"/>
    <rect x="44" y="0" width="22" height="46" fill="#fcd116"/>
    <text x="33" y="30" font-size="20" text-anchor="middle" fill="#fcd116">★</text>
  </g>
  <!-- field icon -->
  <text x="320" y="172" font-size="104" text-anchor="middle" dominant-baseline="central">${f.emoji}</text>
  <!-- field label -->
  <text x="320" y="318" font-size="30" font-family="Segoe UI, Roboto, Arial, sans-serif" font-weight="700" text-anchor="middle" fill="#ffffff">${f.label}</text>
</svg>
`;

await mkdir(OUT, { recursive: true });
for (const f of FIELDS) {
  await writeFile(path.join(OUT, `${f.key}.svg`), svg(f), 'utf8');
  console.log('wrote', `web/images/${f.key}.svg`);
}
console.log('Done:', FIELDS.length, 'images');
