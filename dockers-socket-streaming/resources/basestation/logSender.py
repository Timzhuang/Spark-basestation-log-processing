#!/usr/bin/env python3
import sys
import socket

HOST = socket.gethostname()
PORT = 12345

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print('Connected by', addr)
        for l in sys.stdin:
            conn.sendall(l.encode())
