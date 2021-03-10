package pdl.lib;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;
import java.util.Scanner;

import net.imglib2.view.Views;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.util.Intervals;
import net.imglib2.Interval;

public class Convolution {

	/**
	 * Question 1.1
	 */
	public static void meanFilterSimple(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output) {
		final RandomAccess<UnsignedByteType> rInput = input.randomAccess(), rOutput = output.randomAccess();
		final int maxW = (int) input.max(0); //getting the size
		final int maxH = (int) input.max(1);

		for(int y = 1 ; y < maxH; y++){
			// Bounds are not considered
			for(int x = 1 ; x < maxW ; x++){
				 // x,y correspond to the center of the filter

				int sum = 0 ;
				for(int i = -1 ; i < 2; i++){
					for(int j = -1 ; j < 2; j++){
						rInput.setPosition(x+i, 0);
						rInput.setPosition(y+j, 1);
						sum+=rInput.get().get() ;
					}
				}
				rOutput.setPosition(x, 0);
				rOutput.setPosition(y, 1);
				rOutput.get().set(sum/9);
			}
		}
	}
	/**
	 * Question 1.2
	 */
	public static void meanFilterWithBorders(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, int size) {
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, size/2, size/2 );
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorSingle(input, size/2, size/2 );
		//final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, size/2, size/2) ;
		//final IntervalView<UnsignedByteType> expandedView = Views.expandValue(input, 255, size/2, size/2) ;
		final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorDouble(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorSingle(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendZero(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendValue(input, 255);

		final Cursor<UnsignedByteType> c = output.cursor() ;
		while(c.hasNext()){
			c.fwd() ;
			int x = c.getIntPosition(0), y =c.getIntPosition(1) ;
			RandomAccessibleInterval< UnsignedByteType > interval =  Views.interval(expandedView, new long[] {x-size/2, y-size/2}, new long[] {x+size/2, y+size/2}) ;
			final Cursor< UnsignedByteType > cneigh = Views.iterable( interval ).cursor();
			int sum = 0 ;
			while(cneigh.hasNext()){
				cneigh.fwd() ;
				sum+=cneigh.get().get() ;
			}
			sum/=(size*size) ;
			c.get().set(sum) ;
		}
	}

	public static void meanFilterWithBordersColored(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, int size) {
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, size/2, size/2 );
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorSingle(input, size/2, size/2 );
		//final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, size/2, size/2) ;
		//final IntervalView<UnsignedByteType> expandedView = Views.expandValue(input, 255, size/2, size/2) ;
		final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorDouble(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorSingle(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendZero(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendValue(input, 255);

		final Cursor<UnsignedByteType> c = output.cursor() ;
		while(c.hasNext()){
			c.fwd() ;
			int x = c.getIntPosition(0), y =c.getIntPosition(1) ;
			RandomAccessibleInterval< UnsignedByteType > interval =  Views.interval(expandedView, new long[] {x-size/2, y-size/2, 0}, new long[] {x+size/2, y+size/2, 2}) ;
			final Cursor< UnsignedByteType > cneigh = Views.iterable( interval ).cursor();
			int sum = 0 ;
			while(cneigh.hasNext()){
				cneigh.fwd() ;
				sum+=cneigh.get().get() ;
			}
			sum/=(size*size) ;
			c.get().set(sum) ;
		}
	}


	/**
	 * Question 1.3
	 */
	public static void meanFilterWithNeighborhood(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, int size) {
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, size/2, size/2 );
		//final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorSingle(input, size/2, size/2 );
		final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, (size-1)/2, (size-1)/2) ;
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorDouble(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendMirrorSingle(input);
		//final ExtendedRandomAccessibleInterval<UnsignedByteType, Img<UnsignedByteType>> expandedView = Views.extendZero(input);

		Interval interval = Intervals.expand(expandedView, -(size-1)/2) ; // we suppress the pixels which are outside
		final IntervalView<UnsignedByteType> view = Views.interval(expandedView, interval) ; //we reduct the view at the real size of the img
		final RandomAccess<UnsignedByteType> rOutput = output.randomAccess();

		final RectangleShape neighborhood = new RectangleShape( (size-1)/2, false );
		for(final Neighborhood<UnsignedByteType> localNeighborood : neighborhood.neighborhoods(view)){
			rOutput.setPosition(localNeighborood.getIntPosition(0), 0); rOutput.setPosition(localNeighborood.getIntPosition(1), 1);
			int sum = 0 ;
			for(final UnsignedByteType val : localNeighborood){
				sum+=val.get() ;
			}
			rOutput.get().set(sum/(size*size)) ;
		}


	}

	/**
	 * Question 2.1
	 */
	public static void convolutionLut(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int[][] kernel) {
				final int kWidth = kernel[0].length, kHeight = kernel.length; //getting the size

				final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, kWidth/2, kHeight/2) ;
				final Cursor<UnsignedByteType> cOutput = output.cursor();

				int [][][] lookUpTable = new int[kWidth][kHeight][256] ; // this look up table correspond to the value gived to a pixel according to the position on the filter
				// lut[i][j][k] correspond to a pixel of value k on the line i of the filter and the column j of the filter

				int totalCoefficient = 0 ; // store the sum of the kernel for dividing the pixels
				for(int x = 0; x < kWidth ;x++){
					for(int y = 0; y < kHeight ;y++){
						int value = kernel[y][x] ;
						totalCoefficient += value ;
						for(int p = 0; p < 256; p++){
							lookUpTable[x][y][p] = value * p ;
						}
					}
				}
				totalCoefficient = (totalCoefficient == 0 ? 1 : totalCoefficient) ; // avoid a dvision by 0

				while(cOutput.hasNext()){
					cOutput.fwd() ;
					int x = cOutput.getIntPosition(0), y = cOutput.getIntPosition(1) ;
					RandomAccessibleInterval< UnsignedByteType > interval =  Views.interval(expandedView, new long[] {x-kWidth/2, y-kHeight/2}, new long[] {x+kWidth/2, y+kHeight/2, 2}) ;
					int sum = 0, cursorValue = 0 ;
					final Cursor< UnsignedByteType > cneigh = Views.iterable( interval ).cursor();
					// le sens de parcours d'un cursor est x,y ; x+1,y, x+2,y ; ... ; x,y+1, ... x+n,y+n
					while(cneigh.hasNext()){
						cneigh.fwd() ;
						sum+= lookUpTable[cursorValue%kWidth][cursorValue/kWidth][cneigh.get().get()] ;
						cursorValue++ ;
					}
					cOutput.get().set(sum/totalCoefficient) ;
				}
	}

	public static void convolutionLutImproved(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int[][] kernel) {
				final int kWidth = kernel[0].length, kHeight = kernel.length; //getting the size

				final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, kWidth/2, kHeight/2) ;
				final RandomAccess<UnsignedByteType> rInput = expandedView.randomAccess() ;
				final Cursor<UnsignedByteType> cOutput = output.cursor();

				int [][][] lookUpTable = new int[kWidth][kHeight][256] ; // this look up table correspond to the value gived to a pixel according to the position on the filter
				// lut[i][j][k] correspond to a pixel of value k on the line i of the filter and the column j of the filter

				int totalCoefficient = 0 ; // store the sum of the kernel for dividing the pixels
				for(int x = 0; x < kWidth ;x++){
					for(int y = 0; y < kHeight ;y++){
						int value = kernel[y][x] ;
						totalCoefficient += value ;
						for(int p = 0; p < 256; p++){
							lookUpTable[x][y][p] = value * p ;
						}
					}
				}
				totalCoefficient = (totalCoefficient == 0 ? 1 : totalCoefficient) ; // avoid a dvision by 0

				while(cOutput.hasNext()){
					cOutput.fwd() ;
					int x = cOutput.getIntPosition(0), y = cOutput.getIntPosition(1) ;
					int sum = 0 ;
					for(int i = -kWidth/2 ; i <= kWidth/2; i++){
						for(int j = -kHeight/2 ; j <= kHeight/2; j++){
							rInput.setPosition(x+i, 0);
							rInput.setPosition(y+j, 1);
							sum+= lookUpTable[kWidth/2 + i][kHeight/2+j][rInput.get().get()] ;
						}
					}
					cOutput.get().set(sum/totalCoefficient) ;
				}
	}

	public static void convolutionLutColored(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,	int[][] kernel) {
		final int kWidth = kernel[0].length, kHeight = kernel.length; //getting the size
		int [][][] lookUpTable = new int[kWidth][kHeight][256] ; // this look up table correspond to the value gived to a pixel according to the position on the filter

		int totalCoefficient = 0 ; // store the sum of the kernel for dividing the pixels
		for(int x = 0; x < kWidth ;x++){
			for(int y = 0; y < kHeight ;y++){
				int value = kernel[y][x] ;
				totalCoefficient += value ;
				for(int p = 0; p < 256; p++){
					lookUpTable[x][y][p] = value * p ;
				}
			}
		}
		totalCoefficient = (totalCoefficient == 0 ? 1 : totalCoefficient) ; // avoid a dvision by 0

		final RandomAccess<UnsignedByteType> rOutput = output.randomAccess() ;


		final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, kWidth/2, kHeight/2, 0) ; // O corresponds to the color dimnesion
		final RandomAccess<UnsignedByteType> rInput = expandedView.randomAccess() ;

		// lut[i][j][k] correspond to a pixel of value k on the line i of the filter and the column j of the filter


		for(int x = 0 ; x <= input.max(0) ; x++ ){
			for(int y = 0; y <= input.max(1) ; y++){
				for(int col = 0; col <= input.max(2); col++){
					int sum = 0 ;
					for(int i = -kWidth/2 ; i <= kWidth/2; i++){
						for(int j = -kHeight/2 ; j <= kHeight/2; j++){
							rInput.setPosition(x+i, 0);
							rInput.setPosition(y+j, 1);
							rInput.setPosition(col, 2);
							sum += lookUpTable[kWidth/2 + i][kHeight/2+j][rInput.get().get()] ;
						}
					}
					rOutput.setPosition(x, 0);
					rOutput.setPosition(y, 1);
					rOutput.setPosition(col, 2);
					rOutput.get().set(sum / totalCoefficient) ;
 				}
			}
		}
	}

