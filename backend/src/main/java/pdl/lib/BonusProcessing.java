package pdl.lib;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;

public class BonusProcessing {
    public static void addImg(Img<UnsignedByteType> img1, Img<UnsignedByteType> img2, Img<UnsignedByteType> output){
        RandomAccess<UnsignedByteType> random1 = img1.randomAccess(), random2 = img2.randomAccess(), rOutput = output.randomAccess() ;
        long maxW1 = img1.max(0), maxW2 = img2.max(0), maxH1 = img1.max(1), maxH2 = img2.max(1) ;
        long maxW = Math.max(maxW1, maxW2), maxH = Math.max(maxH1, maxH2) ;

        for(long x = 0 ; x <= maxW; x++){
            for(long y = 0; y <= maxH; y++){
                int pix1 = -1, pix2 = -1 ;
                if (x <= maxW1 && y <= maxH1){
                    random1.setPosition(x, 0);
                    random1.setPosition(y, 1);
                    pix1 = random1.get().get() ;
                }
                if (x <= maxW2 && y <= maxH2){
                    random2.setPosition(x, 0);
                    random2.setPosition(y, 1);
                    pix2 = random2.get().get() ;
                }
                rOutput.setPosition(x, 0) ;
                rOutput.setPosition(y, 1) ;
                if(pix1 >= 0 && pix2 >= 0){
                    rOutput.get().set((pix1+pix2)/2) ; // la valeur de pix1+pix2 est entre 0 et 255
                }
                else if (pix1 >= 0){
                    rOutput.get().set(pix1) ;
                }
                else {
                    rOutput.get().set(pix2) ;
                }
            }
        }
    }

    public static void subImg(Img<UnsignedByteType> img1, Img<UnsignedByteType> img2, Img<UnsignedByteType> output){
        RandomAccess<UnsignedByteType> random1 = img1.randomAccess(), random2 = img2.randomAccess(), rOutput = output.randomAccess() ;
        long maxW1 = img1.max(0), maxW2 = img2.max(0), maxH1 = img1.max(1), maxH2 = img2.max(1) ;
        long maxW = Math.max(maxW1, maxW2), maxH = Math.max(maxH1, maxH2) ;

        for(long x = 0 ; x <= maxW; x++){
            for(long y = 0; y <= maxH; y++){
                int pix1 = -1, pix2 = -1 ;
                if (x <= maxW1 && y <= maxH1){
                    random1.setPosition(x, 0);
                    random1.setPosition(y, 1);
                    pix1 = random1.get().get() ;
                }
                if (x <= maxW2 && y <= maxH2){
                    random2.setPosition(x, 0);
                    random2.setPosition(y, 1);
                    pix2 = random2.get().get() ;
                }
                rOutput.setPosition(x, 0) ;
                rOutput.setPosition(y, 1) ;
                if(pix1 >= 0 && pix2 >= 0){
                    rOutput.get().set(Math.abs(pix1 - pix2)) ;
                }
                else if (pix1 >= 0){
                    rOutput.get().set(pix1) ;
                }
                else {
                    rOutput.get().set(255 - pix2) ;
                }
            }
        }

    }

