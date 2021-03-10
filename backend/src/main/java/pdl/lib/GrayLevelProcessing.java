package pdl.lib;

import net.imglib2.RandomAccess;
import net.imglib2.Cursor ;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
//import io.scif.SCIFIO;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.view.Views;
import net.imglib2.view.IntervalView;
import net.imglib2.loops.LoopBuilder;

public class GrayLevelProcessing{

	public static int [] histogramInGray(Img<UnsignedByteType> img){
		final Cursor<UnsignedByteType> c = img.cursor() ;

		// first we compute the histogram
		int histogram[] = new int[256] ;
		Arrays.fill(histogram, 0) ;
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			histogram[val.get()]++ ;
		}
		return histogram ;
	}

	public static void histogramExtensionColorGray(Img<UnsignedByteType> img){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		final IntType valueInt1 = new IntType(1) ;

		List<IntType[]> listOfHistograms = LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachChunk(
			chunk -> {
				IntType [] histogram = new IntType[256] ;
				for(int i = 0; i < 256; i++)
					histogram[i] = new IntType(0) ;
				chunk.forEachPixel((r,g,b) -> {
					histogram[(int) Math.round(0.3*r.get() + 0.59*g.get() + 0.11*b.get())].add(valueInt1); ;
				});
				return histogram ;
			}
		) ;
		IntType[] completeHistogram = new IntType[256] ;
		for(int i = 0; i < 256; i++)
			completeHistogram[i] = new IntType(0) ;

		listOfHistograms.forEach(histogram -> {
			for(int i = 0; i < 256; i++){
				completeHistogram[i].add(histogram[i]) ;
			}
		});

		int cumulatedHistogram[] = new int[256] ;
		cumulatedHistogram[0] = completeHistogram[0].get() ;
		for(int i = 1; i< 256; i++){
			cumulatedHistogram[i] = cumulatedHistogram[i-1] + completeHistogram[i].get() ;
		}
		int lookUpTable[] = new int[256] ;
		for(int i = 0; i < 256; i++){
			// look up table
			lookUpTable[i] = cumulatedHistogram[i]*255/ ((int)img.size()) ;
		}
		LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachPixel(
			(r, g, b) -> {
				r.set(lookUpTable[r.get()]) ; g.set(lookUpTable[g.get()]) ; b.set(lookUpTable[b.get()]) ;
			}
		);
	}

	public static void histogramExtensionColorValue(Img<UnsignedByteType> img){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		final IntType valueInt1 = new IntType(1) ;
		// we consider the v of hsv as an int between 0 and 100

		List<IntType[]> listOfHistograms = LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachChunk(
			chunk -> {
				IntType [] histogram = new IntType[101] ;
				float [] hsv = new float[3] ;
				for(int i = 0; i <= 100; i++)
					histogram[i] = new IntType(0) ;
				chunk.forEachPixel((r,g,b) -> {
					rgbToHsv(r.get(), g.get(), b.get(), hsv);
					histogram[(int) Math.round(hsv[2]*100)].add(valueInt1) ;
				});
				return histogram ;
			}
		) ;
		IntType[] completeHistogram = new IntType[101] ;
		for(int i = 0; i <= 100; i++)
			completeHistogram[i] = new IntType(0) ;

		listOfHistograms.forEach(histogram -> {
			for(int i = 0; i <= 100; i++){
				completeHistogram[i].add(histogram[i]) ;
			}
		});

		int cumulatedHistogram[] = new int[101] ;
		cumulatedHistogram[0] = completeHistogram[0].get() ;
		for(int i = 1; i<= 100; i++){
			cumulatedHistogram[i] = cumulatedHistogram[i-1] + completeHistogram[i].get() ;
		}
		int lookUpTable[] = new int[101] ;
		for(int i = 0; i <= 100; i++){
			// look up table
			lookUpTable[i] = cumulatedHistogram[i]*100/ ((int)img.size()) ;
		}
		LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachPixel(
			(r, g, b) -> {
				float [] hsv = new float[3] ;
				int [] rgb = new int [] {r.get(), g.get(), b.get()} ;
				rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
				hsv[2] = ((float) lookUpTable[(int)Math.round(hsv[2]*100)])/100 ;
				hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
				r.set(rgb[0]) ; g.set(rgb[1]) ; b.set(rgb[2]) ;
			}
		);
	}

	public static void improveContrastHistogramEqualization(Img<UnsignedByteType> img){

		final Cursor<UnsignedByteType> c = img.cursor() ;

		// first we compute the histogram
		int histogram[] = new int[256] ;

		Arrays.fill(histogram, 0) ;
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			histogram[val.get()]++ ;
		}
		// second we compute the cumulated histogram
		int cumulatedHistogram[] = new int[256] ;
		cumulatedHistogram[0] = histogram[0] ;
		for(int i = 1; i< 256; i++){
			cumulatedHistogram[i] = cumulatedHistogram[i-1] + histogram[i] ;
		}
		int lookUpTable[] = new int[256] ;
		for(int i = 0; i < 256; i++){
			// look up table
			lookUpTable[i] = cumulatedHistogram[i]*255/ ((int)img.size()) ;
		}
		c.reset(); ;
		// now we make the dynamic extension
		while(c.hasNext()){
			c.fwd();
			final int val = c.get().get();
			c.get().set(lookUpTable[val]) ;
		}
	}
	/**
	 * @Deprecated
	 */
	public static void improveContrastDynamicExtension(Img<UnsignedByteType> img, int newMin, int newMax){
		//verified by magic : ok ; +- 50 ms for image-1 ; +- 63 ms for street
		int min = 256, max = -1 ;
		Cursor<UnsignedByteType> c = img.cursor() ;
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			if (val.get() < min) min = val.get() ;
			if (val.get() > max) max = val.get() ;
		}
		c = img.cursor() ;
		// now we make the dynamic extension
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			val.set((newMax-newMin)*(val.get()-min)/(max-min) + newMin) ;
		}

	}


	public static void improveContrastDynamicExtensionLUT(Img<UnsignedByteType> img, int newMin, int newMax){
		int min = 256, max = -1 ;
		Cursor<UnsignedByteType> c = img.cursor() ;
		//first we compute the min and the max values of the histogram
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			if (val.get() < min) min = val.get() ;
			if (val.get() > max) max = val.get() ;
		}

		int lookUpTable[] = new int[max-min+1] ;
		for(int i = min; i <= max; i++){
			// look up table formation
			lookUpTable[i-min] = (newMax-newMin)*(i-min)/(max - min) + newMin;
		}

		c = img.cursor() ;
		// now we make the dynamic extension
		while(c.hasNext()){
			c.fwd();
			final UnsignedByteType val = c.get();
			val.set(lookUpTable[val.get()-min]) ;
		}
	}

	public static void changeLuminosityLoop(Img<UnsignedByteType> img, int delta){
		LoopBuilder.setImages(img).multiThreaded().forEachPixel(
			pix -> {
				if (pix.get() + delta > 255) pix.set(255) ;
				else if (pix.get() + delta < 0) pix.set(0) ;
				else pix.set(pix.get() + delta) ;
			}
		);
	}
	public static void changeLuminosityRandomAccessNVerif(Img<UnsignedByteType> img, int delta){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				int val = r.get().get();
				r.get().set(val + delta) ; // non verification de l'intervalle
			}
		}
	}

	public static void changeLuminosityRandomAccessVerif(Img<UnsignedByteType> img, int delta){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				int val = r.get().get();
				if (val + delta > 255) r.get().set(255) ;
				else if (val + delta < 0) r.get().set(0) ;
				else r.get().set(val + delta) ;
			}
		}
	}

	public static void changeLuminosityRandomAccessVerifColored(Img<UnsignedByteType> img, int delta){
		RandomAccess<UnsignedByteType> r = Views.hyperSlice(img, 2, 0).randomAccess(), g = Views.hyperSlice(img, 2, 1).randomAccess(),
		 b = Views.hyperSlice(img, 2, 2).randomAccess() ;

		 List<RandomAccess<UnsignedByteType>> randomAccesses = new ArrayList<>() ;
		 randomAccesses.add(r) ; randomAccesses.add(g) ; randomAccesses.add(b) ;

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y){
				for (RandomAccess<UnsignedByteType> ra : randomAccesses){
					ra.setPosition(x, 0);
					ra.setPosition(y, 1);

					int val = ra.get().get();
					if (val + delta > 255) ra.get().set(255) ;
					else if (val + delta < 0) ra.get().set(0) ;
					else ra.get().set(val + delta) ;
				}
			}
		}
	}

	public static void changeLuminosityCursorClassic(Img<UnsignedByteType> img, int delta){
		final Cursor<UnsignedByteType> c = img.cursor() ;

		while(c.hasNext()){
			c.fwd();
			int val = c.get().get();
			if (val + delta > 255) c.get().set(255) ;
			else if (val + delta < 0) c.get().set(0) ;
			else c.get().set(val + delta) ;
		}
	}


	public static void changeLuminosityCursorImproved(Img<UnsignedByteType> img, int delta){
		final Cursor<UnsignedByteType> c = img.cursor() ;

		int [] lut = new int [256] ;
		for (int i = 0; i < 256; i++){
			if (i + delta > 255) lut[i] = 255 ;
			else if (i + delta < 0) lut[i] = 0 ;
			else lut[i] = i+ delta ;
		}

		while(c.hasNext()){
			c.fwd();
			int val = c.get().get();
			c.get().set(lut[val]) ;
		}
	}

	public static void colorImg(Img<UnsignedByteType> input, int newHue){
		if (newHue < 0 || newHue > 360){
			System.err.println("La nouvelle teinte doit être dans [0, 360]") ;
			System.exit(-1) ;
		}
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0), inputG = Views.hyperSlice(input, 2, 1), inputB = Views.hyperSlice(input, 2, 2);
		int [] rgb = new int[3] ; float [] hsv = new float [3] ;
		LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
			(r, g, b) -> {
				rgbToHsv(r.get(), g.get(), b.get(), hsv);
				hsv[0]=newHue ;
				hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
				r.set(rgb[0]) ; g.set(rgb[1]) ; b.set(rgb[2]) ;
			}
		);
	}

	public static void betterHistogramEqualization(Img<UnsignedByteType> img){
		long base = computeMeanLuminosity(img) ;
		histogramExtensionColorGray(img);
		changeLuminosityCursorImproved(img, (int) (base - computeMeanLuminosity(img) + 100));
	}

	public static void dynamicExtensionColorGrayLut(Img<UnsignedByteType> img, int newMin, int newMax){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		

		List<IntType[]> listOfMins = LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachChunk(
			chunk -> {
				IntType[] values = new IntType[]{new IntType(256), new IntType(-1)} ;
				chunk.forEachPixel((r,g,b) -> {
					int grayValue = (int) (0.3*r.get() + 0.59*g.get() + 0.11*b.get()) ;
					values[0].set(Math.min(values[0].get(), grayValue )) ;
					values[1].set(Math.max(values[1].get(), grayValue )) ;
				});
				return values ;
			}
		) ;

		IntType[] globalValues = new IntType[]{new IntType(256), new IntType(-1)} ;
		listOfMins.forEach( value -> {
			globalValues[0].set(Math.min(value[0].get(), globalValues[0].get())) ;
			globalValues[1].set(Math.max(value[1].get(), globalValues[1].get())) ;
		}) ;

		int intMin = globalValues[0].get(), intMax = globalValues[1].get()  ;

		/*
		Simple way but not efficient
		int intMax = -1, intMin = 256 ;
		final Cursor<UnsignedByteType> cR = inputR.cursor(), cG = inputG.cursor(), cB = inputB.cursor();
		while(cR.hasNext() && cG.hasNext() && cB.hasNext()){
			cR.fwd() ; cG.fwd() ; cB.fwd() ;
			int value = (int) (0.3*cR.get().get() + 0.59*cG.get().get() + 0.11*cB.get().get()) ;
			intMin = Math.min(intMin, value) ; intMax = Math.max(intMax, value) ;
		}*/

		int lookUpTable[] = new int[256] ;
		for(int i = 0; i < 256; i++){
			// look up table formation
			lookUpTable[i] = (newMax-newMin)*(i-intMin)/(intMax - intMin) + newMin;
		}

		LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
            (r, g, b) -> {
                r.set(lookUpTable[r.get()]);
				g.set(lookUpTable[g.get()]);
				b.set(lookUpTable[b.get()]);
            }
        );

	}
	public static void dynamicExtensionColorValueLut(Img<UnsignedByteType> img, int newMin, int newMax){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		if (newMin < 0 || newMax < 0 || newMin >100 || newMax > 100){
			System.err.println("les nouvelles valeurs de min et max doivent être dans [0, 100]") ;
			System.exit(-1) ;
		}

		List<IntType[]> listOfExtrema =LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachChunk(
			chunk -> {
				IntType[] extrema = new IntType[] {new IntType(101), new IntType(-1)} ; // we consider v has an int value between 0 and 100
				chunk.forEachPixel((r,g,b) -> {
					float [] hsv = new float[3] ;
					rgbToHsv(r.get(),g.get(),b.get(), hsv) ;
					int value = (int) (hsv[2]*100) ;
					extrema[0].set(Math.min(extrema[0].get(), value)) ; // extrema[0] is the min
					extrema[1].set(Math.max(extrema[1].get(), value)) ; // extrema[1] is the max
				}) ;
				return extrema ;
			}
		) ; // we have in listOfExtrema the locals extrema

		IntType[] globalExtrema = new IntType[] {new IntType(101), new IntType(-1)} ; // we consider v has an int value between 0 and 100

		listOfExtrema.forEach( extrema -> {
			globalExtrema[0].set(Math.min(globalExtrema[0].get(), extrema[0].get())) ;
			globalExtrema[1].set(Math.max(globalExtrema[1].get(), extrema[1].get())) ;
		} );

		int intMin = globalExtrema[0].get(), intMax = globalExtrema[1].get() ;

		System.out.println(intMin +" "+ intMax) ;

		int [] lookUpTable = new int[intMax - intMin + 1] ;

		for(int i = intMin ; i <= intMax; i++){
			//making the lut
			lookUpTable[i-intMin] = (newMax-newMin)*(i-intMin)/(intMax - intMin) + newMin ;
		}

		LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
            (r, g, b) -> {
				float [] hsv = new float[3] ;
				rgbToHsv(r.get(), g.get(), b.get(), hsv);
				int value = (int) (hsv[2]*100) ;
				value = lookUpTable[value-intMin] ;

				hsv[2] = ((float) value)/100 ;
				int [] rgb = new int[3] ;
				hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
				r.set(rgb[0]) ; g.set(rgb[1]) ; b.set(rgb[2]) ;
            }
		);

	}


	public static void colorToGrayLBMultithreaded(Img<UnsignedByteType> img){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);

        LoopBuilder.setImages(inputR, inputG, inputB).multiThreaded().forEachPixel(
            (r, g, b) -> {
				int value = (int) (0.3*r.get() + 0.59*g.get() + 0.11*b.get()) ;
                r.set(value);
				g.set(value);
				b.set(value);
            }
        );
	}
	public static void colorToGrayLBNMultithreaded(Img<UnsignedByteType> img){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
            (r, g, b) -> {
				int value = (int) (0.3*r.get() + 0.59*g.get() + 0.11*b.get()) ;
                r.set(value);
				g.set(value);
				b.set(value);
            }
        );
	}
	public static void colorToGrayCursor(Img<UnsignedByteType> img){
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		final Cursor<UnsignedByteType> cursorR =inputR.cursor(), cursorG = inputG.cursor(), cursorB = inputB.cursor() ;
		while (cursorR.hasNext() && cursorG.hasNext() && cursorB.hasNext()){
			cursorR.fwd(); cursorG.fwd(); cursorB.fwd();
			int value = (int) (0.3*cursorR.get().get() + 0.59*cursorG.get().get() + 0.11*cursorB.get().get()) ;
			cursorR.get().set(value) ; cursorG.get().set(value) ; cursorB.get().set(value) ;
		}
	}


	public static void rgbToHsv(int r, int g, int b, float[] hsv){
		int max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b) ;
		float maxF = max, minF = min, rF = r, gF = g, bF = b ;
		if (max == min)
			hsv[0] = 0 ;
		else if (max == r)
			hsv[0] = (60 * ( (gF-bF)/(maxF - minF) ) + 360)%360 ;
		else if (max == g)
			hsv[0] = 60 * ( (bF-rF)/(maxF - minF) ) + 120 ;
		else
			hsv[0] = 60 * ( (rF-gF)/(maxF - minF) ) + 240 ;
		if (max == 0)
			hsv[1] = 0 ;
		else
			hsv[1] = 1.0F -  minF/ maxF ;
		hsv[2] = ((float) max)/255.0F ;
	}

	public static void hsvToRgb(float h, float s, float v, int[] rgb){
		int t = (int) (h/60)%6 ;
		float f = h/60 - t ;
		int l = Math.round(v*(1-s)*255) ;
		int m = Math.round(v * (1- f*s)* 255) ;
		int n = Math.round(v * (1- (1-f)*s)*255) ;
		int nv = Math.round(v*255) ;
		switch(t){
			case(0):
				rgb[0] = nv ; rgb[1] = n; rgb[2] = l ;
				break ;
			case(1):
				rgb[0] = m ; rgb[1] = nv; rgb[2] = l ;
				break ;
			case(2):
				rgb[0] = l ; rgb[1] = nv; rgb[2] = n ;
				break ;
			case(3):
				rgb[0] = l ; rgb[1] = m; rgb[2] = nv ;
				break ;
			case(4):
				rgb[0] = n ; rgb[1] = l; rgb[2] = nv ;
				break ;
			default:
				rgb[0] = nv ; rgb[1] = l; rgb[2] = m ;
				break ;
		}
	}

	public static long computeMeanLuminosity(Img<UnsignedByteType> img){
		long sum = 0 ;
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0), inputG = Views.hyperSlice(img, 2, 1), inputB = Views.hyperSlice(img, 2, 2);
		final Cursor<UnsignedByteType> cursorR =inputR.cursor(), cursorG = inputG.cursor(), cursorB = inputB.cursor() ;
		while (cursorR.hasNext() && cursorG.hasNext() && cursorB.hasNext()){
			cursorR.fwd(); cursorG.fwd(); cursorB.fwd();
			int value = (int) (0.3*cursorR.get().get() + 0.59*cursorG.get().get() + 0.11*cursorB.get().get()) ;
			sum += value ;
		}
		return sum / img.size() ;
	}

	public static void threshold(Img<UnsignedByteType> img, int t) {
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				if (val.get() < t)
				    val.set(0);
				else
				    val.set(255);
			}
		}
	}
}
