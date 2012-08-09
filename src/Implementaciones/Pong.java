/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Implementaciones;


import java.awt.Color;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.SlickException;
import org.niktin.entrada.Raton;
import org.niktin.entrada.Teclado;
import org.niktin.juego.Juego;
import org.niktin.recursos.Fuente;
import org.niktin.recursos.Recurso;
import org.niktin.recursos.Sonido;
import org.niktin.recursos.Vacio;
import org.niktin.utilidades.Utilidades;

/**
 *
 * @author Daniel
 */
public class Pong {
    Recurso pelota,barraDer,barraIzq,pantallaTitulo, fondo, barraPuntaje,barraAbajo, btnJugar, pelotaTitle, btnCreditos, btnSalir, creditos, regresar;
    Juego juego;
    float velocidad= (float).5;
    float velocidadX=(float) velocidad;
    float velocidadY=(float) velocidad;
    float delta;
    long lastFPS;
    float tiempo = (float)1f/100f;
    Vacio vacio;
    public static Fuente ft;
    private int puntajeIzq=0;
    private int puntajeDer=0;
    private Fuente ftGanador;
    int movimientoTextoGana=0;
    int sentidoMovimientoTextoGana=0;
    private Fuente ft2,ftCreditos;
    boolean golpeo=false;
    int contadorRestaColoresBola=0;
        private long ultimoFrame;
   	/** frames per second */
    int fps;
	/** last fps time */
    long ultimoFPS;
    private float angulo=.1f;
    private int tamanoPelota;
    private float direccion=.1f;
    long ultimoClick=0;
    private int segundos;
    enum TransicionDeColor{rojo,verde,azul};

    TransicionDeColor transicion = TransicionDeColor.rojo;

    final int ticksPorSegundo = 100;
    final int saltoTicks = 1000 / ticksPorSegundo;
    final int maximoSalto = 2;

    int ticks = 100;

    long ultimoTiempo = Utilidades.obtenerTiempo();

    double siguienteTick = Utilidades.obtenerTiempo();
    int loops;
    
    enum Estado{titulo, jugando,nuevoJuego,saqueIzq, saqueDer, ganaDer, ganaIzq, creditos};

    Estado estado=Estado.titulo;


     private void mostrarTitulo() {
        juego.borrarRecursos();
        juego.agregarRecurso(pantallaTitulo, 0, 0);
        juego.agregarRecurso(btnJugar, Juego.obtenerAnchura()/2 - btnJugar.obtenerAnchura()/2, 240);
        juego.agregarRecurso(btnCreditos, Juego.obtenerAnchura()/2 - btnCreditos.obtenerAnchura()/2, 140);
        juego.agregarRecurso(btnSalir, Juego.obtenerAnchura()/2 - btnSalir.obtenerAnchura()/2, 40);
        juego.agregarRecurso(pelotaTitle, 287,393);

        pelotaTitle.rotarAgregado(Math.toRadians(.3), pelotaTitle.obtenerCentro().x, pelotaTitle.obtenerCentro().y);

        if(btnJugar.ratonSobreObjeto()){
            btnJugar.aplicarMascaraDeColor(btnJugar.obtenerMascaraColorRojo()-0.03f,btnJugar.obtenerMascaraColorVerde() , btnJugar.obtenerMascaraColorAzul()-0.05f);
        }else{
            btnJugar.aplicarMascaraDeColor(btnJugar.obtenerMascaraColorRojo()+0.03f,btnJugar.obtenerMascaraColorVerde() , btnJugar.obtenerMascaraColorAzul()+0.05f);
        }

         if(btnCreditos.ratonSobreObjeto()){
            btnCreditos.aplicarMascaraDeColor(btnCreditos.obtenerMascaraColorRojo()-0.03f,btnCreditos.obtenerMascaraColorVerde() , btnCreditos.obtenerMascaraColorAzul()-0.05f);
        }else{
            btnCreditos.aplicarMascaraDeColor(btnCreditos.obtenerMascaraColorRojo()+0.03f,btnCreditos.obtenerMascaraColorVerde() , btnCreditos.obtenerMascaraColorAzul()+0.05f);
        }

         if(btnSalir.ratonSobreObjeto()){
            btnSalir.aplicarMascaraDeColor(btnSalir.obtenerMascaraColorRojo()-0.03f,btnSalir.obtenerMascaraColorVerde() , btnSalir.obtenerMascaraColorAzul()-0.05f);
        }else{
                        btnSalir.aplicarMascaraDeColor(btnSalir.obtenerMascaraColorRojo()+0.03f,btnSalir.obtenerMascaraColorVerde() , btnSalir.obtenerMascaraColorAzul()+0.05f);
        }

        if(btnJugar.ratonSobreObjeto()&&Raton.botonPresionado(Raton.BOTON_IZQUIERDO)&&Raton.obtenerIntervalo()>100){
            estado = Estado.nuevoJuego;
        }

        if(btnCreditos.ratonSobreObjeto()&&Raton.botonPresionado(Raton.BOTON_IZQUIERDO)&&Raton.obtenerIntervalo()>100){
            estado = Estado.creditos;
        }

        if(btnSalir.ratonSobreObjeto()&&Raton.botonPresionado(Raton.BOTON_IZQUIERDO)&&Raton.obtenerIntervalo()>100){
            System.exit(0);
        }
        
    }

