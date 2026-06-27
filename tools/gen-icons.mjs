// Generates PWA PNG icons (maroon with a white "graduation" mark) — no dependencies.
// Run: node tools/gen-icons.mjs  → writes web/icons/icon-192.png, icon-512.png, icon-180.png
import { deflateSync, crc32 } from 'node:zlib';
import { writeFileSync, mkdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const OUT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', 'web', 'icons');
mkdirSync(OUT, { recursive: true });

const MAROON = [128, 0, 32];
const WHITE = [255, 255, 255];

// Pixel colour: maroon background, a white circle, with a maroon "cap" diamond inside it.
function pixel(x, y, size) {
  const cx = size / 2, cy = size / 2;
  const d = Math.hypot(x - cx, y - cy);
  if (d > size * 0.34) return MAROON;            // background
  // graduation-cap board: a maroon diamond in the upper part of the white disc
  const dx = Math.abs(x - cx), dy = y - (cy - size * 0.04);
  if (dx + Math.abs(dy) < size * 0.2 && dy < size * 0.02) return MAROON;
  return WHITE;
}

function chunk(type, data) {
  const len = Buffer.alloc(4); len.writeUInt32BE(data.length, 0);
  const body = Buffer.concat([Buffer.from(type), data]);
  const crc = Buffer.alloc(4); crc.writeUInt32BE(crc32(body) >>> 0, 0);
  return Buffer.concat([len, body, crc]);
}

function png(size) {
  const raw = Buffer.alloc(size * (size * 3 + 1));
  let p = 0;
  for (let y = 0; y < size; y++) {
    raw[p++] = 0; // filter: none
    for (let x = 0; x < size; x++) {
      const [r, g, b] = pixel(x, y, size);
      raw[p++] = r; raw[p++] = g; raw[p++] = b;
    }
  }
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(size, 0); ihdr.writeUInt32BE(size, 4);
  ihdr[8] = 8; ihdr[9] = 2; // 8-bit, RGB
  const sig = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  return Buffer.concat([sig, chunk('IHDR', ihdr), chunk('IDAT', deflateSync(raw)), chunk('IEND', Buffer.alloc(0))]);
}

for (const size of [192, 512, 180]) {
  writeFileSync(path.join(OUT, `icon-${size}.png`), png(size));
  console.log('wrote', `web/icons/icon-${size}.png`);
}
