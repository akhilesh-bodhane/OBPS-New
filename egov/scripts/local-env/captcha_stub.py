# Local stand-in for the Google reCAPTCHA verification endpoint: always answers success.
# Listens on 127.0.0.1:9099 (override.properties: captcha.verification.url=http://localhost:9099/verify).
from http.server import BaseHTTPRequestHandler, HTTPServer


class H(BaseHTTPRequestHandler):
    def do_POST(self):
        self.rfile.read(int(self.headers.get('Content-Length', 0) or 0))
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(b'{"success": true}')

    def log_message(self, *a):
        pass


HTTPServer(('127.0.0.1', 9099), H).serve_forever()