         private void actualizaPuntaje() {
        String puntajes = puntajeIzq + " - " + puntajeDer;
       juego.dibujarTexto(ft, puntajes,Juego.obtenerAnchura()/2 - (ft.obtenerAnchura(puntajes)/2), Juego.obtenerAltura()-ft.obtenerAltura(puntajes)-16);
    }

    private void mensajeGanador(String ganador) {
    if(sentidoMovimientoTextoGana==0){
                    movimientoTextoGana++;
           if(movimientoTextoGana==40){
                sentidoMovimientoTextoGana=1;
           }
    }else if(sentidoMovimientoTextoGana==1){
                    movimientoTextoGana--;
           if(movimientoTextoGana==-40){
                sentidoMovimientoTextoGana=0;
           }
    }

    juego.dibujarTexto(ftGanador,ganador, Juego.obtenerAnchura()/2 - (ftGanador.obtenerAnchura(ganador)/2) + movimientoTextoGana , Juego.obtenerAltura()/2-ftGanador.obtenerAltura(ganador)/2);
    juego.dibujarTexto(ft2,"Presione ESPACIO para iniciar nuevo juego", Juego.obtenerAnchura()/2 - (ft2.obtenerAnchura("Presione ESPACIO para iniciar nuevo juego")/2), Juego.obtenerAltura()/2-(ft2.obtenerAltura("Presione ESPACIO para iniciar nuevo juego")/2)-50);

    if(Teclado.teclaPresionada(Teclado.TECLA_ESPACIO)&&Teclado.obtenerIntervalo()>100){
        puntajeIzq= 0;
        puntajeDer= 0;

        actualizaPuntaje();
        juego.borrarTexto(ftGanador);

        estado = Estado.nuevoJuego;
    }

            }

    private void bolaGolpea() {
        if(golpeo){
            pelota.aplicarMascaraDeColor(pelota.obtenerMascaraColorRojo()-0.25f, pelota.obtenerMascaraColorVerde()-.01f,pelota.obtenerMascaraColorAzul());
            contadorRestaColoresBola++;
            if(contadorRestaColoresBola==10){
                golpeo=false;
                contadorRestaColoresBola=0;
            }
        }else{
            pelota.aplicarMascaraDeColor(pelota.obtenerMascaraColorRojo()+0.25f, pelota.obtenerMascaraColorVerde()+.01f,pelota.obtenerMascaraColorAzul());
        }
    }

