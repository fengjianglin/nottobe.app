package app.nottobe.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class Index {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/mp")
	@ResponseBody
	public String mp(String signature, String timestamp, String nonce, String echostr) {
		return echostr;
	}

	@GetMapping("/s")
	public String s() {
		return "s";
	}
}
