package app.nottobe.component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploader {

	@Value("${server.host}")
	private String host;

	@Value("${web.static-location}")
	private String filePath;

	public String uploadFile(String filepath, MultipartFile multipartFile) throws IOException {
		File file = new File(filePath, filepath);
		multipartFile.transferTo(file);
		String url = String.format("http://%s/%s", host, filepath);
		return url;
	}

	public BufferedImage randomUserHB() throws IOException {
		String userHB = "ntb/hb";

		File file = new File(filePath, userHB);
		List<String> list = new ArrayList<>();
		for (String name : file.list()) {
			if (checkImage(name)) {
				list.add(name);
			}
		}
		int objIndex = (int) (System.currentTimeMillis() % list.size());
		File f = new File(file, list.get(objIndex));
		BufferedImage moban = ImageIO.read(f);
		return moban;
	}

	private boolean checkImage(String key) {
		return key.endsWith(".png") || key.endsWith(".PNG") || key.endsWith(".jpg") || key.endsWith(".JPG")
				|| key.endsWith(".jpeg") || key.endsWith(".JPEG");
	}
}
