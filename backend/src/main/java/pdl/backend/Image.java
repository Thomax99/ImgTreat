package pdl.backend;

import org.springframework.http.MediaType ;
public class Image {
  private static Long count = Long.valueOf(0);
  private Long id, height, width, nbSlices;
  private String name;
  private MediaType type ;
  private byte[] data;

  public Image(final String name, final byte[] data, Long width, Long height, Long nbSlices, MediaType type) {
    id = count++;
    this.name = name;
    this.data = data;
    this.width = width ;
    this.height = height ;
    this.type = type ;
    this.nbSlices = nbSlices ;
  }
  public long getId() {
    return id;
  }
  public MediaType getType () {
    return type ;
  }
  public long getWidth() {
    return width;
  }
  public long getHeight() {
    return height;
  }

  public long getNbSlices() {
    return nbSlices;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }
}
