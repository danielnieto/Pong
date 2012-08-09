package org.niktin.juego;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.niktin.recursos.Recurso;
import javax.imageio.ImageIO;

/**
 *
 * @author Daniel
 */
public class ManejadorDeRecursos {
    private BufferedImage imagenACargar;
public ManejadorDeRecursos(){}

public BufferedImage cargarImagen(String ruta) throws IOException{
    imagenACargar = ImageIO.read(getClass().getResource(ruta));

    //Recurso nuevo = new Recurso(imagenACargar,imagenACargar.getWidth(),imagenACargar.getHeight(),0,0);

    return imagenACargar;
}

public BufferedImage cargarImagenDeMatriz(String ruta, int numX, int numY,int posicion) throws IOException{
    imagenACargar = ImageIO.read(getClass().getResource(ruta));
    int x=0,y=0,ancho,alto;
    int contadorPosicion=0;

    for(y=0;y<numY;y++){
        for(x=0;x<numX;x++){
           
            if(contadorPosicion==posicion){
                break;
            }

            contadorPosicion++;
        }

        if(contadorPosicion==posicion){
                break;
            }
    }

    alto = imagenACargar.getHeight()/numY;
    ancho = imagenACargar.getWidth()/numX;

    System.out.println("X:" + x*ancho+" - Y:" + y*alto);

   

    return imagenACargar.getSubimage(x*ancho, y*alto, ancho, alto);
}


}
