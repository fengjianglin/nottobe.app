package app.nottobe.component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

@Component
public class OssUploader {

	@Value("${oss.endpoint}")
	private String endpoint;

	@Value("${oss.host}")
	private String ossHost;

	@Value("${oss.bucketName}")
	private String bucketName;

	private String accessKeyId = "LTAIpKsxlekteEON";

	private String accessKeySecret = "DOF9iSuiQapLkcUNsBLp8UvRtPKrz0";

	public String uploadFile(String filepath, InputStream in) {
		OSSClient client = getClient();
		client.putObject(bucketName, filepath, in);
		client.shutdown();
		return ossHost + "/" + filepath;
	}

	public BufferedImage randomUserHB() throws IOException {
		String userHB = "ntb/hb";
		OSSClient client = getClient();
		ObjectListing objList = client.listObjects(bucketName, userHB);
		List<OSSObjectSummary> objTagSummaries = new ArrayList<>();
		for (OSSObjectSummary summary : objList.getObjectSummaries()) {
			if (checkImage(summary.getKey())) {
				objTagSummaries.add(summary);
			}
		}
		int objIndex = (int) (System.currentTimeMillis() % objTagSummaries.size());
		OSSObject ossObj = client.getObject(bucketName, objTagSummaries.get(objIndex).getKey());
		BufferedImage moban = ImageIO.read(ossObj.getObjectContent());
		client.shutdown();
		return moban;
	}

	private OSSClient getClient() {
		ClientConfiguration conf = new ClientConfiguration();
		conf.setSupportCname(false);
		conf.setMaxErrorRetry(5);
		return new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);
	}

	private boolean checkImage(String key) {
		return key.endsWith(".png") || key.endsWith(".PNG") || key.endsWith(".jpg") || key.endsWith(".JPG")
				|| key.endsWith(".jpeg") || key.endsWith(".JPEG");
	}
}
