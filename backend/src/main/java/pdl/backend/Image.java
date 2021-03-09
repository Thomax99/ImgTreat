package pdl.backend;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id, height, width;
  private String name;
  private byte[] data;

  public Image(final String name, final byte[] data, Long width, Long height) {
    id = count++;
    this.name = name;
    this.data = data;
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

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }
}
