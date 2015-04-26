import socket
import json
import time
import hashlib
import random
import calendar
import time
from pymongo import MongoClient
from threading import Thread
from threading import Lock

debug = True

def recievejson(sock):
    data = ''
    while not is_json(data):
        try:
            chunk = sock.recv(4096).decode('utf-8')
            data += chunk
        except socket.timeout:
            break
    if is_json(data):
        return json.loads(data)
    else:
        return None

def getipaddress():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(('itb.ac.id', 0))
    ip = s.getsockname()[0]
    s.close()
    return ip

def genhash(username, password):
    h = hashlib.md5()
    h.update((username + ":").encode('utf-8'))
    h.update(password.encode('utf-8'))
    tyme = time.gmtime()
    h.update(time.strftime("%Y-%m-%d %H:%M:%S",tyme).encode('utf-8'))
    import calendar
    return (h.hexdigest(), calendar.timegm(tyme))

def genrandomhash():
    hash = random.getrandbits(128)
    return str(hash)

def mixitem(item1, item2):
    if item1==0 and item2==1 or item1==1 and item2==0:
       return 4
    elif item1==1 and item2==2 or item1==2 and item2==1:
       return 5
    elif item1==2 and item2==3 or item1==3 and item2==2:
       return 6
    elif item1==5 and item2==4 or item1==4 and item2==5:
        return 7
    elif item1==5 and item2==6 or item1==6 and item2==5:
       return 8
    elif item1==7 and item2==8 or item1==8 and item2==7:
        return 9
    else:
        return 10

def is_json(data):
    try:
        json_object = json.loads(data)
    except ValueError:
        return False
    return True

class SpfServer:
    def __init__(self,addr,port,backup,db):
        self.upper = []
        self.downer = []
        self.ip = addr
        self.port = port
        self.backup = backup['value']
        self.main_ip = backup['ip']
        self.main_port = backup['port']
        self.backup_ip = ''
        self.backup_port = 0
        self.servers = []
        self.dbname = db
        self.initdb(self.dbname)
        self.initserver()
        self.itemcachelock = Lock()
        self.tracker = socket.socket()
        if not self.backup:
            self.conntracker()
            print('You are the main server')
        else:
            self.connmainserver()
            print('You are the backup server')


    def __del__(self):
        if hasattr(self, 'dbclient'):
            self.dbclient.close()
            del self.db
        if hasattr(self, 'tracker'):
            self.tracker.close()
        if hasattr(self, 'server'):
            self.server.close()

    def initdb(self,dbname):
        self.dbclient = MongoClient()
        self.db = self.dbclient[dbname]
        print('Database Connected')

    def conntracker(self):
        self.tracker.connect(('167.205.32.46',8000))
        print('Tracker Connected')

    def connmainserver(self):
        self.tracker.connect((self.main_ip,self.main_port))
        print('Main Server Connected')

    def initserver(self): # listening socket
        self.server = socket.socket()
        self.server.bind(('',self.port))
        print(self.ip+','+str(self.port))
        print('Server ready to listen')

    def gettracker(self): # hanya dipanggil pada awal koneksi ke tracker
        message = '{"method":"join", "ip":"'+self.ip+'","port":'+str(self.port)+'}'
        self.tracker.send(bytes(message,'utf-8'))
        data = ''
        while not is_json(data):
            chunk = self.tracker.recv(4096).decode('utf-8')
            data += chunk
        if debug:
            print(data)
        if data != "":
            decoded = json.loads(data)
            status = decoded['status']
            if status == "ok":
                self.servers = decoded['value']
                return 0
            else:
                self.servers = None
                self.error = decoded['description']
                return 1

    def getcacheitem(self,item):
        self.itemcachelock.acquire()

        refresh = False
        # cari informasi cache offer
        infocache = self.db.offercache.find_one({'item_number':item})
        jsonofferlist = None
        if (infocache == None):
            refresh = True
        else:
            # kalau cache kosong atau cache expired (lebih dari 3000 detik)
            if (calendar.timegm(time.gmtime()) - infocache['time']) > 3000 :
                refresh = True
                self.db.offercache.remove({'item_number':item})
                self.db.offercache.remove({'offered_item':item})
            else:
                jsonofferlist = self.db.offercache.find({'offered_item':item})

        if refresh:
            servers = self.servers
            srvthread = []
            jsonofferlist = []
            for server in servers:
                if server['ip'] == self.ip and server['port'] == self.port:
                    continue
                thr = SpfSendFindThread(server['ip'],server['port'],item)
                thr.daemon = True
                thr.start()
                srvthread.append(thr)
                for thr in srvthread:
                    thr.join()
                for thr in srvthread:
                    if thr.data == None:
                        continue
                    status = thr.data['status']
                    if status == "ok":
                        for recvoffer in thr.data['offers']:
                            if debug:
                                print(recvoffer)
                            try:
                                if int(recvoffer[0]) == item:
                                    jsonofferlist.append({'token':recvoffer[5], 'offered_item':int(recvoffer[0]), \
                                                          'n1':int(recvoffer[1]), 'demanded_item':int(recvoffer[2]), \
                                                          'n2':int(recvoffer[3]), 'availability':recvoffer[4], \
                                                          'ip':thr.server, 'port':thr.port})
                            except ValueError:
                                continue
            for offer in jsonofferlist:
                self.db.offercache.insert(offer)
            self.db.offercache.insert({'item_number':item, 'time':calendar.timegm(time.gmtime())})
        # return hasil cache dan unlock
        self.itemcachelock.release()
        return jsonofferlist

    def searchcachetoken(self,token):
        self.itemcachelock.acquire()
        result = self.db.offercache.find_one({'token':token})
        self.itemcachelock.release()
        return result

    def removecachetoken(self,token):
        self.itemcachelock.acquire()
        result = self.db.offercache.remove({'token':token})
        self.itemcachelock.release()

    def start(self):
        print('Starting')
        status = self.gettracker()
        print('Get tracker')
        print('Servers: ',self.servers)
        if status == 1:
            print('Error', self.error)
            exit()
        self.tracker.close()
        self.trkthread = SpfTrackerThread(self)
        self.trkthread.daemon = True
        self.trkthread.start()
        self.msvthread = SpfMainServerThread(self)
        self.msvthread.daemon = True
        self.msvthread.start()
        self.bckthread = SpfBackupThread(self)
        self.bckthread.daemon = True
        self.bckthread.start()
        self.server.listen(1)
        while True:
            try:
                print('Cari client')
                conn,  addr = self.server.accept()
                print('Dapet client')
                SpfListenerThread(self, conn).start()
            except SystemExit:
                print('keyboard interupt!')
                break

