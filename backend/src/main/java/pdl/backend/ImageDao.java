package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.springframework.util.MimeType ;

import java.io.File;
import org.springframework.http.MediaType;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import pdl.lib.GrayLevelProcessing;

import net.imglib2.img.array.ArrayImgFactory;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import io.scif.img.ImgIOException;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public static void getAllFiles(File file, ArrayList<File> list) {
    if (file.isFile()) {
      list.add(file) ;
      return ;
    }
    for(File f: file.listFiles()) {
      getAllFiles(f, list);
    }
  }

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    // on va charger le dossier images
    final ClassPathResource imgFold = new ClassPathResource("images");
    if (!imgFold.exists() ){
      // le dossier n'existe pas
      throw new RuntimeException("Le dossier images n'existe pas : " ) ;
    }
    try {
        File imgDir = imgFold.getFile() ;
        ArrayList<File> files =  new ArrayList<File>() ;
        getAllFiles(imgDir, files);
        for(File f: files) {
          if ( Files.probeContentType(f.toPath()).equals("image/jpeg") || Files.probeContentType(f.toPath()).equals("image/tiff") ){
            BufferedImage img = ImageIO.read(f) ;
            long nbSlices = (img.getType() == BufferedImage.TYPE_BYTE_GRAY || img.getType() == BufferedImage.TYPE_USHORT_GRAY ) ? 1 : 3 ;
            long height = img.getHeight(), width = img.getWidth() ;
            byte [] fileContent = Files.readAllBytes(f.toPath()) ;
            MimeType type = MimeType.valueOf(Files.probeContentType(f.toPath())) ;
            Image image = new Image(f.getName(), fileContent, width, height, nbSlices, new MediaType(type.getType(), type.getSubtype())) ;
            create(image) ;
          }
        }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    Image back = images.get(id) ;
    if (back == null)
      return Optional.empty();
    return Optional.of(back) ;
  }

  @Override
  public List<Image> retrieveAll() {
    final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
		final ImgOpener imgOpener = new ImgOpener();
    final ClassPathResource imgFile = new ClassPathResource("test1.jpg");
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img) ;
  }

  @Override
  public void update(final Image img, final String[] params) {
    // not used
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId()) ;
  }
}
