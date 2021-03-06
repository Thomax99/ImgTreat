package pdl.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.img.array.ArrayImgFactory;


import org.springframework.mock.web.MockMultipartFile;
import org.scijava.io.ByteArrayByteBank ;
import org.scijava.io.location.BytesLocation ;
import io.scif.img.ImgSaver ;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Test
	@Order(1)
	public void createImageShouldReturnSuccess() throws Exception {
		final ArrayImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final long[] dimensions = new long[] { 400, 320 };
		final UnsignedByteType type = new UnsignedByteType();
		final Img< UnsignedByteType > img = factory.create( dimensions, type );
		BytesLocation locBack = new BytesLocation(new ByteArrayByteBank(), "modif.jpg") ;
		ImgSaver saver = new ImgSaver() ;
		saver.saveImg(locBack, img) ;
		saver.context().dispose() ;

        byte [] fileContent = locBack.getByteBank().toByteArray() ;
		MockMultipartFile mpf = new MockMultipartFile(
			"file", 
			"hello.jpeg", 
			MediaType.IMAGE_JPEG_VALUE,
			fileContent
		  );
		  mockMvc.perform(multipart("/images").file(mpf)).andExpect(status().isOk()) ;
	}

	@Test
	@Order(2)
	public void createImageShouldReturnUnsupportedMediaType() throws Exception {
		MockMultipartFile mpf = new MockMultipartFile(
			"file", 
			"hello.txt", 
			MediaType.TEXT_PLAIN_VALUE, 
			"Hello, World!".getBytes()
		  );
		  mockMvc.perform(multipart("/images").file(mpf)).andExpect(status().isUnsupportedMediaType()) ;
	}
	@Test
	@Order(3)
	public void createImageShouldReturnUnprocessableEntity() throws Exception {
		MockMultipartFile mpf = new MockMultipartFile(
			"file", 
			"hello.txt", 
			MediaType.IMAGE_JPEG_VALUE, 
			"".getBytes()
		  );
		  mockMvc.perform(multipart("/images").file(mpf)).andExpect(status().isUnprocessableEntity()) ;
	}
	@Test
	@Order(4)
	public void getImageListShouldReturnSuccess() throws Exception {
		mockMvc.perform(get("/images")).andExpect(status().isOk()) ;
	}

	@Test
	@Order(5)
	public void getImageShouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/images/120")).andExpect(status().isNotFound()) ;
	}

	@Test
	@Order(6)
	public void getImageShouldReturnSuccess() throws Exception {
		String ind = mockMvc.perform(get("/images")).andReturn().getResponse().getContentAsString().split(",")[0].split(":")[1] ; //recup a correct indice

		mockMvc.perform(get("/images/"+ind)).andExpect(status().isOk()).andDo(print()) ;
	}

	@Test
	@Order(7)
	public void getImageInfosShouldReturnSuccess() throws Exception {
	String ind = mockMvc.perform(get("/images")).andReturn().getResponse().getContentAsString().split(",")[0].split(":")[1] ; //recup a correct indice

	mockMvc.perform(get("/images/infos/"+ind)).andExpect(status().isOk()) ;
	}
	@Test
	@Order(8)
	public void getImageInfosShouldReturnNotFound() throws Exception {
		  mockMvc.perform(get("/images/infos/120")).andExpect(status().isNotFound()) ;
	}

	@Test
	@Order(9)
	public void deleteImageShouldReturnBadRequest() throws Exception {
		// la pertinence de ce test est a mon avis limitee : spring-boot retourne ceci automatiquement ;
		// ce n'est pas nous qui decidons ce renvoi
		mockMvc.perform(delete("/images/toto")).andExpect(status().isBadRequest()) ;
	}

	@Test
	@Order(10)
	public void deleteImageShouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/images/120")).andExpect(status().isNotFound()) ;
	}

	@Test
	@Order(11)
	public void deleteImageShouldReturnSuccess() throws Exception {
		String ind = mockMvc.perform(get("/images")).andReturn().getResponse().getContentAsString().split(",")[0].split(":")[1] ; //recup a correct indice
		mockMvc.perform(delete("/images/"+ind)).andExpect(status().isOk()) ;
	}
}
