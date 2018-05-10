package app.nottobe;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import app.nottobe.bean.User;
import app.nottobe.component.SessionService;

@Component
@ServletComponentScan
@WebFilter(filterName = "sessionFilter", urlPatterns = "/*")
public class SessionFilter implements Filter {

	private static Logger logger = Logger.getLogger(SessionFilter.class);

	@Autowired
	private SessionService sessionService;

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		logger.debug("DoFilter Start");

		HttpServletRequest req = (HttpServletRequest) request;

		String sessionId = req.getHeader("SessionId");
		if (StringUtils.isEmpty(sessionId)) {
			sessionId = req.getParameter("SessionId");
		}

		logger.debug("DoFilter SessionId: " + sessionId);

		if (StringUtils.hasText(sessionId)) {
			User user = sessionService.find(sessionId);
			if (user != null) {
				sessionService.save(sessionId, user);
				req.setAttribute("user", user);
				req.setAttribute("sessionId", sessionId);
			}
		}

		chain.doFilter(request, response);

		logger.debug("DoFilter End");
	}

	public void destroy() {

	}

}
