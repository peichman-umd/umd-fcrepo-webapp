package edu.umd.lib.fcrepo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String remoteUser = request.getRemoteUser();
      log.debug("Logging out: " + remoteUser);
      
      request.getSession().invalidate();
      // Redirect to CAS logout to destroy CAS session
      response.sendRedirect("https://shib.idm.umd.edu/shibboleth-idp/profile/cas/logout");
    }
}