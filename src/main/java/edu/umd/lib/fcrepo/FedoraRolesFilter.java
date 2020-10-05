package edu.umd.lib.fcrepo;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchResult;

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
            System.out.println("----- FedoraRolesFilter::principal="+principal);
            String userName = principal.toString();

            String adminGroup = "cn=Application_Roles:Libraries:FCREPO:FCREPO-Administrator,ou=grouper,ou=group,dc=umd,dc=edu";
            String userGroup = "cn=Application_Roles:Libraries:FCREPO:FCREPO-User,ou=grouper,ou=group,dc=umd,dc=edu";
            try {
              ConnectionConfig connConfig = new ConnectionConfig("ldap://directory.umd.edu");
              connConfig.setUseStartTLS(true);
              connConfig.setConnectionInitializer(
                new BindConnectionInitializer(
                  "uid=libr-fedora,cn=auth,ou=ldap,dc=umd,dc=edu", new Credential("<REPLACE_WITH_FCREPO_CREDENTIAL>")));
              ConnectionFactory cf = new DefaultConnectionFactory(connConfig);
              SearchExecutor executor = new SearchExecutor();
              executor.setBaseDn("uid="+userName+",ou=people,dc=umd,dc=edu");
              SearchResult result = executor.search(cf, "objectclass=*", "memberOf").getResult();
              for (LdapEntry entry : result.getEntries()) {
                System.out.println("attributes="+entry.getAttributes());
                LdapAttribute memberOfAttr = entry.getAttribute("memberOf");
                Collection<String> memberships = memberOfAttr.getStringValues();
                if (memberships.contains(adminGroup)) {
                  System.out.println("*********** " + userName + " IS ADMIN ************");
                } else if (memberships.contains(userGroup)){
                  System.out.println("*********** " + userName + " IS USER ************");
                }
//                for(String membership: memberships) {
//                  System.out.println("membership: "+membership);
//
//                }
              }

              // wrap the request so that it will answer "true" for the correct roles
              // XXX: for prototype purposes, always set the role to "fedoraAdmin"
              chain.doFilter(new ProvideRoleRequestWrapper(httpRequest, "fedoraAdmin"), response);
            } catch (LdapException e) {
              System.out.println("----LDAPException: " + e);
              e.printStackTrace();
            }
        } else {
            chain.doFilter(httpRequest, response);
        }
    }

    @Override
    public void destroy() {

    }
}
