package com.aaron.dibujo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Main extends ActionBarActivity implements ColorPickerDialog.OnColorChangedListener {

    private final int SELECT_PICTURE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        iniciarCanvas();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                    Vista.mapaDeBits=bitmap;
                    iniciarCanvas();

                    Vista.lienzoFondo.drawBitmap(bitmap, 0, 0,Vista.pincel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bitmap bm= Vista.mapaDeBits;
        outState.putParcelable("fondo", bm);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bitmap bm=savedInstanceState.getParcelable("fondo");
        Vista.mapaDeBits=bm;
    }

    private void iniciarCanvas(){
        LinearLayout ll=(LinearLayout)findViewById(R.id.llCanvas);
        Vista canvas=new Vista(this);
        ll.removeAllViews();
        ll.addView(canvas);
    }

    public void dedos(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.dedos);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dedos, null);
        alert.setView(vista);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Vista.pincel.setXfermode(null);
                RadioGroup rg = (RadioGroup) vista.findViewById(R.id.rgDedos);
                int rbID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(rbID);
                int idx = rg.indexOfChild(radioButton);
                Vista.dedoMax=idx;
            }
        });
        alert.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tostada(R.string.mensajeCancelar);
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void relleno(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.relleno);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.relleno, null);
        alert.setView(vista);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                desactivarGoma();
                RadioGroup rg = (RadioGroup) vista.findViewById(R.id.rgRelleno);
                int rbID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(rbID);
                int idx = rg.indexOfChild(radioButton);
                if(idx==0){
                    Vista.pincel.setStyle(Paint.Style.STROKE);
                    Vista.trama=false;
                }else if(idx==1){
                    Vista.pincel.setStyle(Paint.Style.FILL);
                    Vista.trama=false;
                }else{
                    Vista.pincel.setStyle(Paint.Style.STROKE);
                    Vista.trama=true;
                }
            }
        });
        alert.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tostada(R.string.mensajeCancelar);
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void grosor(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.grosor);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.grosor, null);
        alert.setView(vista);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText et = (EditText) vista.findViewById(R.id.etGrosor);
                if(et.getText().toString().isEmpty()){
                    tostada(R.string.mensajeCancelar);
                }else{
                    Vista.grosor(Integer.parseInt(et.getText().toString()));
                }
            }
        });
        alert.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tostada(R.string.mensajeCancelar);
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void formas(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.formas);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.formas, null);
        alert.setView(vista);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                desactivarGoma();
                RadioGroup rg = (RadioGroup) vista.findViewById(R.id.rg);
                int rbID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(rbID);
                int idx = rg.indexOfChild(radioButton);
                Vista.metodo=idx;
            }
        });
        alert.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tostada(R.string.mensajeCancelar);
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void nuevo(View v){
        Vista.mapaDeBits=null;
        iniciarCanvas();
    }

    public void salvar(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.guardar);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.salvar, null);
        alert.setView(vista);
        alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText nombreA = (EditText) vista.findViewById(R.id.etFile);
                String nombre = nombreA.getText().toString();
                if (!nombre.isEmpty()) {
                    String fecha = DateFormat.format("dd-MM-yyyy-hh:mm:ss", new java.util.Date()).toString();
                    File f = new File(getExternalFilesDir(null), nombre + "_" + fecha + ".png");
                    try {
                        FileOutputStream fos = new FileOutputStream(f);
                        Vista.mapaDeBits.compress(Bitmap.CompressFormat.PNG, 80, fos);
                        fos.close();
                    } catch (FileNotFoundException e) {

                    } catch (IOException e) {
                    }
                }
            }
        });
        alert.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tostada(R.string.mensajeCancelar);
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void cargar(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select picture"), SELECT_PICTURE);
    }

    public void goma(View v){
        tostada(R.string.goma);
        Vista.pincel.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Vista.metodo=3;
        Vista.pincel.setStyle(Paint.Style.STROKE);
        Vista.pincel.setStrokeWidth(30);
    }

    public void desactivarGoma(){
        Vista.pincel.setXfermode(null);
        Vista.pincel.setStrokeWidth(5);
    }

    public void color(View v){
        new ColorPickerDialog(this, this, Vista.pincel.getColor()).show();
    }

    @Override
    public void colorChanged(int color) {
        Vista.pincel.setColor(color);
    }

    /* Mostramos un mensaje flotante a partir de un recurso string*/
    private void tostada(int s){
        Toast.makeText(this, getText(s), Toast.LENGTH_SHORT).show();
    }

}
