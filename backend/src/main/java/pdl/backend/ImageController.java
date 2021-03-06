package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;

import org.springframework.core.io.ClassPathResource;


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

import pdl.lib.EntryPointImageProcessing;
import pdl.lib.GrayLevelProcessing;
import pdl.lib.exception.NonExistingAlgorithmException;
import pdl.lib.exception.NonExistingParameterException;

import org.springframework.util.MimeType ;
@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getImage(@PathVariable("id") long id, @RequestParam(value = "algorithm", required = false) String algorithm, 
  @RequestParam(value = "p1", required = false) String param1, @RequestParam(value = "p2", required = false) String param2) {
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    if (algorithm == null) {
      // pas d'algo : requete get simple
      return ResponseEntity.ok().contentType(back.get().getType()).body(back.get().getData()) ;
    }
    byte[] backData = null ;
    try {
      backData =  EntryPointImageProcessing.algorithmEntryPoint(back.get(), new String [] {algorithm, param1, param2}) ;
    } catch (InvalidAlgorithmParameterException e) {
      return ResponseEntity.badRequest().build() ;
    } catch (NonExistingAlgorithmException e) {
      return ResponseEntity.badRequest().build() ;
    } catch (NonExistingParameterException e) {
      return ResponseEntity.badRequest().build() ;
    }
    return ResponseEntity.ok().contentType(back.get().getType()).body(backData) ;
  }
  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    System.err.println(id) ;
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    imageDao.delete(back.get());
    return ResponseEntity.ok().build() ;
  }
  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
      if (! (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/tiff"))){
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
        long nbSlices = (image.getType() == BufferedImage.TYPE_BYTE_GRAY || image.getType() == BufferedImage.TYPE_USHORT_GRAY ) ? 1 : 3 ;
        MimeType type = MimeType.valueOf(file.getContentType()) ;
        Image img = new Image(file.getResource().getFilename(), file.getBytes(), width, height, nbSlices, new MediaType(type.getType(), type.getSubtype())) ;
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
      nodes.add(mapper.valueToTree(new ImageInfos(img.getName(), img.getId(), img.getWidth(), img.getHeight(), img.getNbSlices(), img.getType()))) ;
    }
    return nodes;
  }
  @RequestMapping(value = "/images/infos/{id}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  public ResponseEntity<?> getImageInfo(@PathVariable("id") Long id) {
    Optional<Image> back = imageDao.retrieve(id) ;
    if (back.isEmpty())
      return ResponseEntity.notFound().build() ;
    JsonNode node = mapper.valueToTree(new ImageInfos(back.get().getName(), back.get().getId(), back.get().getWidth(), back.get().getHeight(), back.get().getNbSlices(), back.get().getType())) ;
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(node);
  }
    /*
    try {
      File file = imgFile.getFile() ;
      final Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(file.getAbsolutePath(), factory).get(0);
      imgOpener.context().dispose();
      GrayLevelProcessing.changeLuminosityCursorClassic(input, 50);
      File path = new File (file.getParent() + "/vmodif.jpg") ;
      // clear the file if it already exists.
      if (path.exists()) {
        path.delete();
      }
      ImgSaver imgSaver = new ImgSaver();
      imgSaver.saveImg(path.getAbsolutePath(), input);
      imgSaver.context().dispose();
      System.out.println("okok ; enregistr?? en " + path.getAbsolutePath()) ;
    } catch (IOException e) {
      System.err.println("error") ;
    }
    */
}
