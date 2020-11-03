package com.example.bluetoothuno;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="Tarea";
    private boolean ledOnOff=false;
    private TextView salida;
    public BluetoothAdapter adaptadorBluetooth;
    public final static int REQUEST_ENABLE_BT =1;
    private final static String MANDARLO="mandarlo hacia la otra";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adaptadorBluetooth= BluetoothAdapter.getDefaultAdapter();
        salida=findViewById(R.id.dispoSelec);
        String recivido=getIntent().getStringExtra(MANDARLO);
        if(recivido!=null) salida.setText("Conectar a: "+recivido);
    }
    public void iniciarBluetooth(View entraAqui){
        if (adaptadorBluetooth==null){
            Log.d(TAG,"Dispositivo NO compatible con Bluetooth");
        }
        else{
            if(!adaptadorBluetooth.isEnabled()){
                Intent intentaHabilitar=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //De acuerdo al resultado del usuario si presiona rechazar no iniciara
                //startActivityForResult(intentaHabilitar,RESULT_OK);
                startActivityForResult(intentaHabilitar, REQUEST_ENABLE_BT);
            }
            else{
                adaptadorBluetooth.disable();
                Toast.makeText(this,"Bluetooth Desconectado",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void ToggLed(View entrada){
        if(!ledOnOff){
            Toast.makeText(this,"Led Encendido",Toast.LENGTH_SHORT).show();
            ledOnOff=true;
        }
        else{
            Toast.makeText(this,"Led Apagado",Toast.LENGTH_SHORT).show();
            ledOnOff=false;
        }
    }
    public void Buscar(View entrada){
        if(adaptadorBluetooth.isEnabled()){
            Intent volver=new Intent(this,DispositivoSincro.class);
            startActivity(volver);
        }
        else{
            Toast.makeText(this,"Bluetooth NO esta activado",Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(this,"Ok",Toast.LENGTH_SHORT).show();
        }
        else finish();
    }//fin de onActivityResult
}
