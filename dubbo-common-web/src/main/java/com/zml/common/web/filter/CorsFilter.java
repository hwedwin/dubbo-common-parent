package com.zml.common.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * CORS全称为Cross Origin Resource Sharing（跨域资源共享），服务端只需添加相关响应头信息，即可实现客户端发出AJAX跨域请求。<br/>
 * CorsFilter将从web.xml中读取相关Filter初始化参数，并将在处理HTTP请求时将这些参数写入对应的CORS响应头中，下面大致描述一下这些CORS响应头的意义：
 * 1.Access-Control-Allow-Origin：允许访问的客户端域名，例如：http://web.xxx.com，若为*，则表示从任意域都能访问，即不做任何限制。
 * 2.Access-Control-Allow-Methods：允许访问的方法名，多个方法名用逗号分割，例如：GET,POST,PUT,DELETE,OPTIONS。
 * 3.Access-Control-Allow-Credentials：是否允许请求带有验证信息，若要获取客户端域下的cookie时，需要将其设置为true。给一个带有withCredentials的请求发送响应的时候,服务器端必须指定允许请求的域名,不能使用'*'.
 * 4.Access-Control-Allow-Headers：允许服务端访问的客户端请求头，多个请求头用逗号分割，例如：Content-Type。
 * 5.Access-Control-Expose-Headers：允许客户端访问的服务端响应头，多个响应头用逗号分割。
 * 6.Access-Control-Max-Age：预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
 * 
 * 注：CORS规范中定义Access-Control-Allow-Origin只允许两种取值，要么为*，要么为具体的域名，也就是说，不支持同时配置多个域名。
 * 为了解决跨多个域的问题，需要在代码中做一些处理，这里将Filter初始化参数(allowOrigin)作为一个域名的集合（用逗号分隔），
 * 只需从当前请求中获取Origin请求头，就知道是从哪个域中发出的请求，若该请求在以上允许的域名集合中，
 * 则将其放入Access-Control-Allow-Origin响应头，这样跨多个域的问题就轻松解决了。
 *
 * 我们都知道，在发同域请求时，浏览器会将cookie自动加在request header中。但大家是否遇到过这样的场景：在发送跨域请求时，cookie并没有自动加在request header中。
 * 造成这个问题的原因是：在CORS标准中做了规定，默认情况下，浏览器在发送跨域请求时，不能发送任何认证信息（credentials）如"cookies"和"HTTP authentication schemes"。除非xhr.withCredentials为true（xhr对象有一个属性叫withCredentials，默认值为false）。
 * 所以根本原因是cookies也是一种认证信息，在跨域请求中，client端必须手动设置xhr.withCredentials=true，且server端也必须允许request能携带认证信息（即response header中包含Access-Control-Allow-Credentials:true），这样浏览器才会自动将cookie加在request header中。
 * 另外，要特别注意一点，一旦跨域request能够携带认证信息，server端一定不能将Access-Control-Allow-Origin设置为*，而必须设置为请求页面的域名。
 * 
 * spring mvc 从4.2版本开始增加了对CORS的支持 <mvc:cors> or @CrossOrigin, 所以打算采用
 *
 * @Description
 * @author zhaomingliang
 * @date 2017年3月9日
 */
public class CorsFilter implements Filter {

	private String allowOrigin;
    private String allowMethods;
    private String allowCredentials;
    private String allowHeaders;
    private String exposeHeaders;
    private String maxAge;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.allowOrigin = filterConfig.getInitParameter("allowOrigin");
		this.allowMethods = filterConfig.getInitParameter("allowMethods");
		this.allowCredentials = filterConfig.getInitParameter("allowCredentials");
		this.allowHeaders = filterConfig.getInitParameter("allowHeaders");
		this.exposeHeaders = filterConfig.getInitParameter("exposeHeaders");
		this.maxAge = filterConfig.getInitParameter("maxAge");
		
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (StringUtils.isNotEmpty(allowOrigin)) {
            List<String> allowOriginList = Arrays.asList(allowOrigin.split(","));
            if (CollectionUtils.isNotEmpty(allowOriginList)) {
                String currentOrigin = request.getHeader("Origin");
                if (allowOriginList.contains(currentOrigin)) {
                    response.setHeader("Access-Control-Allow-Origin", currentOrigin);
                }
            }
        }
        if (StringUtils.isNotEmpty(this.allowMethods)) {
            response.setHeader("Access-Control-Allow-Methods", this.allowMethods);
        }
        if (StringUtils.isNotEmpty(this.allowCredentials)) {
            response.setHeader("Access-Control-Allow-Credentials", this.allowCredentials);
        }
        if (StringUtils.isNotEmpty(this.allowHeaders)) {
            response.setHeader("Access-Control-Allow-Headers", this.allowHeaders);
        }
        if (StringUtils.isNotEmpty(this.exposeHeaders)) {
            response.setHeader("Access-Control-Expose-Headers", this.exposeHeaders);
        }
        if (StringUtils.isNotEmpty(this.maxAge)) {
        	response.setHeader("Access-Control-Max-Age", this.maxAge);
        }
        chain.doFilter(req, res);
	}
	
	@Override
	public void destroy() {
		
	}
	
}