	public static void sobelFilter(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output){
		final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, 1, 1) ; // the size of the filter (1) ; expanding on zero to detect the contours of the img
		final RandomAccess<UnsignedByteType> rInput = expandedView.randomAccess() ;
		final Cursor<UnsignedByteType> cOutput = output.cursor();

		int [] [] grad = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}} ;

		while(cOutput.hasNext()){
			cOutput.fwd() ;
			int x = cOutput.getIntPosition(0), y = cOutput.getIntPosition(1) ;
			int sumX = 0, sumY = 0 ;
			for(int i = -1 ; i <= 1; i++){
				for(int j = -1 ; j <= 1; j++){
					rInput.setPosition(x+i, 0);
					rInput.setPosition(y+j, 1);
					sumX+= rInput.get().get() * grad[i+1][j+1] ;
					sumY+=rInput.get().get() * grad[j+1][i+1] ;
				}
			}
			cOutput.get().set((int) Math.sqrt (sumX*sumX + sumY*sumY)) ;
		}
	}

	public static void cannyFilter(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output){
		final Img<UnsignedByteType> inputWithoutNoise = input.copy() ;
		gaussFilterImgLib(1.4, input, inputWithoutNoise) ; // reduction du bruit

		final IntervalView<UnsignedByteType> expandedView = Views.expandZero(inputWithoutNoise, 1, 0) ; // the size of the filter (1) ; expanding on zero to detect the contours of the img
		final RandomAccess<UnsignedByteType> rInput = expandedView.randomAccess() ;
		final Cursor<UnsignedByteType> cOutput = output.cursor();

		while(cOutput.hasNext()){
			cOutput.fwd() ;
			int x = cOutput.getIntPosition(0), y = cOutput.getIntPosition(1) ;
			int sumX = 0, sumY = 0 ;
			for(int i = -1 ; i <= 1; i++){
				rInput.setPosition(x+i, 0);
				rInput.setPosition(y, 1);
				sumX+= rInput.get().get() * i ;

				rInput.setPosition(x, 0);
				rInput.setPosition(y+i, 1);
				sumY+= rInput.get().get() * i ;
			}
			cOutput.get().set((int) Math.sqrt (sumX*sumX + sumY*sumY)) ;
		}

	}



	public static void convolutionNotLut(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int[][] kernel) {
				final int kWidth = kernel[0].length, kHeight = kernel.length; //getting the size

				final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, kWidth/2, kHeight/2) ;
				final RandomAccess<UnsignedByteType> rInput = expandedView.randomAccess() ;
				final Cursor<UnsignedByteType> cOutput = output.cursor();

				// lut[i][j][k] correspond to a pixel of value k on the line i of the filter and the column j of the filter

				int totalCoefficient = 0 ; // store the sum of the kernel for dividing the pixels
				for(int x = 0; x < kWidth ;x++){
					for(int y = 0; y < kHeight ;y++){
						int value = kernel[y][x] ;
						totalCoefficient += value ;
					}
				}
				totalCoefficient = (totalCoefficient == 0 ? 1 : totalCoefficient) ; // avoid a dvision by 0
				while(cOutput.hasNext()){
					cOutput.fwd() ;
					int x = cOutput.getIntPosition(0), y = cOutput.getIntPosition(1) ;
					int sum = 0 ;
					for(int i = -kWidth/2 ; i <= kWidth/2; i++){
						for(int j = -kHeight/2 ; j <= kHeight/2; j++){
							rInput.setPosition(x+i, 0);
							rInput.setPosition(y+j, 1);
							sum+= rInput.get().get()*kernel[j+kHeight/2][i+kWidth/2] ;
						}
					}
					cOutput.get().set(sum/totalCoefficient) ;
				}
	}

	/**
	 * Question 2.3
	 */
	public static void gaussFilterImgLib(double value, final Img<UnsignedByteType> input, final Img<UnsignedByteType> output) {
		// The size of the kernel is 2*3*value +1, we have to expend the value size.
		int filterSize = Math.max( 2, ( int ) ( 3 * value + 0.5 ) + 1 ) ;
		final IntervalView<UnsignedByteType> expandedView = Views.expandZero(input, filterSize, filterSize) ;

		Interval interval = Intervals.expand(expandedView, - filterSize) ; // we suppress the pixels which are outside
		final IntervalView<UnsignedByteType> view = Views.interval(expandedView, interval) ; //we reduct the view at the real size of the img

		Gauss3.gauss(value, view, output) ;

	}
}