    private void muestraCreditos() {
        juego.borrarRecursos();

        juego.agregarRecurso(creditos, 0, 0);
        juego.agregarRecurso(pelotaTitle, 287,393);
        pelotaTitle.rotarAgregado(Math.toRadians(.3), pelotaTitle.obtenerCentro().x, pelotaTitle.obtenerCentro().y);

        juego.agregarRecurso(regresar,10 , 10);

         if(regresar.ratonSobreObjeto()){
            regresar.aplicarMascaraDeColor(regresar.obtenerMascaraColorRojo()-0.03f,regresar.obtenerMascaraColorVerde() , regresar.obtenerMascaraColorAzul()-0.05f);
        }else{
            regresar.aplicarMascaraDeColor(regresar.obtenerMascaraColorRojo()+0.03f,regresar.obtenerMascaraColorVerde() , regresar.obtenerMascaraColorAzul()+0.05f);
        }

        if(regresar.ratonSobreObjeto()&&Raton.botonPresionado(Raton.BOTON_IZQUIERDO)&&Raton.obtenerIntervalo()>100){
            estado = Estado.titulo;
        }

       }


    private void saqueJugador() {
        pelota.aplicarMascaraDeColor(1, 1, 1);
        escuchaTeclado();
        velocidad=0.5f;
        if(Teclado.teclaPresionada(Teclado.TECLA_ESPACIO)&&Teclado.obtenerIntervalo()>100){
            estado = Estado.jugando;

        }
    }

    private void cambiarColorDeFondo() {

        switch(transicion){
            case rojo:
                if(fondo.obtenerMascaraColorRojo()>0){
                    fondo.aplicarMascaraDeColor(fondo.obtenerMascaraColorRojo()-.0005f, fondo.obtenerMascaraColorVerde()+0.0005f, fondo.obtenerMascaraColorAzul()+0.0005f);
                }else{
                    transicion=TransicionDeColor.azul;
                }
            break;

            case azul:
                if(fondo.obtenerMascaraColorAzul()>0){
                    fondo.aplicarMascaraDeColor(fondo.obtenerMascaraColorRojo()+.0005f, fondo.obtenerMascaraColorVerde()+.0005f, fondo.obtenerMascaraColorAzul()-.0005f);
                }else{
                    transicion=TransicionDeColor.verde;
                }
            break;

            case verde:
                if(fondo.obtenerMascaraColorVerde()>0){
                    fondo.aplicarMascaraDeColor(fondo.obtenerMascaraColorRojo()+.0005f, fondo.obtenerMascaraColorVerde()-0.0005f, fondo.obtenerMascaraColorAzul()+.0005f);
                }else{
                    transicion=TransicionDeColor.rojo;
                }
            break;
             }

    }


    public static void main(String arg[]) throws LWJGLException{
        
        Pong app = new Pong();

        app.loopPrincipal();
    }
   
    Pong() throws LWJGLException{
    juego = new Juego(800,600,100);
            
    //juego.asignarSincronizacion(true);
    juego.asignarSincronizacionVertical(false);

        
    ultimoClick=Utilidades.obtenerTiempo();   

    }