    public static void divideImg(Img<UnsignedByteType> img1, Img<UnsignedByteType> img2, Img<UnsignedByteType> output){
        RandomAccess<UnsignedByteType> random1 = img1.randomAccess(), random2 = img2.randomAccess(), rOutput = output.randomAccess() ;
        long maxW1 = img1.max(0), maxW2 = img2.max(0), maxH1 = img1.max(1), maxH2 = img2.max(1) ;
        long maxW = Math.max(maxW1, maxW2), maxH = Math.max(maxH1, maxH2) ;
        double min = 256, max = -1 ;

        for(long x = 0 ; x <= maxW; x++){
            for(long y = 0; y <= maxH; y++){
                //premier parcours pour connaitre la valeur du min et du max
                int pix1 = -1, pix2 = -1 ;
                if (x <= maxW1 && y <= maxH1){
                    random1.setPosition(x, 0);
                    random1.setPosition(y, 1);
                    pix1 = random1.get().get() ;
                }
                if (x <= maxW2 && y <= maxH2){
                    random2.setPosition(x, 0);
                    random2.setPosition(y, 1);
                    pix2 = random2.get().get()+1 ;
                }
                rOutput.setPosition(x, 0) ;
                rOutput.setPosition(y, 1) ;
                if(pix1 >= 0 && pix2 > 0){
                    double value = ((double)pix1)/((double)pix2) ;
                    if (value < min) min = value ;
                    if(value > max) max = value ;
                }
            }
        }



        for(long x = 0 ; x <= maxW; x++){
            for(long y = 0; y <= maxH; y++){
                int pix1 = -1, pix2 = -1 ;
                if (x <= maxW1 && y <= maxH1){
                    random1.setPosition(x, 0);
                    random1.setPosition(y, 1);
                    pix1 = random1.get().get() ;
                }
                if (x <= maxW2 && y <= maxH2){
                    random2.setPosition(x, 0);
                    random2.setPosition(y, 1);
                    pix2 = random2.get().get()+1 ; //eviter division par 0
                }
                rOutput.setPosition(x, 0) ;
                rOutput.setPosition(y, 1) ;
                if(pix1 >= 0 && pix2 > 0){
                    double value = ((double)pix1)/((double)pix2) ;
                    rOutput.get().set( (int) (255.*(value-min)/(max-min))) ;
                }
                else if (pix1 >= 0){
                    rOutput.get().set(pix1) ;
                }
                else {
                    rOutput.get().set(0) ;
                }
            }
        }
    }
    public static void rotate(Img<UnsignedByteType> img, Img<UnsignedByteType> output, int tetha){
        final RandomAccess<UnsignedByteType> rInput = img.randomAccess(), rOutput = output.randomAccess() ;
        double cosT = Math.cos(Math.toRadians(-tetha)), sinT =  Math.sin(Math.toRadians(-tetha));
        int cx = (int) output.max(0)/2, cy = (int) output.max(1)/2 ;
        int newCX = (int) (cosT*cx + sinT*cy), newCY = (int) (-sinT*cx + cosT*cy) ;
        int tx = -newCX+cx, ty = -newCY+cy ;
        long maxW = img.max(0), maxH = img.max(1) ;
        for(int x = 0 ; x <= maxW ; x ++){
            for(int y = 0 ; y <= maxH ; y ++){
                rOutput.setPosition(x, 0);
                rOutput.setPosition(y, 1);

                int newX = (int) (cosT*x + sinT*y) +tx, newY = (int) (-sinT*x + cosT*y)  + ty;
                if (newX >= 0 && newY >= 0 && newX <= maxW && newY <=maxH){
                    rInput.setPosition(newX, 0);
                    rInput.setPosition(newY, 1);
                    rOutput.get().set(rInput.get().get()) ;
                }
                else {
                    rOutput.get().set(0) ;
                }
            }
        }

    }

    public static void negative(Img<UnsignedByteType> img, Img<UnsignedByteType> output){
        final Cursor<UnsignedByteType> cInput = img.cursor(), cOutput = output.cursor() ;
        while (cInput.hasNext() && cOutput.hasNext()){
            cInput.fwd(); cOutput.fwd() ;
            cOutput.get().set(255 - cInput.get().get()) ;
        }
    }

    public static void pixellization(Img<UnsignedByteType> img, Img<UnsignedByteType> output, int blocSize){
        if (blocSize <= 0){
            System.err.println("error : blocSize has to be positive") ;
            return ;
        }
        final RandomAccess<UnsignedByteType> rInput = img.randomAccess(), rOutput = output.randomAccess() ;
        long maxW = img.max(0), maxH = img.max(1) ;
        for(int x = 0 ; x <= maxW ; x += blocSize){
            for(int y = 0 ; y <= maxH ; y += blocSize){
                long sum = 0, nbPix = 0 ;
                for(int i = 0 ; i < blocSize && x + i <=maxW ; i++){
                    for(int j = 0 ; j < blocSize && y + j <= maxH ; j++){
                        rInput.setPosition(x+i, 0);
                        rInput.setPosition(y+j, 1);
                        sum+=rInput.get().get() ;
                        nbPix++ ;
                    }
                }
                sum/=nbPix ;
                for(int i = 0 ; i < blocSize && x + i <=maxW ; i++){
                    for(int j = 0 ; j < blocSize && y + j <= maxH ; j++){
                        rOutput.setPosition(x+i, 0);
                        rOutput.setPosition(y+j, 1);
                        rOutput.get().set((int)sum) ;
                    }
                }
            }
        }
    }
}

