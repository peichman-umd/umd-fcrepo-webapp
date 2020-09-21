package edu.umd.lib.fcrepo;

import org.jasig.cas.client.authentication.AttributePrincipal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FedoraRolesFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO: this is probably where we would define a mapping of Grouper DNs to role strings
    }

    /**
     * If the user is authenticated, use their Grouper groups to determine which Fedora roles to
     * place them in.
     *
     * @param request the current request object
     * @param response the current response object
     * @param chain the current filter chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getUserPrincipal() != null) {
            // TODO: how to get the memberOf attribute and set the fedoraAdmin or fedoraUser role
            // TODO: is there a different filter in web.xml we can use to get them as attributes?
            // TODO: or do an LDAP query with an authDN to get them that way?
            AttributePrincipal principal = (AttributePrincipal) httpRequest.getUserPrincipal();

            // wrap the request so that it will answer "true" for the correct roles
            // XXX: for prototype purposes, always set the role to "fedoraAdmin"
            chain.doFilter(new ProvideRoleRequestWrapper(httpRequest, "fedoraAdmin"), response);
        } else {
            chain.doFilter(httpRequest, response);
        }
    }

    @Override
    public void destroy() {

    }
}