    private void loopPrincipal() {

        cargarRecursos();

        obtenerDelta(); 
	lastFPS = Utilidades.obtenerTiempo(); 
        siguienteTick = Utilidades.obtenerTiempo();
        while(true){
        
          if(Utilidades.obtenerTiempo()>siguienteTick){
            updateFPS();
            delta = obtenerDelta();    

            switch(estado){

                case titulo:
                    mostrarTitulo();
                    break;
                case jugando:
                    juego.borrarTexto(ft2);
                    cambiarColorDeFondo();
                    moverPelota();
                    escuchaTeclado();
                    actualizaPuntaje();
                    if(puntajeDer==5){
                        estado = Estado.ganaDer;
                    }else if(puntajeIzq==5){
                        estado = Estado.ganaIzq;
                    }
                    break;
                case nuevoJuego:

                    inicializarJuego();
                    break;
                case saqueDer:
                    vacio.asignarX(barraDer.obtenerX()-vacio.obtenerAnchura());
                    vacio.asignarY(barraDer.obtenerY()+(barraDer.obtenerAltura()/2)-vacio.obtenerAltura()/2);

                    saqueJugador();
                    break;
                case saqueIzq:

                    vacio.asignarX(barraIzq.obtenerX()+barraIzq.obtenerAnchura());
                    vacio.asignarY(barraIzq.obtenerY()+(barraIzq.obtenerAltura()/2)-vacio.obtenerAltura()/2);

                    saqueJugador();

                    break;
                case ganaIzq:
                    mensajeGanador("Gana Jugador 1!");
                    break;
                case ganaDer:
                    mensajeGanador("Gana Jugador 2!");
                    break;
                case creditos:
                    muestraCreditos();
                    break;
                }

           
              siguienteTick+=1000/ticks;
            }
         
              
        
             
                juego.actualizar();
        }

    }

    private void moverPelota() {
      
        if(vacio.obtenerX()+vacio.obtenerAnchura()>=juego.obtenerAnchura()){
           estado = Estado.saqueDer;
           puntajeIzq++;
           
        }else if(vacio.obtenerY()+vacio.obtenerAltura()>=juego.obtenerAltura()-barraPuntaje.obtenerAltura()){
            velocidadY=-velocidad;
            golpeo=true;
          

        } else if (vacio.colisionaLadoIzquierdoDeObjetivo(barraDer)) {
            velocidadX=-velocidad;
            direccion*=-1;
            velocidad*=1.01;
            golpeo=true;
           

        }else if(vacio.colisionaLadoDerechoDeObjetivo(barraIzq)){
            velocidadX=velocidad;
            direccion*=-1;
            velocidad*=1.01;
            golpeo=true;
         

        }else if(vacio.colisionaLadoSuperiorDeObjetivo(barraIzq)||vacio.colisionaLadoSuperiorDeObjetivo(barraDer)){
            velocidadY=velocidad;
            golpeo=true;
            
        
        
        }else if(vacio.colisionaLadoInferiorDeObjetivo(barraIzq)||vacio.colisionaLadoInferiorDeObjetivo(barraDer)){
            velocidadY=-velocidad;
            golpeo=true;
           
            
        
        
        }else if(vacio.obtenerX()<0){
            estado = Estado.saqueIzq;
            puntajeDer++;
        }else if(vacio.obtenerY()<barraAbajo.obtenerAltura()){
            velocidadY=velocidad;
            golpeo=true;
           
            
        }
        
        angulo=direccion;
            
        bolaGolpea();
        vacio.asignarX(pelota.obtenerX()+velocidadX*10);
        vacio.asignarY(pelota.obtenerY()+velocidadY*10);
        pelota.rotarAgregado(angulo,pelota.obtenerCentro().x, pelota.obtenerCentro().y);

        
}

    private void escuchaTeclado() {
        float velocidadBarras=(float) .5;
        

        if(Teclado.teclaPresionada(Teclado.TECLA_ABAJO)&&barraDer.obtenerY()>barraAbajo.obtenerAltura()){
            barraDer.asignarY(barraDer.obtenerY()-velocidadBarras*delta);
        }

        if(Teclado.teclaPresionada(Teclado.TECLA_ARRIBA)&&barraDer.obtenerY()+barraDer.obtenerAltura()<barraPuntaje.obtenerY()){
            barraDer.asignarY(barraDer.obtenerY()+velocidadBarras*delta);
        }

        if(Teclado.teclaPresionada(Teclado.TECLA_A)&&barraIzq.obtenerY()+barraIzq.obtenerAltura()<barraPuntaje.obtenerY()){
            barraIzq.asignarY(barraIzq.obtenerY()+velocidadBarras*delta);
        }

        if(Teclado.teclaPresionada(Teclado.TECLA_Z)&&barraIzq.obtenerY()>barraAbajo.obtenerAltura()){
            barraIzq.asignarY(barraIzq.obtenerY()-velocidadBarras*delta);
        }
       
        
        

    }