class SpfMainServerThread(Thread):
    def __init__(self,srv):
        Thread.__init__(self)
        self.srv = srv

    def run(self):
        while True:
            try:
                time.sleep(3)
                if self.srv.backup_ip != '' and self.srv.backup_port != 0:
                    sock = socket.socket()
                    sock.settimeout(10)
                    try:
                        sock.connect((self.srv.backup_ip,self.srv.backup_port))
                        data = {'method':'serverStatus','server':self.srv.servers}
                        data['server'].index({'ip':self.srv.ip,'port':self.srv.port})
                        jstring = json.dumps(data)
                        if debug:
                            print("Send server status",jstring)
                        sock.send(bytes(jstring,'utf-8'))
                        response = recievejson(sock)
                        if response == None:
                            response = json.loads('{"status":"fail","description":"JSON Parsing error"}')
                        if debug:
                            print("Receive server status: ",response)
                    finally:
                        sock.close()
            except SystemExit:
                break

class SpfBackupThread(Thread):
    def __init__(self,srv):
        Thread.__init__(self)
        self.srv = srv

    def run(self):
        while True:
            try:
                time.sleep(3)
                if self.srv.backup:
                    sock = socket.socket()
                    sock.settimeout(10)
                    try:
                        sock.connect((self.srv.main_ip,self.srv.main_port))
                        servers = self.srv.downer + [{'ip':self.srv.ip,'port':self.srv.port}]
                        data = {'method':'backup','server':servers}
                        jstring = json.dumps(data)
                        if debug:
                            print("Send backup: ",jstring)
                        sock.send(bytes(jstring,'utf-8'))
                        response = recievejson(sock)
                        if response == None:
                            response = json.loads('{"status":"fail","description":"JSON Parsing error"}')
                        if debug:
                            print("Receive backup: ",response)
                        if response['status']=='ok':
                            self.srv.upper = response['server']
                    except (socket.timeout,ConnectionRefusedError): #Takeover the main server
                        self.srv.upper.pop()
                        self.srv.tracker = socket.socket()
                        if self.srv.upper == []:
                            self.srv.backup = False
                            print('You are now the main server')
                            self.srv.conntracker()
                        else:
                            self.srv.main_ip = self.srv.upper[-1]['ip']
                            self.srv.main_port = self.srv.upper[-1]['port']
                            self.srv.connmainserver()
                        self.srv.gettracker()
                    finally:
                        sock.close()
            except SystemExit:
                break

