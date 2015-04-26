import socket
sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sock.bind(("127.0.0.1",8000))
print("Server is running now.. Congratulation Daniar!")

sock.listen(20)
(client,(ip,port))=sock.accept()
#print(client)
client.send("Welcome Daniar \n".encode())
rec = ""
data = ""
#runServer = True
#while runServer:
loop = True
while loop:
	rec += client.recv(1024).decode()
	rec_end = rec.find('\n')
	if rec_end != -1:
		loop = False
		data = rec[:rec_end]
		rec = rec[rec_end+1:]
		print(data)
		if (data.find('exit') != -1):
			print("stop wooy")
			#runServer = False

def do_GET(self):
	print(self.path)