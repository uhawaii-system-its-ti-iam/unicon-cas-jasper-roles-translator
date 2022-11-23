University of Hawaii role translator from CAS to Jasper reports plugin 
=======================

This is a custom implementation of Spring Security's `GrantedAuthorityFromAssertionAttributesUserDetailsService` component which translates roles available from CAS' Assertion into Jasper report's naming convetion. These roles are then made avaible in Jasper reports authorization databse. The persisting part into Jasper reports authorization databse is performed by core Jasper reports engine components downstream from this `GrantedAuthorityFromAssertionAttributesUserDetailsService` custom implementation.

## Instalation

* Stop Tomcat instance where Jasper reports exploded web archive is deployed
* Copy `dist/cas-jasper-roles-translator-1.0.jar` to `$TOMCAT_HOME/webapps/{jasper-reports-directory}/WEB-INF/lib`
* Copy `dist/applicationContext-externalAuth-CAS-CustomRolesTranslator.xml` to `$TOMCAT_HOME/webapps/{jasper-reports-directory}/WEB-INF`
* Start Tomcat instance

## Build from source

If there is a need to modify source code, one could build it using Gradle. To do so, execute `./gradlew clean build` and the `cas-jasper-roles-translator-1.0.jar` will be available in `build/libs` directory. Then replace `dist/cas-jasper-roles-translator-1.0.jar` with this newly built jar just to make sure next time deploying the extention the latest version in the `dist` directory is used.



