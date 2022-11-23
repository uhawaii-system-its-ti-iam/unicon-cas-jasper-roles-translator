package edu.hawaii.springframework.security;

import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.jasig.cas.client.validation.Assertion;

import java.util.List;
import java.util.ArrayList;

/**
 * Populates the {@link org.springframework.security.core.GrantedAuthority}s for a user by
 * reading a list of attributes that were returned as part of the CAS response. Each
 * attribute is read and each value of the attribute is turned into a GrantedAuthority. If
 * the attribute has no value then its not added.
 *
 * This is customized for U Hawaii to translate roles incoming from CAS(sourced by Grouper) into Jasper reports
 * native role names according to the provided translation specification.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 */
public final class GrantedAuthorityFromAssertionAttributesUserDetailsService extends
        AbstractCasAssertionUserDetailsService {

    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";

    private final String[] attributes;

    private boolean convertToUpperCase = true;

    private static final String JASPER_ROLE_FORMAT = "ROLE_UH_%s_%s";

    public GrantedAuthorityFromAssertionAttributesUserDetailsService(
            final String[] attributes) {
        Assert.notNull(attributes, "attributes cannot be null.");
        Assert.isTrue(attributes.length > 0,
                "At least one attribute is required to retrieve roles from.");
        this.attributes = attributes;
    }



    /**
     * Converts the returned attribute values to uppercase values.
     *
     * @param convertToUpperCase true if it should convert, false otherwise.
     */
    public void setConvertToUpperCase(final boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        for (final String attribute : this.attributes) {
            final Object value = assertion.getPrincipal().getAttributes().get(attribute);

            if (value == null) {
                continue;
            }

            if (value instanceof List) {
                final List list = (List) value;
                for (final Object o : list) {
                    //Hawaii customization
                    translateRoleAndAddGrantedAuthorityIfNecessary(o, grantedAuthorities);
                }

            } else {
                //Hawaii customization
                translateRoleAndAddGrantedAuthorityIfNecessary(value, grantedAuthorities);
            }
        }

        //Hawaii customization
        boolean userEnabled = grantedAuthorities.size() > 0;
        return new User(assertion.getPrincipal().getName(), NON_EXISTENT_PASSWORD_VALUE,
                userEnabled, true, true, true, grantedAuthorities);
    }

    private void translateRoleAndAddGrantedAuthorityIfNecessary(Object casRole, List<GrantedAuthority> grantedAuthorities) {
        String jasperRole = translateRole(casRole.toString());
        if(jasperRole != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(jasperRole));
        }
    }

    private String translateRole(String incomingRole) {
        if(!incomingRole.startsWith("uh-jasper-")) {
            return null;
        }
        String[] tokens = incomingRole.split("-", 5);
        return String.format(JASPER_ROLE_FORMAT, tokens[2].toUpperCase(), tokens[4].toUpperCase());
    }

}