    private float obtenerDelta() {
            long tiempo = Utilidades.obtenerTiempo();
	    float delta = (float) (tiempo - ultimoFrame);
	    ultimoFrame = tiempo;
          
	    return delta;
    }

        public void updateFPS() {
		if (Utilidades.obtenerTiempo() - lastFPS > 1000) {
			Display.setTitle("Pong! by Daniel Nieto | FPS: " + fps);
                 
                        fps = 0;
			lastFPS += 1000;

		}
		fps++;
	}
    
public void cargarRecursos(){
    long inicio = Utilidades.obtenerTiempo();
    long otro;
    pelota = new Recurso("imagenes/pelota_25_perrona.png");      
    otro =Utilidades.obtenerTiempo();

    barraDer = new Recurso("imagenes/derecho.png");
    inicio =Utilidades.obtenerTiempo();
   
    barraIzq = new Recurso("imagenes/izquierdo.png");
  
    vacio = new Vacio(30,30);


    pantallaTitulo = new Recurso("imagenes/titleScreen.png");
    fondo = new Recurso("imagenes/background.png");
    barraPuntaje = new Recurso("imagenes/top.png");
    barraAbajo = new Recurso("imagenes/bottom.png");
    btnJugar = new Recurso("imagenes/btnJugar.png");
    pelotaTitle = new Recurso("imagenes/pelotaTitle.png");
    btnCreditos = new Recurso("imagenes/btnCreditos.png");
    btnSalir = new Recurso("imagenes/btnSalir.png");
    creditos = new Recurso("imagenes/creditos.png");
    regresar = new Recurso("imagenes/regresar.png");

  
        



    try {
            ft = new Fuente("imagenes/fuente.ttf", 50,false,false,Color.lightGray);
           
            ftGanador = new Fuente("imagenes/fuente.ttf", 80,false,false,Color.WHITE);
           
            ft2 = new Fuente("imagenes/fuente.ttf", 16,false,false,Color.DARK_GRAY);
           
            ftCreditos = new Fuente("imagenes/fuente.ttf", 32,false,false,Color.DARK_GRAY);
            
            
    } catch (SlickException ex) {

    }

}

public void inicializarJuego(){
    
    juego.borrarRecursos();

    juego.dibujarTexto(ft2, "Presiona ESPACIO para iniciar", Juego.obtenerAnchura()/2 - (ft2.obtenerAnchura("Presiona ESPACIO para iniciar")/2),Juego.obtenerAltura()/2 - (ft2.obtenerAltura("Presiona ESPACIO para iniciar"))+50);

    juego.agregarRecurso(fondo, 0, 0);

    juego.agregarRecurso(barraDer, juego.obtenerAnchura()-(barraDer.obtenerAnchura())-20, juego.obtenerAltura()/2-barraDer.obtenerAltura()/2);

    juego.agregarRecurso(barraIzq, 20, juego.obtenerAltura()/2-barraDer.obtenerAltura()/2);

    juego.agregarRecurso(barraAbajo, 0, 0);
    
    juego.agregarRecurso(barraPuntaje, 0, Juego.obtenerAltura()-barraPuntaje.obtenerAltura());

    juego.agregarRecurso(vacio,juego.obtenerAnchura()/2-(pelota.obtenerAnchura()/2),juego.obtenerAltura()/2-(pelota.obtenerAltura()/2));
        
    juego.agregarRecurso(pelota, juego.obtenerAnchura()/2-(pelota.obtenerAnchura()/2), juego.obtenerAltura()/2-(pelota.obtenerAltura()/2));


  
    vacio.agregarHijo(pelota);

    if(Teclado.teclaPresionada(Teclado.TECLA_ESPACIO)&&Teclado.obtenerIntervalo()>100){
        estado = Estado.jugando;
    }

    

}
}
