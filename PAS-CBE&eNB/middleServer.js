/*KPAS - eNB - SERVER*/
var http = require('http');
var fs = require('fs');
var url = require('url');
var template = require('./syntax/template.js');
var qs = require('querystring');
var net = require('net');

var Udp2TcpMSG;
var Tcp2UdpMSG;
var useSocket;
var client;
//UE에게서 받는 문자들
var TCPserver = net.createServer(function(socket) {

  // connection event
  console.log('Client connection: ');
  console.log('   local = %s:%s', socket.localAddress, socket.localPort);
  console.log('   remote = %s:%s', socket.remoteAddress, socket.remotePort);
  client = socket;
  socket.setTimeout(500);
  socket.setEncoding('utf8');

    //수신 이벤트
    socket.on('data', function(chunck) {
      var msg = chunck.toString();
      console.log('UE가 보냄 : ',msg);

      var obj = JSON.parse(msg);
      var messageType = obj.messageType;
      console.log('messagetype : ',messageType);
      //시그널링
      //해당 서버는 UE에게 confirm내용과 eNB정보보냄
      //해당 서버는 eNB본체에게 UE정보보냄
      if(messageType == "Signal"){
        /*********************UE에게 전송*************************/

        var data = new Object();

        data.messageType = 'SignalConfrim';
        data.signalConfrim = 'true';
        //문자 내용 객체를 json형태로 바꿈
        var jsonData = JSON.stringify(data);
        console.log('jsonData:'+jsonData);
        //보낼 버퍼 길이 계산
        var lengths = jsonData.length;
        //버퍼 길이 할당
        var buffer = Buffer.alloc(lengths);
        //버퍼생성 및 내용삽입
        buffer = new Buffer.from(jsonData);
        console.log(`UE에게 전송:${jsonData}`);
        socket.write(buffer);
        //socket.write(chunck);
        /*********************eNB에게 전송*************************/
        Tcp2UdpMSG = chunck;
        console.log('eNB에게 전송');
        //될까???
        var sender = require('./sender.js');
        sender.BroadcastRequest(Tcp2UdpMSG,7000,'39.127.82.93');

      }
      //쉘터요청문자
      //eNB에게 전송
      else if(messageType == "ShelterReuquset"){
        Tcp2UdpMSG = chunck;

      }
      //쉘터확인문자
      //eNB에게 전송
      else if (messageType == "ShelterConfrim"){

      }else{

      }
    });
    //접속 종료 이벤트
    socket.on('end', function() {
      socket.write('end');
        console.log('클라이언트 접속 종료');
    });


});

function writeData(socket, data){
  var success = !socket.write(data);
  if (!success){
    (function(socket, data){
      socket.once('drain', function(){
        writeData(socket, data);
      });
    })(socket, data);
  }
}


//eNB에게서 받는 문자들
var UDPserver = function(port){
  var dgram = require('dgram');
  var socket = dgram.createSocket('udp4');
  var message
  socket.bind(port);

  //메세지 송출후 정상으로 전달됬는지 확인하기위해 기다림
  socket.on('listening',function(){
    console.log('listening event');
  });
    //응답 메세지 받음
    socket.on('message',function(msg,rinfo){
      console.log(`eNB로부터 UDP메세지도착::>address:${rinfo.address},port:${rinfo.port}\ncontext:${msg}`  );
      //udp =>> tcp
      Udp2TcpMSG = msg;
      //여기가 문제
      writeData(client, msg);
      //TCPserver.write(Udp2TcpMSG);
      console.log(`UDP메세지 TCP로 UE에게 전송함!`  );
    });

    //모든 응답을 받으면 소켓 닫음
    socket.on('close',function(){
      console.log('close event');
    });

    this.getMSG = function(){
      this.mesaage;
    }

}
//eNB본체와 연결
UDPserver(7500,function(){
  console.log('UdpServer listening');
});

//UE와 시그널링
TCPserver.listen(7500,function(){
  console.log('TcpServer listening for connections');
});
