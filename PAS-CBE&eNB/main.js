/*KPAS - CBE*/
var http = require('http');
var fs = require('fs');
var url = require('url');
var qs = require('querystring');
var template = require('./syntax/template.js');
var warningList = require('./WarningList.js');

//var list = require('./List.js');
//서버 포트:2500
var receiver = function (port){
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
    console.log(`메세지도착::>address:${rinfo.address},port:${rinfo.port}\ncontext:${msg}`  );
    this.message = msg;

    //수신 메시지 파싱
    var obj = JSON.parse(this.message);
    var warningAreaCoordinate = obj.warningAreaCoordinate;
    var messageidentifier = obj.messageidentifier;
    var serialNumber = obj.serialNumber;

    var num = warningList.findWarningAreaCoordinate(warningAreaCoordinate);
    warningList.addConfirm(num,serialNumber,messageidentifier);
    //console.log(`num:${num}>>${warningList.get(num).messageidentifier}or${warningList.getMessageidentifier(num)} `);
  });


  //모든 응답을 받으면 소켓 닫음
  socket.on('close',function(){
    console.log('close event');
  });

  this.getMSG = function(){
    this.mesaage;
  }
}


var app = http.createServer(function(request,response){
    var _url = request.url;
    var queryData = url.parse(_url, true).query;
    var pathname = url.parse(_url, true).pathname;
    console.log(pathname);
    if(pathname === '/'){
      if(queryData.id === undefined){
        fs.readdir('./data', function(error,filelist){
          var title = 'PWS-CBE';
          var description = 'Select the function of PWS-CBE';
          var html = template.html(title,`<h2>${title}</h2>${description}`);
          response.writeHead(200);
          response.end(html);
        });
      }
    }else if(pathname === '/Warning'){
      fs.readdir('./data', function(error,filelist){
        fs.readFile(`data/${queryData.id}`, 'utf8', function (err,description){
          var title = 'Warning';
          var html = template.html(title,template.warning());
          response.writeHead(200);
          response.end(html);
        });
      });
    }else if(pathname === '/Shelter'){
      fs.readdir('./data', function(error,filelist){
        fs.readFile(`data/${queryData.id}`, 'utf8', function (err,description){
          var title = 'Shelter';
          var html = template.html(title,template.shelter(warningList));
          response.writeHead(200);
          response.end(html);
        });
      });
    }else if(pathname === '/Shelter/seletWarning'){
      fs.readdir('./data', function(error,filelist){
        fs.readFile(`data/${queryData.id}`, 'utf8', function (err,description){
          var title = 'Warning';
          var html = template.html(title,template.selectWarning(warningList));
          response.writeHead(200);
          response.end(html);
        });
      });
    }else if(pathname === '/Warning_process'){  //1.Emergency Broadcast Request
      var body= '';

      request.on(`data`,function(data){
          body = body + data;

      });

      request.on(`end`,function(data){
        //입력받은 내용을 쿼리
        var parseData = qs.parse(body);
        //문자 내용 객체 생성
        var data = new Object();

        //객체의 내용들
        data.messageType = "Emergency_Broadcast_Request"
        data.source = parseData.Source;
        data.calamity = parseData.Calamity;
        data.impactArea = parseData.WaringArea;
        data.warningAreaCoordinate = parseData.WaringAreaCoordinate;
        data.context = parseData.Context;


        //문자 내용 객체를 json형태로 바꿈
        var jsonData = JSON.stringify(data);
        console.log('jsonData:'+jsonData);
        //보낼 버퍼 길이 계산
        var lengths = jsonData.length;
        //버퍼 길이 할당
        var buffer = Buffer.alloc(lengths);
        //버퍼생성 및 내용삽입
        buffer = new Buffer.from(jsonData);
        //리스트에 추가
        warningList.append(data);
        //console.log(`버퍼를 내용:${warningList.get(warningList.find(data)).context}`);
        //console.log(`길이:${warningList.length()}`);
        var sender = require('./sender.js');
        sender.BroadcastRequest(Buffer.from(buffer),5000,'39.127.82.93');
      });
      response.writeHead(200);
      response.end(`Warning success`);
    }else if(pathname === '/Shelter_process'){//10.Shelter Broadcast Request
      var body= '';
      request.on(`data`,function(data){

          body = body + data;
      });

      request.on(`end`,function(data){
        console.log('data:'+body);
        //입력받은 내용을 쿼리
        var parseData = qs.parse(body);
        //문자 내용 객체 생성
        var data = new Object();
        if(parseData.warning_select == "warning_select=-1"){//아무것도 선택안한 경우

        }else{
        //이전에 보낸 메시지정보 가져오기


        //console.log(`${parseData.warning_select}`);
        var num = parseData.warning_select
        console.log(`num:${num}>>${warningList.get(num).messageidentifier}`);
        //객체의 내용들
        data.messageType = "Shelter_Broadcast_Request";
        data.messageidentifier = warningList.getMessageidentifier(num);
        data.serialNumber = warningList.getSerialNumber(num);
        data.infoType = "shelter";
        data.context = parseData.Context;
        console.log(`${data.messageidentifier},${data.serialNumber}`);
        var shelterList = new Array();

        var i;
        //for(i = 0 ; i < 4 < i++){
          var shelter = new Object();
          shelter.num = 0;
          shelter.name = parseData.shelter_name0;
          shelter.shelterCoordinate = parseData.shelterCoordinate0;
          shelterList.push(shelter);
          var shelter = new Object();
          shelter.num = 1;
          shelter.name = parseData.shelter_name1;
          shelter.shelterCoordinate = parseData.shelterCoordinate1;
          shelterList.push(shelter);
          var shelter = new Object();
          shelter.num = 2;
          shelter.name = parseData.shelter_name2;
          shelter.shelterCoordinate = parseData.shelterCoordinate2;
          shelterList.push(shelter);
          var shelter = new Object();
          shelter.num = 3;
          shelter.name = parseData.shelter_name3;
          shelter.shelterCoordinate = parseData.shelterCoordinate3;
          shelterList.push(shelter);
        //}

        data.shelterList = shelterList;

        //문자 내용 객체를 json형태로 바꿈
        var jsonData = JSON.stringify(data);
        //보낼 버퍼 길이 계산
        var lengths = jsonData.length;
        //버퍼 길이 할당
        var buffer = Buffer.alloc(lengths);
        //버퍼생성 및 내용삽입
        buffer = new Buffer.from(jsonData);
        var sender = require('./sender.js');
        sender.BroadcastRequest(Buffer.from(buffer),5000,'39.127.82.93');
      }
      });
      response.writeHead(200);
      response.end(`Shelter success`);
    }else if(pathname === '/Shelter/seletWarning_process'){
      var body= '';
      request.on(`data`,function(data){
          console.log('data:'+data);
          //body에서 선택한 Warning의 정보 가져옴 때옴

          if(data == "warning_select=-1"){//아무것도 선택안한 경우
            var html = template.html(template.alertError("재난을 선택해주세요!"));
            response.writeHead(200);
            response.end(html);
          }else{//선택한경우
            //warningList의 선택한 재난 정보 불러오기 및 설정
            //warningLsit=num [0]=warningLsit [1]=num
            var warningNum = data.split('=');
            warningList.get(warningNum);
            //
            var title = 'Shelter';
            var html = template.html(title,template.shelter());
            response.writeHead(200);
            response.end(html);
          }

      });
    }else {
      response.writeHead(404);
      response.end('Not found');
    }
});

//웹페이지 포트 :3000
app.listen(3000);

receiver(3000);
