package pdl.backend;
import org.springframework.http.MediaType;

//class used to transfer the informations corresponding to an image without getting all the data of the images
public class ImageInfos {
  private final Long id, width, height;
  private final String name;
  private final MediaType type ;
  public ImageInfos(final String name, final Long id, final Long width, final Long height, final MediaType type) {
    this.id = id ;
    this.name = name;
    this.width = width ;
    this.height = height ;
    this.type = type ;
  }

  public long getId() {
    return id;
  }

  public long getWidth() {
    return width;
  }

  public long getHeight() {
    return height;
  }

  public String getName() {
    return name;
  }
  public MediaType getType() {
    return type;
  }
}
