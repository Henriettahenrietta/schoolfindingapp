// Vercel serverless function — handles all /api/* (and /actuator/*) requests
// by delegating to the same request handler the local/Render server uses.
import { handler } from '../tools/mock-server.mjs';

export default function (req, res) {
  return handler(req, res);
}
