package edu.umd.lib.fcrepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthnServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AuthnServlet.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // show a basic userinfo page
        // taken from https://github.com/cas-projects/cas-sample-java-webapp/blob/master/src/main/webapp/index.jsp
        request.getRequestDispatcher("/WEB-INF/jsp/userinfo.jsp").forward(request, response);
    }

}
