// Minimal static-file server for the School Finder web frontend (pure Node, no deps).
// Serves the web/ folder on http://localhost:3000. The frontend calls the API on :8080.
//
// Run:  node tools/web-server.mjs
import http from 'node:http';
import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const PORT = process.env.WEB_PORT || 3000;
const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..', 'web');

const TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
};

const server = http.createServer(async (req, res) => {
  try {
    let rel = decodeURIComponent(new URL(req.url, `http://localhost:${PORT}`).pathname);
    if (rel === '/' || rel === '') rel = '/index.html';
    // Resolve and guard against path traversal
    const filePath = path.join(ROOT, rel);
    if (!filePath.startsWith(ROOT)) { res.writeHead(403); return res.end('Forbidden'); }
    const data = await readFile(filePath);
    res.writeHead(200, {
      'Content-Type': TYPES[path.extname(filePath)] || 'application/octet-stream',
      'Cache-Control': 'no-cache, no-store, must-revalidate',
    });
    res.end(data);
  } catch {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('Not found');
  }
});

server.listen(PORT, () => {
  console.log(`School Finder web frontend on http://localhost:${PORT}`);
  console.log('(Make sure the API server is running:  node tools/mock-server.mjs)');
});
