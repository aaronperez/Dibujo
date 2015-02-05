package com.aaron.dibujo;

/**
 * Created by Aaron on 03/02/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Vista extends View {

    //Pincel
    public static Paint pincel;
    //Lienzo
    public static Bitmap mapaDeBits;
    public static Canvas lienzoFondo;
    //Variables de control
    public static int metodo=3;
    public static int dedoMax=2;
    public static boolean trama=false;
    private float x0=-1,y0=-1,xi=-1,yi=-1;
    //circulos
    private float radio;
    //rectas
    private Path rectaPoligonal = new Path();
    //rectangulo
    private Rect rectangulo=new Rect(-1,-1,-1,-1);
    //MultiTouch
    private Path[] rutaPath=new Path[3];
    private Coordenadas[] coordenadaPath = new Coordenadas[3];


    public Vista(Context contexto) {
        super(contexto);
        pincel = new Paint();
        pincel.setColor(Color.WHITE);
        pincel.setAntiAlias(true);
        pincel.setStrokeWidth(1);
        pincel.setStyle(Paint.Style.STROKE);
        for(int i=0;i<3;i++){
            rutaPath[i]= new Path();
            coordenadaPath[i]=new Coordenadas(-1,-1,-1,-1);
        }
    }
    @Override
    protected void onDraw(Canvas lienzo) {
        super.onDraw(lienzo);
        switch (metodo){
            case 0:
                lienzo.drawLine(x0, y0, xi, yi, pincel);
                break;
            case 1:
                for(Path ruta: rutaPath){
                    lienzo.drawPath(ruta, pincel);
                }
                break;
            case 2:
                lienzo.drawCircle(x0,y0,radio,pincel);
                break;
            case 3:
                lienzo.drawPath(rectaPoligonal,pincel);
                break;
            case 4:
                lienzo.drawRect(rectangulo, pincel);
                break;
        }
        lienzo.drawBitmap(mapaDeBits, 0, 0, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mapaDeBits==null){
            mapaDeBits = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        else{
            mapaDeBits = Bitmap.createScaledBitmap(mapaDeBits,w,h,true);
        }
        lienzoFondo = new Canvas(mapaDeBits);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (metodo){
            case 0:
                //dibujar rectas
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0=x;
                        y0=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xi=x;
                        yi=y;
                        if(trama){
                            lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        }
                        invalidate();
                        break;//Sin break mola más
                    case MotionEvent.ACTION_UP:
                        xi=x;
                        yi=y;
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        //rectas.add(new Recta(x0,y0,xi,yi));
                        invalidate();
                        break;
                }
                break;
            case 1:
                //Para multiTouch
                int accion = event.getActionMasked();
                int numPuntos = event.getPointerCount();
                for (int i = 0; i < numPuntos; i++) {
                    int numPuntero = event.getPointerId(i);
                    x = event.getX(i);
                    y = event.getY(i);
                    if (numPuntero > dedoMax){
                        numPuntero=dedoMax;
                        continue;
                    }
                    switch (accion) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            rutaPath[numPuntero].reset();
                            coordenadaPath[numPuntero].x0 = coordenadaPath[numPuntero].xi = x;
                            coordenadaPath[numPuntero].y0 = coordenadaPath[numPuntero].yi = y;
                            rutaPath[numPuntero].moveTo(coordenadaPath[numPuntero].x0, coordenadaPath[numPuntero].y0);
                            invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            rutaPath[numPuntero].quadTo(coordenadaPath[numPuntero].xi, coordenadaPath[numPuntero].yi, (x + coordenadaPath[numPuntero].xi) / 2, (y + coordenadaPath[numPuntero].yi) / 2);
                            coordenadaPath[numPuntero].xi = x;
                            coordenadaPath[numPuntero].yi = y;
                            invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL:
                            lienzoFondo.drawPath(rutaPath[numPuntero], pincel);
                            invalidate();
                            break;
                    }
                }
                break;
            case 2:
                //dibujar circulos
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0=x;
                        y0=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xi=x;
                        yi=y;
                        radio=(float)Math.sqrt(Math.pow(x0-xi,2)+Math.pow(y0-yi,2));
                        if(trama){
                            lienzoFondo.drawCircle(x0, y0, radio, pincel);
                        }
                        invalidate();
                        break;//Sin break mola más
                    case MotionEvent.ACTION_UP:
                        xi=x;
                        yi=y;
                        radio=(float)Math.sqrt(Math.pow(x0-xi,2)+Math.pow(y0-yi,2));
                        lienzoFondo.drawCircle(x0, y0, radio, pincel);
                        invalidate();
                        radio=0;
                        x0=y0=xi=yi=-1;
                        break;
                }
                break;
            case 3:
                //Dibujar linea poligonal mediante PATH
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0= xi = x;
                        y0= yi = y;
                        rectaPoligonal.reset();
                        rectaPoligonal.moveTo(x0,y0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rectaPoligonal.quadTo((x + xi) / 2,(y + yi) / 2,x,y);
                        xi=x;
                        yi=y;
                        if(trama){
                            lienzoFondo.drawPath(rectaPoligonal,pincel);
                        }
                        invalidate();
                        break;//Sin break mola más
                    case MotionEvent.ACTION_UP:
                        xi=x;
                        yi=y;
                        lienzoFondo.drawPath(rectaPoligonal,pincel);
                        invalidate();
                        x0=y0=xi=yi=-1;
                        break;
                }
                break;
            case 4:
                //Dibujar rectangulo
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0=x;
                        y0=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xi=x;
                        yi=y;
                        if(x0<xi && y0<yi) {
                            rectangulo = new Rect((int) x0, (int) y0, (int) xi, (int) yi);
                        }else if(xi<x0 && y0<yi){
                            rectangulo = new Rect((int) xi, (int) y0, (int) x0, (int) yi);
                        }else if(x0<xi && yi<y0){
                            rectangulo = new Rect((int) x0, (int) yi, (int) xi, (int) y0);
                        }
                        else if(xi<x0 && yi<y0){
                            rectangulo = new Rect((int) xi, (int) yi, (int) x0, (int) y0);
                        }
                        if(trama){
                            lienzoFondo.drawRect(rectangulo, pincel);
                        }
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        xi=x;
                        yi=y;
                        rectangulo=new Rect((int)x0, (int)y0,(int) xi,(int) yi);
                        lienzoFondo.drawRect(rectangulo, pincel);
                        invalidate();
                        break;
                    }
                    break;
        }
        return true;
    }

    public static void grosor(int px){
        if(px>90)px=90;
        if(px<1)px=1;
        pincel.setStrokeWidth(px);
    }

    public class Coordenadas{
        public float x0,y0,xi,yi;

        public Coordenadas(float x0, float y0, float xi, float yi) {
            this.x0 = x0;
            this.y0 = y0;
            this.xi = xi;
            this.yi = yi;
        }
    }

}