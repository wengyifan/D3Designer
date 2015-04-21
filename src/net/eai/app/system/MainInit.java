package net.eai.app.system;

import javax.servlet.http.HttpServlet;

import net.eai.util.memcached.*;

public class MainInit extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init()
    {
    	System.out.println("-----------EAIDEV start to init---------------");
    	String quorum = getServletContext().getInitParameter("quorum");
    	String clientport = getServletContext().getInitParameter("clientport");
    	System.out.println("quorum is :"+quorum);
    	System.out.println("clientport is :"+clientport);
    	
    	String memservers = getServletContext().getInitParameter("memservers");
    	MemCacheConstants.memservers = memservers;
    	System.out.println("-----------init succesful---------------");
    }
	

}
