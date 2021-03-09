package pdl.backend;

//class used to transfer the informations corresponding to an image without getting all the data of the images
public class ImageInfos {
  private final Long id, width, height;
  private final String name;
  public ImageInfos(final String name, final Long id, final Long width, final Long height) {
    this.id = id ;
    this.name = name;
    this.width = width ;
    this.height = height ;
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
}
