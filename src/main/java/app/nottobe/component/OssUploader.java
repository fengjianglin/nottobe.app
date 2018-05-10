package app.nottobe.component;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;

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

	private OSSClient getClient() {
		ClientConfiguration conf = new ClientConfiguration();
		conf.setSupportCname(false);
		conf.setMaxErrorRetry(5);
		return new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);
	}
}
