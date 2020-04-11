var exports = module.exports = {};
var message;
exports.Receiver = function (port){
var dgram = require('dgram');
var socket = dgram.createSocket('udp4');
socket.bind(port);

//메세지 송출후 정상으로 전달됬는지 확인하기위해 기다림
socket.on('listening',function(){
  console.log('listening event');
});

//응답 메세지 받음
socket.on('message',function(msg,rinfo){
  console.log(`메세지도착::>address:${rinfo.address},port:${rinfo.port}\ncontext:${msg}`  );
  message = msg;
});


//모든 응답을 받으면 소켓 닫음
socket.on('close',function(){
  console.log('close event');
});

this.getMSG = function(){
  this.mesaage;
}
}

exports.receiveMSG = function(){
  return this.msg
}
//module.exports.Receiver = Receiver;
