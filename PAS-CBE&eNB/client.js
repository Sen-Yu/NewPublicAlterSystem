function Client(){
var net = require('net');
//host주소와 포트넘버
var ip = '39.127.82.93';
var port = 10002;

var socket = new net.Socket();
socket.connect({host:ip,port:port},function(){
  console.log('server 연결 성공');

  socket.write('Hello Socket Server\n');
  socket.end();

  socket.on('data',function(chunk){
    console.log('server is send:',chunk.toString());
  });



  //모든 행동이 끝나면 소켓 종료
  socket.on('end',function(){
    console.log('server connection is closed');
  });
});
}

module.exports.Client = Client;
