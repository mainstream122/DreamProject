package com.ktds.interceptors;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ktds.logger.KafkaLogger;
import com.ktds.member.vo.MemberVO;

public class Interceptor extends HandlerInterceptorAdapter {

	private Map<String, Object> requestInfo = new HashMap<>();

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//"ktds§192.168.162.67§#start#§/member/signUp§ § §GET§#end#\n"
		
		
		requestInfo.put("serverName", "ktds");
		requestInfo.put("serverIp", "192.168.201.20");
		requestInfo.put("requestTime", Calendar.getInstance());
		requestInfo.put("requestPage", request.getRequestURI());
		requestInfo.put("requestParams", request.getParameterMap());
		requestInfo.put("requestMethod", request.getMethod());

		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		Calendar requestCal = (Calendar) requestInfo.get("requestTime");
		String requestDateTime = 
			String.format("%s%s%s%s%s%s",
				requestCal.get(Calendar.YEAR),
				requestCal.get(Calendar.MONTH) + 1 < 10 ? "0" + requestCal.get(Calendar.MONTH) + 1 : requestCal.get(Calendar.MONTH) + 1,
				requestCal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + requestCal.get(Calendar.DAY_OF_MONTH) : requestCal.get(Calendar.DAY_OF_MONTH),
				requestCal.get(Calendar.HOUR) < 10 ? "0" + requestCal.get(Calendar.HOUR) : requestCal.get(Calendar.HOUR),
				requestCal.get(Calendar.MINUTE) < 10 ? "0" + requestCal.get(Calendar.MINUTE) : requestCal.get(Calendar.MINUTE),
				requestCal.get(Calendar.SECOND) < 10 ? "0" + requestCal.get(Calendar.SECOND) : requestCal.get(Calendar.SECOND));

		Calendar responseCal = Calendar.getInstance();
		String responseDateTime = 
			String.format("%s%s%s%s%s%s",
				responseCal.get(Calendar.YEAR),
				responseCal.get(Calendar.MONTH) + 1 < 10 ? "0" + responseCal.get(Calendar.MONTH) + 1 : responseCal.get(Calendar.MONTH) + 1,
				responseCal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + responseCal.get(Calendar.DAY_OF_MONTH) : responseCal.get(Calendar.DAY_OF_MONTH),
				responseCal.get(Calendar.HOUR) < 10 ? "0" + responseCal.get(Calendar.HOUR) : responseCal.get(Calendar.HOUR),
				responseCal.get(Calendar.MINUTE) < 10 ? "0" + responseCal.get(Calendar.MINUTE) : responseCal.get(Calendar.MINUTE),
				responseCal.get(Calendar.SECOND) < 10 ? "0" + responseCal.get(Calendar.SECOND) : responseCal.get(Calendar.SECOND));
		Map<String, String[]> paramMap = (Map<String, String[]>) requestInfo.get("requestParams");
		Iterator<String> keys = paramMap.keySet().iterator();
		String param = "";
		while(keys.hasNext()) {
			String key = keys.next();
			param += "&" + key + "=" + paramMap.get(key)[0];
		}

		String logMessage = String.format("%s§%s§%s§%s§%s§%s§%s§%s§%s",
			requestInfo.get("serverName").toString(),
			requestInfo.get("serverIp").toString(),
			requestDateTime,
			requestInfo.get("requestPage").toString(),
			" ", // 접근 Action
			param,
			requestInfo.get("requestMethod").toString(),
			responseDateTime,
			request.getSession().getAttribute("_USER_") != null ? 
					((MemberVO)request.getSession().getAttribute("_USER_")).getMemberId() : "no-login"
			);
		KafkaLogger logger = new KafkaLogger(Interceptor.class);
		logger.info("access", logMessage);

		super.postHandle(request, response, handler, modelAndView);
	}
}