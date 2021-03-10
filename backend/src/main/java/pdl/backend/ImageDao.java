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
import java.io.File;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import pdl.lib.GrayLevelProcessing;
@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    final ClassPathResource imgFile = new ClassPathResource("test1.jpg");
    try {
      BufferedImage image = ImageIO.read(imgFile.getFile());
      long height = (long) image.getHeight();
      long width = (long) image.getWidth();
      byte [] fileContent = Files.readAllBytes(imgFile.getFile().toPath());
      Image img = new Image("logo.jpg", fileContent, width, height);
      images.put(img.getId(), img);
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
    GrayLevelProcessing.toto();
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img) ;
  }

  @Override
  public Optional<Image> update(final Image img, final String[] params) {
    // when updating, we recreate an image according to params
    // we accept a param recreate for the moment and that is all
    if (params.length == 0 || !params[0].equals("recreate")){
      return Optional.empty() ;
    }
    Image copy = new Image(img.getName(), img.getData(), img.getWidth(), img.getHeight()) ;
    return Optional.of(copy) ;
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId()) ;
  }
}
