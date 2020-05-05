package com.example.pas_ue;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

public class TCPServer extends AsyncTask<DatagramPacket, Boolean, Void> {

    Socket socket;
    private String TAG = "TCPServer";

    private String eNBsdomain = "0.tcp.ngrok.io";
    private InetAddress address[];
    private String eNBsPort = "15987";
    private  InputStream inputStream;
    public OutputStream outputStream;

    public MessageList messageList;
    private boolean signaling;

    public TCPServer(MessageList messageList){
        this.messageList = messageList;
        this.signaling = false;
    }

    @Override//준비(시그널링)
    protected void onPreExecute() {

        //doInBackground의 실행
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(DatagramPacket... datagramPackets) {
        Log.d("Tag","connection");
        connection();
        Log.d("Tag","connection Success");
        this.inputStream = null;

        try {
            InetAddress host = InetAddress.getByName(getLocalIpAddress());
            Log.d("Tag","host"+host );
            signaling();
            this.inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"-----wait------");

        String msg ="";
        Message message;
        Vector<Message> vector;
        while (msg.equals("")&& !msg.equals("end")){
            msg = receive();
            Log.d(TAG,"receive msg!!!: "+ msg);
            //수신 받은 메세지 해독
            message = messageDecoding(msg,this.signaling);
            //시그널링문자
            if(message == null){
                this.signaling = true;
            }
            //디코딩이 가능한 문자메시지
            else {
                //수신 받은 메세지 클래스 벡터 찾기
                vector = this.messageList.checkClass(message.getMessageidentifier());
                //수신 메세지 해독 정보에 따른 실행
                this.messageList.findMessage(vector, message);
            }


            //초기화
            msg ="";
            message = new Message();
            vector = new Vector<Message>();
        }


        Log.d(TAG,"receive end!!!: ");
        close();


        return null;
    }

    public void connection(){
        try {
            this.address = InetAddress.getAllByName(this.eNBsdomain);
            Log.d(TAG,this.eNBsdomain+">>>"+this.address[0]);
            this.socket = new Socket(this.address[0],Integer.parseInt(this.eNBsPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signaling() throws IOException {
       // try {
            Log.d(TAG, "--------------------------------------------------------------------------------");
            Log.d(TAG, "signaling");
            Log.d(TAG, "--------------------------------------------------------------------------------");

            //시그널링 json데이터 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageType","Signal");
            jsonObject.put("adrress",InetAddress.getByName(getLocalIpAddress()).toString());
            jsonObject.put("port",8000);
            //버퍼 생성
            byte buffer[] = jsonObject.toJSONString().getBytes();
            Log.d(TAG,"send Signal: "+this.address[0]+":" +buffer.toString());
            //eNB의 서버에게 보내는 시그널링 패킷생성
            send(buffer);

    }

    public String receive(){
        Log.d(TAG,"receive 중");
        try {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            bytes = this.inputStream.read(buffer);
            String msg = new String(buffer);
                return msg.trim();


        } catch (EOFException e){
            e.printStackTrace();
            Log.e(TAG,"EOFE");
        } catch (IOException e ) {
        e.printStackTrace();
        Log.e(TAG,"IOE");
        }
        Log.d(TAG,"receive 실패");
        return null;
    }

    public void send(byte[] buffer) throws IOException {
        this.outputStream = this.socket.getOutputStream();
        this.outputStream.write(buffer,0,buffer.length);
    }

    public void close(){
        try {
            this.outputStream.close();
            this.inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkMessageList(String msg){

    }

    public Message messageDecoding(String msg,Boolean signaling){
        Message message;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(msg);

            //시그널 됨
            if(signaling) {
                int messageIdentifier = (int)(long) jsonObject.get("messageidentifier");
                int serialNumber = (int)(long) jsonObject.get("serialNumber");
                String CB_Data = (String) jsonObject.get("warningContentMessage");
               int dataCodingScheme = (int)(long) jsonObject.get("dataCodingScheme");
                //int Warning_Area_Coordinates = (int)jsonObject.get("dataCodingScheme");
                message = new Message(messageIdentifier, serialNumber, CB_Data, dataCodingScheme);
                return message;
            }
            //시그널 안됨
            else{

            }
        }catch (ParseException e){
            e.printStackTrace();

        }
        return null;
    }

    public Boolean isSignal(JSONObject object){
        if(object.get("signal") == "true" ){
            return true;
        }
        return false;
    }
/*
    @Override
    protected void onProgressUpdate() {
        return true;
    }

    @Override//백그라운드 작업이 완료된 후 결과값
    protected void onPostExecute() {
        super.onPostExecute();
    }
*/


    // long 값을 int로 변환
    public static int safeLongToInt(long l) {
        int i = (int)l;
        if ((long)i != l) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return i;
    }

    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en =
                    NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()) {
                NetworkInterface interf = en.nextElement();
                Enumeration<InetAddress> ips = interf.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Testing", ex.toString());
        }
        return null;
    }

}
