var template = {
  html:function(title,body){
    return `
    <!doctype html>
    <html>
    <head>
      <title>PWS-CBE:${title}</title>
      <meta charset="utf-8" content="PWS-CBE">
    </head>
    <body>
      <h1><a href="/">PWS-CBE</a></h1>
      ${body}
      <a href="/Warning">Warning</a>
      <a href="/Shelter">Shelter</a>
    </body>
    </html>
    `},list:function(filelist){
      var list = `<ul>`;
      var i = 0;
      while( i < filelist.length){
        list = list + `<li><a href="/?id=${filelist[i]}">${filelist[i]}</a></li>`;
        i++;
      }
      list = list +`<ul>`;
      return list;
    },warning:function(){
      return`
      <form action="http://localhost:3000/Warning_process" method="post">
      <p><input type="text" name="Source" placeholder="발송처"></p>
      <p><input type="text" name="Calamity" placeholder="재난 종류"></p>
      <p><input type="text" name="WaringArea" placeholder="재난 발생지"></p>
      <p><input type="text" name="WaringAreaCoordinate" placeholder="(위도,경도)"></p>
      <p><textarea name ="Context" placeholder="문자 내용"></textarea></p>
        <p><input type="submit"></p>
      </form>
      `;
    },shelter:function(warningList){
      return`
      <form action="http://localhost:3000/Shelter_process" method="post">
      <title>Shelter</title>
      ${this.selectWarning(warningList)}
      <p><input type="text" name="numberOfShelter" placeholder="쉘터 수"></p>
      ${this.printInputShelters(4)}
      <p><textarea name ="context" placeholder="대피내용"></textarea></p>
      <p><input type="submit"></p>
      </form>
      `;
    },selectWarning:function(warningList){
      return`
      <form action="http://localhost:3000/Shelter/seletWarning_process" method="post">
      <title>SelectWarning</title>
      <meta charset="utf-8" content="PWS-CBE">
      <body>
        <select name = "warning_select" id = "warning_select" onchange = "select_value">
        <option value="-1">===재난선택===</option>
        ${this.printWarnings(warningList)}
      </body>
      <script>
       var select_value = function (select_obj){

       };
   </script>
      `;
    },printWarnings:function(warningList){
      var i = 0;
      var optionList ='';
      for(i ; i < warningList.length() ; i++){
        optionList += `<option value = ${i}>${warningList.getInfo(i)}</option>`
      }
      return optionList;
    },printInputShelters:function(num){
      var i = 0;
      var inputShelterList ='';
      for(i ; i < num ; i++){
        inputShelterList += `
        <p><input type="text" name="shelter_name${i}" placeholder="쉘터[${i}]이름"></p>
        <p><input type="text" name="shelterCoordinate${i}" placeholder="쉘터[${i}]좌표"></p>
        `
      }
      return inputShelterList;
    },alertError:function(context){
      return`
      <form action="http://localhost:3000/Shelter/seletWarning" method="post">
      <script language="javascript">
        function alerttest(){
          alert("${context}");
        }
        </script>
      `
    }
  };

module.exports = template;
