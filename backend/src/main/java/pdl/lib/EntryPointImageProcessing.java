package pdl.lib;

import java.security.InvalidAlgorithmParameterException;

import pdl.lib.exception.NonExistingAlgorithmException;
import pdl.lib.exception.NonExistingParameterException;
import net.imglib2.img.array.ArrayImgFactory;

import net.imglib2.img.Img;
import org.scijava.io.location.Location ;
import io.scif.img.ImgOpener ;
import io.scif.img.ImgSaver ;

import org.scijava.io.location.BytesLocation ;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import pdl.backend.Image;

public class EntryPointImageProcessing {
    public static byte [] algorithmEntryPoint(Image image, String [] params) throws InvalidAlgorithmParameterException, NonExistingAlgorithmException, NonExistingParameterException {
        if (params[0].equals("increaseLuminosity")) {
            String[] paramGain = params[1].split("=") ;
            if (paramGain.length != 2) {
                throw new InvalidAlgorithmParameterException("Need a gain to be precised") ;
            }
            if (paramGain[0].equals("gain")){
                int gain = 0 ;
                try {
                    gain = Integer.parseInt(paramGain[1]) ;
                } catch (NumberFormatException e) {
                    throw new InvalidAlgorithmParameterException("The gain has to be a number") ;
                }
                BytesLocation loc = new BytesLocation(image.getData(), image.getName()) ;
                BytesLocation locBack = new BytesLocation(image.getData().length, "modif."+image.getType().getSubtype()) ;
                ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType ()) ;
                ImgOpener imgOpener = new ImgOpener() ;
                Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(loc, factory).get(0) ;
                imgOpener.context().dispose() ;
                GrayLevelProcessing.changeLuminosityCursorClassic(input, gain);
                ImgSaver saver = new ImgSaver() ;
                saver.saveImg(locBack, input) ;
                saver.context().dispose() ;
                byte[] backData = locBack.getByteBank().toByteArray() ;
                return backData ;
            }
        }
        else {
            throw new NonExistingAlgorithmException("Cet algorithme n'existe pas") ;
        }
        return null ;
    }
}