class SpfTrackerThread(Thread):
    def __init__(self,srv):
        Thread.__init__(self)
        self.srv = srv

    def run(self):
        while True:
            try:
                time.sleep(10)
                print('Servers: ',self.srv.servers)
                print('('+self.srv.ip+','+str(self.srv.port)+')')
            except SystemExit:
                break

class SpfListenerThread(Thread):
    def __init__(self,srv, sock):
        Thread.__init__(self)
        self.daemon = True
        self.srv = srv
        self.sock = sock

    def process(self, message):
        if message['method']== 'signup':
            if 'username' in message and 'password' in message:
                if self.srv.db.user.find_one({'username':message['username']}) == None:
                    self.srv.db.user.insert({'username':message['username'],'password':message['password'], 'inventory':[0, 0, 0, 0, 0, 0, 0, 0, 0, 0], 'field':True, 'x':0, 'y':0})
                    return json.dumps({'status':'ok'})
                else:
                    return json.dumps({'status':'fail', 'description':'Username exists'})
            return json.dumps({'status':'error'})
        elif message['method']== 'serverStatus':
            if 'server' in message:
                self.srv.servers = message['server']
                return json.dumps({'status':'ok','servers':self.srv.servers})
            return json.dumps({'status':'error'})
        elif message['method']== 'login':
            if 'username' in message and 'password' in message:
                user = self.srv.db.user.find_one({'username':message['username'], 'password':message['password']})
                if user != None:
                    token = genhash(user['username'], user['password'])
                    if self.srv.db.active_user.find_one({'username':message['username']}) == None:
                        self.srv.db.active_user.insert({'username':message['username'],'token':token[0]})
                    else:
                        self.srv.db.active_user.update_one({'username':message['username']},{'$set':{'token':token[0]}})
                    return json.dumps({'status':'ok', 'token':token[0], 'x':user['x'], 'y':user['y'], 'time':token[1]})
                else:
                    return json.dumps({'status':'fail', 'description':'Invalid username/password'})
            return json.dumps({'status':'error'})
        elif message['method']=='inventory':
            if 'token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    inventory = self.srv.db.user.find_one({'username':active_user['username']}, {'inventory':1,'_id':0 })['inventory']
                    return json.dumps({'status':'ok', 'inventory':inventory})
            return json.dumps({'status':'error'})
        elif message['method']=='mixitem':
            if 'token' in message and 'item1' in message and 'item2' in message:
                active_user = srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    inventory = srv.db.user.find_one({'username':active_user['username']}, {'inventory':1,'_id':0 })['inventory']
                    mix = mixitem(message['item1'], message['item2'])
                    if  mix < 10 and inventory[message['item1']] > 0 and inventory[message['item2']] > 0:
                        inventory[message['item1']] -= 1
                        inventory[message['item2']] -= 1
                        inventory[mix] += 1
                        srv.db.user.update_one({'username':active_user['username']},{'$set':{'inventory':inventory}})
                        return json.dumps({'status':'ok', 'inventory':inventory})
                    elif mix == 10:
                        return json.dumps({'status':'fail', 'description':'Wrong mixture'})
                    elif inventory[message['item1']] == 0 or inventory[message['item2']] == 0:
                        return json.dumps({'status':'fail', 'description':'Not enough item'})
            return json.dumps({'status':'error'})
        elif message['method']=='map':
            if 'token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    map = self.srv.db.map.find_one()
                    return json.dumps({'status':'ok', 'name':map['name'], 'width':int(map['width']), 'height':int(map['height'])})
            return json.dumps({'status':'error'})
        elif message['method']=='move':
            if 'token' in message and 'x' in message and 'y' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    user = self.srv.db.user.find_one({'username':active_user['username']})
                    map = self.srv.db.map.find_one()
                    if (int(message['x']) != user['x'] or int(message['y']) != user['y']) and int(message['x']) >= 0 and int(message['y']) >= 0\
                    and message['x'] <= map['width'] and message['y'] <= map['height']:
                        self.srv.db.user.update_one({'username':active_user['username']},{'$set':{'x':message['x'],  'y':message['y'],  'field':True}})
                        import calendar
                        return json.dumps({'status':'ok', 'time':calendar.timegm(time.gmtime())+10})
                    elif message['x'] == user['x'] and message['y'] == user['y']:
                        return json.dumps({'status':'fail', 'description':'Yer character is not moving'})
                    else:
                        return json.dumps({'status':'fail', 'description':'Out of bounds'})
            return json.dumps ({'status':'error'})
        elif message['method']=='field':
            if 'token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    user = self.srv.db.user.find_one({'username':active_user['username']})
                    user_field = user['field']
                    inventory = user['inventory']
                    item = int(self.srv.db.map.find_one()['map'][user['x']][user['y']])
                    if user_field:
                        inventory[item] += 1
                        self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'field':False, 'inventory':inventory}})
                        return json.dumps({'status':'ok', 'item':int(item)})
                    else:
                        return json.dumps({'status':'fail', 'description':'Item already taken in ('+str(user['x'])+','+str(user['y'])+')'})
            return json.dumps({'status':'error'})
        elif message['method']=='offer':
            if 'token' in message and 'offered_item' in message and 'n1' in message and 'demanded_item' in message and 'n2' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    offered_item = int(message['offered_item'])
                    inventory = self.srv.db.user.find_one({'username':active_user['username']})['inventory']
                    if int(message['n1']) <= inventory[offered_item]:
                        inventory[offered_item] -= int(message['n1'])
                        self.srv.db.tradebox.insert({'token':genrandomhash(), 'offered_item':message['offered_item'], \
                        'n1':message['n1'], 'demanded_item':message['demanded_item'],  'n2':message['n2'], 'availability':'true', \
                        'username':active_user['username']})
                        self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'inventory':inventory}})
                        return json.dumps({'status':'ok'})
                    else:
                        return json.dumps({'status':'fail', 'description':'Insufficient offer'})
            return json.dumps({'status':'error'})
        elif message['method']=='tradebox':
            if 'token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    offers = []
                    list = self.srv.db.tradebox.find({'username':active_user['username']})
                    for offer in list:
                        offers.append([int(offer['offered_item']), int(offer['n1']), int(offer['demanded_item']), int(offer['n2']), offer['availability'], offer['token']])
                    return json.dumps({'status':'ok', 'offers':offers})
            return json.dumps({'status':'error'})
        elif message['method']=='sendfind':
            if 'token' in message and 'item' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    offers = []
                    list = self.srv.db.tradebox.find({'offered_item':message['item'],  'username':{'$ne':active_user['username']},  'availability':'true'})
                    for offer in list:
                        offers.append([int(offer['offered_item']), int(offer['n1']), int(offer['demanded_item']), int(offer['n2']), offer['availability'], offer['token']])

                    cachejsonlist = self.srv.getcacheitem(int(message['item']))
                    for offer in cachejsonlist:
                        offers.append([int(offer['offered_item']), int(offer['n1']), int(offer['demanded_item']), int(offer['n2']), offer['availability'], offer['token']])

                    return json.dumps({'status':'ok', 'offers':offers})
            return json.dumps({'status':'error'})
        elif message['method']=='findoffer':
            if 'item' in message:
                offers = []
                list = self.srv.db.tradebox.find({'offered_item':message['item']})
                for offer in list:
                    offers.append([offer['offered_item'], offer['n1'], offer['demanded_item'], offer['n2'], offer['availability'], offer['token']])
                return json.dumps({'status':'ok', 'offers':offers})
            return json.dumps({'status':'error'})
        elif message['method']=='sendaccept':
            if 'token' in message and 'offer_token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    inventory = self.srv.db.user.find_one({'username':active_user['username']})['inventory']
                    offer = self.srv.db.tradebox.find_one({'token':message['offer_token']})

                    # maybe in trade box
                    if offer != None:
                        if int(offer['n2']) > inventory[offer['demanded_item']]:
                            return json.dumps({'status':'fail', 'description':'Insufficient demand'})
                        # update tradebox
                        self.srv.db.tradebox.update_one({'token':message['offer_token']},\
                        {'$set':{'availability':'false'}})
                        # update inventory
                        inventory[offer['demanded_item']] -= int(offer['n2'])
                        inventory[offer['offered_item']] += int(offer['n1'])
                        self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'inventory':inventory}})
                        return json.dumps({'status':'ok'})
                    # maybe in item cache
                    offer = srv.searchcachetoken(message['offer_token'])
                    if offer != None:
                        if int(offer['n2']) > inventory[offer['demanded_item']]:
                            return json.dumps({'status':'fail', 'description':'Insufficient demand'})

                        # send accept offer to corresponding server
                        sock = socket.socket()
                        sock.connect((offer['ip'], offer['port']))
                        sock.settimeout(10)
                        jstring = '{"method":"accept", "offer_token":'+message['offer_token']+'}'
                        sock.send(bytes(jstring,'utf-8'))
                        data = ''
                        while not is_json(data):
                            try:
                                chunk = self.sock.recv(4096).decode('utf-8')
                                data += chunk
                            except socket.timeout:
                                break
                        sock.close()
                        if debug:
                            print(data)
                        if is_json(data):
                            #delete offer from cache
                            srv.removecachetoken(message['offer_token'])
                            decoded = json.loads(data)
                            status = decoded['status']
                            if status == "ok":
                                # update inventory
                                inventory[offer['demanded_item']] -= int(offer['n2'])
                                inventory[offer['offered_item']] += int(offer['n1'])
                                self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'inventory':inventory}})
                                return json.dumps({'status':'ok'})
                            else:
                                return json.dumps({'status':'fail', 'description':'Offer unavailable in corresponding server'})
                        else:
                            return json.dumps({'status':'fail', 'description':'Server corresponding to offer fail to respond'})
                    # nowhere found!
                    return json.dumps({'status':'fail', 'description':'Offer not found'})
                else:
                    return json.dumps({'status':'fail', 'description':'User not exist'})
            return json.dumps({'status':'error'})
        elif message['method']=='accept':
            if 'offer_token' in message:
                offer = self.srv.db.tradebox.find_one({'token':message['offer_token']})
                if offer != None:
                    if offer['availability']=='true' :
                        self.srv.db.tradebox.update_one({'token':message['offer_token']},\
                                                   {'$set':{'availability':'false'}})
                        return json.dumps({'status':'ok'})
                    else:
                        return json.dumps({'status':'fail', 'description':'Offer not available'})
                else:
                    return json.dumps({'status':'fail', 'description':'Offer not exist'})
            return json.dumps({'status':'error'})
        elif message['method']=='fetchitem':
            if 'token' in message and 'offer_token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    inventory = self.srv.db.user.find_one({'username':active_user['username']})['inventory']
                    offer = self.srv.db.tradebox.find_one({'token':message['offer_token']})
                    if offer != None:
                        if offer['availability'] != 'true':
                            self.srv.db.tradebox.update_one({'token':message['offer_token']},\
                                                       {'$set':{'availability':'false'}})
                            inventory[int(offer['demanded_item'])] += offer['n2']
                            self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'inventory':inventory}})
                            self.srv.db.tradebox.delete_one({'token':message['offer_token']})
                            return json.dumps({'status':'ok'})
                        else:
                            return json.dumps({'status':'fail', 'description':'Offer still available'})
                    else:
                        return json.dumps({'status':'fail', 'description':'Offer not exist'})
                else:
                    return json.dumps({'status':'fail', 'description':'User not exist'})
            return json.dumps({'status':'error'})
        elif message['method']=='canceloffer':
            if 'token' in message and 'offer_token' in message:
                active_user = self.srv.db.active_user.find_one({'token':message['token']})
                if active_user != None:
                    inventory = self.srv.db.user.find_one({'username':active_user['username']})['inventory']
                    offer = self.srv.db.tradebox.find_one({'token':message['offer_token']})
                    if offer != None:
                        if offer['availability'] == 'true':
                            self.srv.db.tradebox.delete_one({'token':message['offer_token']})
                            inventory[int(offer['offered_item'])] += offer['n1']
                            self.srv.db.user.update_one({'username':active_user['username']}, {'$set':{'inventory':inventory}})
                            return json.dumps({'status':'ok'})
                        else:
                            return json.dumps({'status':'fail', 'description':'Offer already finished'})
                    else:
                        return json.dumps({'status':'fail', 'description':'Offer not exist'})
                else:
                    return json.dumps({'status':'fail', 'description':'User not exist'})
            return json.dumps({'status':'error'})
        elif message['method']=='join':
            if 'ip' in message and 'port' in message:
                return json.dumps({'status':'ok','value':self.srv.servers})
            return json.dumps({'status':'error'})
        elif message['method']=='backup':
            if 'server' in message:
                try:
                    if self.srv.backup_ip == '' or (self.srv.backup_ip == message['server'][-1]['ip'] and\
                    self.srv.backup_port == message['server'][-1]['port']):
                        self.srv.downer = message['server']
                        upper = self.srv.upper + [{'ip':self.srv.ip,'port':self.srv.port}]
                        return json.dumps({'status':'ok','server':upper})
                    else:
                        return json.dumps({'status':'fail','description':'Backup server still running'})
                except ValueError:
                    self.srv.backup_ip = ''
                    self.srv.backup_port = 0
                    return json.dumps({'status':'fail','description':'Invalid ip/port'})
            return json.dumps({'status':'error'})
        else:
            return '{"status":"error"}'

    def run(self):
        try:
            print('Server to client thread running')
            print(self.sock)
            self.sock.settimeout(60)
            data = ''
            while not is_json(data):
                chunk = self.sock.recv(4096).decode('utf-8')
                data += chunk
            try:
                message = json.loads(data)
                if debug:
                    print(message)
            except ValueError:
                response = '{"status":"error"}'
            if 'method' in message:
                response = self.process(message)
            else:
                response = '{"status":"error"}'
            if debug:
                print(response)
            self.sock.send(bytes(response,'utf-8'))
        finally:
            self.sock.close()

