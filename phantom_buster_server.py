from http.server import BaseHTTPRequestHandler, HTTPServer
import json

VALID_API_TOKEN = "Bearer abc"


class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        print(f"Request received: {self.command} {self.path}")
        content_length = self.headers.get('Content-Length')
        print(f"Content-Length: {content_length}")

        if self.path != "/api/alumni/search":
            self._send_response(404, {"status": "error", "message": "Endpoint not found"})
            print("Error: Endpoint not found")
            return

        content_length = self.headers.get('Content-Length')
        content_length = int(content_length) if content_length else 0
        post_data = self.rfile.read(content_length) if content_length > 0 else b"{}"

        try:
            data = json.loads(post_data)
            print(f"Request body: {data}")
        except json.JSONDecodeError:
            self._send_response(400, {"status": "error", "message": "Invalid JSON format"})
            print("Error: Invalid JSON format")
            return

        auth_header = self.headers.get('Authorization')
        if auth_header != VALID_API_TOKEN:
            self._send_response(401, {"status": "error", "message": "Unauthorized. Invalid or missing token."})
            print("Error: Unauthorized or invalid token")
            return

        university = data.get('university')
        if not university:
            self._send_response(400, {"status": "error", "message": "University is required"})
            print("Error: University is missing in the request")
            return

        designation = data.get('designation', "")
        mock_response = get_mock_alumni_data(university, designation)
        self._send_response(200, mock_response)
        print(f"Response sent: {mock_response}")

    def _send_response(self, status_code, response_data):
        self.send_response(status_code)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        self.wfile.write(json.dumps(response_data).encode())
        print(f"Response status: {status_code}, Response data: {response_data}")


def get_mock_alumni_data(university, designation):
    mock_data = [
        {
            "name": "John Doe",
            "currentRole": "Software Engineer",
            "university": university,
            "location": "San Francisco, CA",
            "linkedinHeadline": f"{designation or 'Software Engineer'} at Google",
            "passoutYear": 2020,
        },
        {
            "name": "Jane Smith",
            "currentRole": "Data Scientist",
            "university": university,
            "location": "New York, NY",
            "linkedinHeadline": f"{designation or 'Data Scientist'} | AI Enthusiast",
            "passoutYear": 2019,
        },
    ]
    return {"status": "success", "data": mock_data}


def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=7009):
    server_address = ("", port)
    httpd = server_class(server_address, handler_class)
    print(f"Starting mock server on port {port}...")
    httpd.serve_forever()


if __name__ == "__main__":
    run()
