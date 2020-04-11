function info(){

  this.source;
  this.calamity;
  this.impactArea;
  this.warningAreaCoordinate ;
  this.context ;

  this.serialNumber;
  this.messageidentifier;

  this.getPos = function(){
    return this.pos;
  };
  this.getData = new function(){
    return this.data;
  };
  this.setDate = new function(element){
    this.data = element
  };

}

function List(){
  this.dataStore = [];
  this.listSize = 0;

  this.append =function(element){
    this.dataStore[this.listSize] = element;
    this.listSize++;
  };

  this.findWarningAreaCoordinate = function(element){
    for(var i=0 ; i < this.listSize ; i++){
           if(this.dataStore[i].warningAreaCoordinate == element){
                 return i;
           }
       }
       return -1;
  };

  this.length = function(){
    return this.listSize;
  };

  this.getInfo = function(i){
      return `재난 종류:${this.dataStore[i].calamity}, 재난 좌표:${this.dataStore[i].warningAreaCoordinate}, 재난 발생지:${this.dataStore[i].impactArea}`;

  };

  this.getMessageidentifier = function(i){
    return this.dataStore[i].messageidentifier;
  };

  this.getSerialNumber = function(i){
    return this.dataStore[i].serialNumber;
  };

  this.clear = function(){
      this.dataStore = [];
      this.listSize = 0;
      this.pos = 0;
  };

  this.get = function(i){
    return this.dataStore[i];
  };
  this.addConfirm = function(i,serialNumber,messageidentifier){
    this.dataStore[i].serialNumber = serialNumber;
    this.dataStore[i].messageidentifier = messageidentifier;
  };
  this.getConfirm = function(i,element){
    return this.dataStore[i].confrim = element;
  };

}


var WarningList = new List();

module.exports = WarningList;