class SpfSendFindThread(Thread):
    ip = 'String'
    port = 'Number'
    item = 'List'
    data = None
    def __init__(self,ip,port,item):
        Thread.__init__(self)
        self.sock = socket.socket()
        self.ip = ip
        self.port = port
        self.item = item
        self.sock.settimeout(3)
        self.data = None

    def run(self):
        try:
            self.sock.connect((self.ip,self.port))
            jstring = '{"method":"findoffer", "item":'+str(self.item)+'}'
            if debug:
                print(jstring)
            self.sock.send(bytes(jstring,'utf-8'))
            self.data = recievejson(self.sock)
            if debug and self.data != None:
                print(self.data)
        finally:
            self.sock.close()

class ServerThread (Thread):
    def __init__(self,addr,port,backup,db):
        Thread.__init__(self)
        self.srv = SpfServer(addr,port,backup,db)
    def run (self):
        self.srv.start()

def arghandler(argv):
    i = 2
    param = ['-db','-backup','-addr']
    while i < len(argv):
        if(argv[i]) == param[0]:
            global db
            i += 1
            if i < len(argv) and argv[i] not in param:
                db = argv[i]
                i += 1
        elif(argv[i]) == param[1]:
            global backup
            backup['value'] = True
            i += 1
            if i < len(argv) and argv[i] not in param:
                backup['ip']  = argv[i]
                i += 1
            if i < len(argv) and argv[i] not in param:
                backup['port']  = int(argv[i])
                i += 1
            else:
                backup['ip']  = ''
        elif(argv[i]) == param[2]:
            global addr
            i += 1
            if i < len(argv) and argv[i] not in param:
                addr = argv[i]
                i += 1

if __name__ == "__main__":
    db = 'spf'
    backup = {'value':False,'ip':'','port':0}
    addr = getipaddress()
    import sys
    print("Arguments given: ",sys.argv)
    if len(sys.argv) < 2:
        print('Missing argument for port')
        exit()
    try:
        port = int(sys.argv[1])
    except ValueError:
        print('Invalid port')
        exit()
    arghandler(sys.argv)
    thr = ServerThread(addr,port,backup,db)
    thr.daemon = True
    thr.start()
    if debug:
        print('Debugging mode')
    while True:
        try:
            time.sleep(1)
        except (KeyboardInterrupt, SystemExit):
            break
