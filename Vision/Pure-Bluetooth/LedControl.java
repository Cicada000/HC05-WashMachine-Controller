package infoaryan.in.hc05_bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;

public class LedControl extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4, btn5, btnDis;
    String address = null;
    TextView lumn;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control2);


        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        btn1 =  findViewById(R.id.button2);
        btn2 =  findViewById(R.id.button3);
        //For additional actions to be performed
        btn3 =  findViewById(R.id.button5);
        btn4 =  findViewById(R.id.button6);
        btn5 =  findViewById(R.id.button7);
        btnDis = findViewById(R.id.button4);
        lumn =  findViewById(R.id.textView2);

        new LedControl.ConnectBT().execute();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("A40A0A00B8");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("A40B0B00BA");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("A201A3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("BA0517212B000508080037");
                sendSignal("A001020101010000000000000000000000000000000000000000000000000000");
                sendSignal("0000000000000000A6");
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("BA0517212B000508070036");
                sendSignal("BA0517212B000508070036");
                sendSignal("BA0517212B000508070036");
                sendSignal("A001020401010000000000000000000000000000000000");
                sendSignal("0000000000000000000000000000000000A9");
            }
        });


        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); // 断开连接的方法
                Toast.makeText(getApplicationContext(), "连接已断开", Toast.LENGTH_LONG).show(); // 显示Toast消息
            }
        });

    }

    private void sendSignal(String hexCommand) {
        if (btSocket != null) {
            try {
                // 将十六进制字符串转换为字节
                byte[] bytesToSend = hexStringToByteArray(hexCommand);
                btSocket.getOutputStream().write(bytesToSend);
            } catch (IOException e) {
                msg("发生错误");
            }
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("发生错误");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(LedControl.this, "连接中...", "请稍等片刻");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("连接失败，请退出软件重试或重启洗衣机");
                finish();
            } else {
                msg("设备已连接");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
}
