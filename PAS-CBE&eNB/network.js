var net = require('net');


//net.Server:소켓서버
//net.Socket:소켓

var socket = dgram.createSocket('udp4');
socket.bind(3100);

var message = new Buffer('Hello');

//socket.send(메시지,0,메시지길이,포트번호,주소,콜백);
//멀티캐스트 socket.addMembership
