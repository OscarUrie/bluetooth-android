package com.example.bluetoothuno;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class DispositivoSincro extends AppCompatActivity {
    private ListView listado1, listado2;
    private String[] dispositivosBlueN=new String[8];
    private String[] dispositivosBlueM=new String[8];
    private final static String MANDARLO="mandarlo hacia la otra";
    private int dispositivoFinal;
    private int llevalacuenta=0;
    public BluetoothAdapter miAdaptador;
    ArrayAdapter<String> adaptador1,adaptador2;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                dispositivosBlueM[llevalacuenta]=device.getName();
                ActualizaListaDos();
                llevalacuenta++;
                if(llevalacuenta==8) llevalacuenta=0;

                //nombre del Dispositivo = device.getName() y direccion MAC del Dispositivo = device.getAddress();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_sincro);

        listado1=findViewById(R.id.lista_disp_vinculados);
        listado2=findViewById(R.id.lista_disp_encontrados);
        LLenarStrings();
        adaptador1=new ArrayAdapter<>(this,R.layout.mi_diseno_lv,dispositivosBlueN);
        adaptador2=new ArrayAdapter<>(this,R.layout.mi_diseno_lv,dispositivosBlueM);
        listado1.setAdapter(adaptador1);
        listado2.setAdapter(adaptador2);
        listado1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dispositivoFinal=i;
                Regresar(false);
            }
        });
        listado2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dispositivoFinal=i;
                TerminaDeBuscar();
                Regresar(true);
            }
        });

    }
    protected void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }
    protected void onStop(){
        super.onStop();
        //necesario ponerle aqui si el usuario le da regresar con el boton no con cancelar
        TerminaDeBuscar();
        unregisterReceiver(receiver);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.botoncan,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.cancelador){
            TerminaDeBuscar();
            Intent siguiente=new Intent(this,MainActivity.class);
            siguiente.putExtra(MANDARLO,"No sleccionado");
            startActivity(siguiente);
        }
        return super.onOptionsItemSelected(item);
    }
    public void Mostrar(View entrada){
        Set<BluetoothDevice> dispositivosV;
        //ArrayAdapter<String> adaptado;
        int contador=0;
        try{
            //Es necesario asignarle el getDefaultAdapter() para que funcione el getDefaultAdapter()
            miAdaptador=BluetoothAdapter.getDefaultAdapter();
            dispositivosV = miAdaptador.getBondedDevices();
            if (dispositivosV.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : dispositivosV) {
                    if(contador==8) break;
                    dispositivosBlueN[contador]= device.getName();
                    //dispositivosBlueN[contador]= device.getAddress(); // MAC address
                    contador++;
                }
                //adaptado=new ArrayAdapter<>(this,R.layout.mi_diseno_lv,dispositivosBlueN);
                //listado1.setAdapter(adaptado);
                adaptador1.notifyDataSetChanged();
                listado1.invalidateViews();
                listado1.refreshDrawableState();
                //listado1.deferNotifyDataSetChanged();
            }
            else Toast.makeText(this,"No hay Dispositivos vinculados",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this,"Ups algo salio mal",Toast.LENGTH_SHORT).show();
        }
    }
    public void BuscarDisp(View entrada){
        miAdaptador=BluetoothAdapter.getDefaultAdapter();
        if(miAdaptador.isDiscovering()) Toast.makeText(this,"Estoy Buscando",Toast.LENGTH_SHORT).show();
        else miAdaptador.startDiscovery();
    }
    private void LLenarStrings(){
        for (int i=0;i<8;i++){
            dispositivosBlueM[i]="...";
            dispositivosBlueN[i]="...";
        }
    }
    private void ActualizaListaDos(){
        adaptador2.notifyDataSetChanged();
        listado2.invalidateViews();
        listado2.refreshDrawableState();
    }
    private void TerminaDeBuscar(){
        miAdaptador=BluetoothAdapter.getDefaultAdapter();
        if(miAdaptador.isDiscovering()) miAdaptador.cancelDiscovery();
    }
    private void Regresar(boolean tipo){
        TerminaDeBuscar();
        Intent siguiente=new Intent(this,MainActivity.class);
        if(tipo) siguiente.putExtra(MANDARLO,dispositivosBlueM[dispositivoFinal]);
        else siguiente.putExtra(MANDARLO,dispositivosBlueN[dispositivoFinal]);
        startActivity(siguiente);
    }
}
