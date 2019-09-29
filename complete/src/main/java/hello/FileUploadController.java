package hello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileUploadController {

	private static final String EXTENSION = ".jpg";
	private static final String SERVER_LOCATION = "upload-dir";

	private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

	@RequestMapping(path = "/download", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(@RequestParam("image") String imageName) throws IOException {
		LOG.info("/download?image=" + imageName + " is called");
		File file = new File(SERVER_LOCATION + File.separator + imageName + EXTENSION);

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processedImage.jpg");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");

		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok()
				.headers(header)
				.contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(resource);
	}

	@RequestMapping(path = "/apk", method = RequestMethod.GET)
	public ResponseEntity<Resource> apk() throws IOException {
		LOG.info("/apk is called");
		File file = new File(SERVER_LOCATION + File.separator + "prototype.apk");

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prototype.apk");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");

		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok()
				.headers(header)
				.contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(resource);
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		//		model.addAttribute("files", storageService.loadAll().map(
		//				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
		//						"serveFile", path.getFileName().toString()).build().toString())
		//				.collect(Collectors.toList()));

		LOG.info("homepage is called");

		return "index";
	}

	//	@GetMapping("/files/{filename:.+}")
	//	@ResponseBody
	//	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
	//
	//		Resource file = storageService.loadAsResource(filename);
	//		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
	//				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	//	}

	//	@PostMapping("/")
	//	public String handleFileUpload(@RequestParam("file") MultipartFile file,
	//			RedirectAttributes redirectAttributes) {
	//
	//		LOG.info("Uploading a file ");
	//
	//		storageService.store(file);
	//		redirectAttributes.addFlashAttribute("message",
	//				"You successfully uploaded " + file.getOriginalFilename() + "!");
	//
	//		return "redirect:/";
	//	}

	@RequestMapping(value = "/magic", method = RequestMethod.POST)
	public @ResponseBody String uploadImage2(@RequestParam("image_key") String imageValue,
			HttpServletRequest request,
			@RequestParam("name_key") String imageName) {
		try {

			LOG.info("/magic is called - starting image processing...");

			imageName = "blabla.jpeg";

			//This will decode the String which is encoded by using Base64 class
			byte[] imageByte = Base64.decodeBase64(imageValue);

			String directory = "upload-dir/" + imageName;

			FileOutputStream f = new FileOutputStream(directory);
			f.write(imageByte);
			f.close();

			// process image 

			//			Process p = Runtime.getRuntime().exec("python yourapp.py");
			File workingDirectory = new File("upload-dir");

			ProcessBuilder processBuilder = new ProcessBuilder().directory(workingDirectory);
			processBuilder.command("cmd.exe", "/c", "python process.py " + imageName);
			//			processBuilder.command("/bin/bash", "-c", "python process.py " + imageName);
			Process process = processBuilder.start();
			int exitCode = process.waitFor(); // returns 0 on success, 1 on failure

			LOG.info("python script returned with exit code: " + exitCode);

			return "success ";
		}
		catch (Exception e) {
			LOG.info(e.toString());
			return "error = " + e;
		}

	}

}
