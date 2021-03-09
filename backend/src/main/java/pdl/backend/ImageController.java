package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(back.get().getData()) ;
  }
  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    imageDao.delete(back.get());
    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).build() ;
  }
  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
      if (!file.getContentType().equals("image/jpeg")){
        System.err.println("pas jpeg ... : "+file.getContentType()) ;
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build() ;
      }
      if (file.isEmpty()){
        System.err.println("Fichier vide ...") ;
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build() ;
      }
      try{
        BufferedImage image = ImageIO.read(file.getInputStream());
        long height = (long) image.getHeight();
        long width = (long) image.getWidth();
        Image img = new Image(file.getResource().getFilename(), file.getBytes(), width, height) ;
        imageDao.create(img);
      }catch (IOException e){
        System.err.println("Erreur de lecture du fichier ...") ;
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build() ; 
      }
    return ResponseEntity.ok().build() ;
  }
  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    for (Image img: imageDao.retrieveAll()){
      nodes.add(mapper.valueToTree(new ImageInfos(img.getName(), img.getId(), img.getWidth(), img.getHeight()))) ;
    }
    return nodes;
  }
  @RequestMapping(value = "/images/infos/{id}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  public ResponseEntity<?> getImageInfo(@PathVariable("id") Long id) {
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    JsonNode node = mapper.valueToTree(new ImageInfos(back.get().getName(), back.get().getId(), back.get().getWidth(), back.get().getHeight())) ;
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(node);
  }
}
