
var dgram = require('dgram');
var Sender = {

  socket:dgram.createSocket('udp4'),

//
//message객체를 스트링시켜서 버퍼에 넣는다

//1.Broadcast Request(CBC에게 전송)
//11.Broadcast Request(CBC에게 전송)
BroadcastRequest:function(msg,destport,destadress){
  var socket = this.socket;
  console.log(msg);
  //5000,'39.127.82.93'
  //
  socket.send(Buffer.from(msg),destport,destadress,function(err){
    if(err){
      console.error('UDP Message send errror.',err);
      return;
    }
    console.log('UDP Message send success.');
  });
  }




}

module.exports = Sender;
